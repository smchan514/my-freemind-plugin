package smchan.freemind_my_plugin.adv_search;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;

import javax.swing.AbstractListModel;

class SearchResultsListModel extends AbstractListModel<SearchResult> {

    private static final long serialVersionUID = 1L;
    private static final int COL_INDEX_SCORE = 0;
    private static final int COL_INDEX_NODE_TEXT = 1;

    private final ArrayList<String> _colNames = new ArrayList<>();
    private final LinkedList<SearchResult> _results = new LinkedList<>();

    public SearchResultsListModel() {
        _colNames.add("Score");
        _colNames.add("Node Text");
    }

    public void clear() {
        int oldSize = _results.size();
        _results.clear();
        fireIntervalRemoved(this, 0, oldSize);
    }

    public void setResults(Set<SearchResult> results) {
        clear();

        _results.addAll(results);
        fireContentsChanged(this, 0, _results.size());
    }

    public SearchResult getRow(int row) {
        return _results.get(row);
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
        case COL_INDEX_SCORE:
            return String.format("%.1f", _results.get(rowIndex).getScore());

        case COL_INDEX_NODE_TEXT:
            return _results.get(rowIndex).getNodeText();

        default:
            return null;
        }
    }

    @Override
    public int getSize() {
        return _results.size();
    }

    @Override
    public SearchResult getElementAt(int index) {
        return _results.get(index);
    }

}
