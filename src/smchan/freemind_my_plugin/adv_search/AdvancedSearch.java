package smchan.freemind_my_plugin.adv_search;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Level;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import freemind.controller.MapModuleManager.MapModuleChangeObserver;
import freemind.extensions.ModeControllerHookAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.Mode;
import freemind.modes.mindmapmode.MindMapController;
import freemind.view.MapModule;
import smchan.freemind_my_plugin.NodePath;
import smchan.freemind_my_plugin.NodePathUtil;
import smchan.freemind_my_plugin.adv_search.AdvancedSearchDialog.SearchOrientation;
import smchan.freemind_my_plugin.adv_search.AdvancedSearchDialog.SearchScope;

/**
 * Advanced search with the following characteristics:
 * <OL>
 * <LI>Search by simple text matching or with regular expression
 * <LI>Search results ordered by "scores" to promote the more relevant results
 * <LI>User-specified case-sensitivity, search scope and tree walk orientation:
 * depth-first or breadth-first
 * </OL>
 */
public class AdvancedSearch extends ModeControllerHookAdapter {
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger
            .getLogger(AdvancedSearch.class.getName());

    private static AdvancedSearchResultsFrame _resultsFrame;

    public AdvancedSearch() {
        // ...
    }

    @Override
    public void startupMapHook() {
        super.startupMapHook();

        JFrame frame = getController().getFrame().getJFrame();
        AdvancedSearchDialog dlg = new AdvancedSearchDialog(frame);
        if (!dlg.showDialog(frame)) {
            // User cancelled
            return;
        }

        String searchTerm = dlg.getSearchTerm();
        searchTerm = searchTerm.trim();
        boolean isCaseSensitive = dlg.isCaseSensitive();
        boolean isRegexSearch = dlg.isRegexSearch();
        int maxResults = dlg.getMaxResults();
        SearchScope searchScope = dlg.getSearchScope();
        SearchOrientation searchOrientation = dlg.getSearchOrientation();

        try {
            performSearch(frame, searchTerm, isCaseSensitive, isRegexSearch, maxResults, searchScope,
                    searchOrientation);
        } catch (Exception e) {
            String title = "Search failed";
            JOptionPane.showMessageDialog(dlg, "<html><body><pre>" + e.getMessage(), title, JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performSearch(JFrame frame, String searchTerm, boolean isCaseSensitive, boolean isRegexSearch,
            int maxResults, SearchScope searchScope, SearchOrientation searchOrientation) {
        // Select search scope
        MindMapController mmc = (MindMapController) getController();
        MindMapNode[] nodes;
        if (searchScope == SearchScope.SearchEntireMindMap) {
            nodes = new MindMapNode[] { mmc.getRootNode() };
        } else {
            nodes = extractSelectedNodes(mmc);
        }

        // Select regex search versus simple words search
        TreeSet<SearchResult> results = new TreeSet<>();
        IMatcher matcher;
        if (isRegexSearch) {
            matcher = new RegexMatcher(searchTerm, isCaseSensitive);
        } else {
            matcher = new WordsMatcher(searchTerm, isCaseSensitive);
        }

        // Select search orientation
        ITreeWalker treeWalker;
        if (searchOrientation == SearchOrientation.BreadthFirst) {
            treeWalker = new TreeWalkerBreadthFirst(nodes);
        } else {
            treeWalker = new TreeWalkerDepthFirst(nodes);
        }

        // Perform the search!
        for (MindMapNode node : treeWalker) {
            // Compute the score for the specified node
            String text = node.getPlainTextContent();
            int score = matcher.getMatchScore(text);
            if (score > 0) {
                results.add(new SearchResult(node, score));
            }

            if (results.size() >= 2 * maxResults) {
                removeExcessResults(results, maxResults);
            }
        }

        if (results.size() >= maxResults) {
            removeExcessResults(results, maxResults);
        }

        // Pre-render serach results in HTML
        for (SearchResult res : results) {
            String text = res.getNodeText();
            StyledText st = new StyledText(text);

            // Use the same matcher to tell use where to highilght in the text
            List<MatchRange> matchRanges = matcher.getMatchRanges(text);
            for (MatchRange mr : matchRanges) {
                st.highlightRange(mr._start, mr._end);
            }

            // The rendered HTML has no closing tags so we can append text
            res.setHtml(st.toHtml() + String.format(" [%.1f]", res.getScore()));
        }

        // This is done at the first invocation of the Advanced search function
        if (_resultsFrame == null) {
            // Lazy init
            _resultsFrame = new AdvancedSearchResultsFrame();

            // Register for map close event
            mmc.getController().getMapModuleManager().addListener(new MyMapModuleManagerListener());
        }

        _resultsFrame.showSearchResults(frame, searchTerm, results);
    }

    private void removeExcessResults(TreeSet<SearchResult> results, int maxResults) {
        int i = 0;
        Iterator<SearchResult> it = results.iterator();
        while (it.hasNext()) {
            it.next();
            ++i;

            if (i > maxResults) {
                it.remove();
            }

        }
    }

    private MindMapNode[] extractSelectedNodes(MindMapController mmc) {
        @SuppressWarnings("unchecked")
        List<MindMapNode> selected = mmc.getSelecteds();
        HashSet<MindMapNode> retained = new HashSet<>();

        for (MindMapNode n0 : selected) {
            NodePath np0 = NodePathUtil.getNodePath(n0);
            boolean shouldAddNode0 = true;

            // Check if n0 overlaps with any previously retained nodes...
            Iterator<MindMapNode> it = retained.iterator();
            while (it.hasNext()) {
                MindMapNode n1 = it.next();
                NodePath np1 = NodePathUtil.getNodePath(n1);
                if (np0.isPartOf(np1)) {
                    // n0 is a child of n1, no need to retain it
                    LOGGER.log(Level.INFO, "Skipping '" + n0 + "', it is a child of '" + n1 + "'");
                    shouldAddNode0 = false;
                    break;
                } else if (np1.isPartOf(np0)) {
                    // n1 is a child of n0, remove n1 via the iterator
                    LOGGER.log(Level.INFO, "Swapping '" + n1 + "' by '" + n0 + "'");
                    it.remove();
                }
            }

            if (shouldAddNode0) {
                retained.add(n0);
            }
        }

        return retained.toArray(new MindMapNode[0]);
    }

    //////////////////////////////////////////
    private static class MyMapModuleManagerListener implements MapModuleChangeObserver {
        @Override
        public boolean isMapModuleChangeAllowed(MapModule oldMapModule, Mode oldMode, MapModule newMapModule,
                Mode newMode) {
            // Always allow change
            return true;
        }

        @Override
        public void beforeMapModuleChange(MapModule oldMapModule, Mode oldMode, MapModule newMapModule, Mode newMode) {
            // ...
        }

        @Override
        public void afterMapClose(MapModule oldMapModule, Mode oldMode) {
            // Clear search results whenever a mind map is closed to avoid stale node
            // references in search results
            _resultsFrame.clearResults();
        }

        @Override
        public void afterMapModuleChange(MapModule oldMapModule, Mode oldMode, MapModule newMapModule, Mode newMode) {
            // ...
        }

        @Override
        public void numberOfOpenMapInformation(int number, int pIndex) {
            // ...
        }
    }


}
