package smchan.freemind_my_plugin;

import java.util.List;

import freemind.extensions.ModeControllerHookAdapter;
import freemind.modes.MindMapNode;

/**
 * Show basic statistics on selected nodes
 */
public class ShowSelectedNodesStats extends ModeControllerHookAdapter {
    public ShowSelectedNodesStats() {
        // ...
    }

    @Override
    public void startupMapHook() {
        super.startupMapHook();

        OverallStats overallStats = new OverallStats();
        List<?> list = getController().getSelecteds();
        overallStats.selectedNodes = list.size();

        for (Object object : list) {
            MindMapNode node = (MindMapNode) object;
            BranchStats nodeStats = processBranch(node);
            mergeStats(overallStats, nodeStats);
        }

        printStats(overallStats);
    }

    private void printStats(OverallStats stats) {
        StringBuilder sb = new StringBuilder();
        sb.append("- Selected node count: " + stats.selectedNodes);
        sb.append('\n');
        sb.append("- Level 1 children count: " + stats.level1ChildrenCount);
        sb.append('\n');
        sb.append("- Level 2 children count: " + stats.level2ChildrenCount);
        sb.append('\n');
        sb.append("- Total children count: " + stats.totalChildrenCount);
        sb.append('\n');
        sb.append("- Max depth: " + stats.maxDepth);
        sb.append('\n');

        SimpleTextDialog dialog = new SimpleTextDialog("Stats", sb.toString());
        dialog.setVisible(true);
    }

    private void mergeStats(OverallStats overallStats, BranchStats branchStats) {
        overallStats.level1ChildrenCount += branchStats.level1ChildrenCount;
        overallStats.level2ChildrenCount += branchStats.level2ChildrenCount;
        overallStats.totalChildrenCount += branchStats.totalChildrenCount;
        overallStats.maxDepth = Math.max(overallStats.maxDepth, branchStats.maxDepth);
    }

    private BranchStats processBranch(MindMapNode node) {
        BranchStats stats = new BranchStats();
        stats.level1ChildrenCount = node.getChildCount();
        recurseIntoNode(node, stats, 0);
        return stats;
    }

    private void recurseIntoNode(MindMapNode node, BranchStats stats, int currentDepth) {
        if (currentDepth > stats.maxDepth) {
            stats.maxDepth = currentDepth;
        }

        // Count the number of grand-children nodes from root node
        if (currentDepth == 1) {
            stats.level2ChildrenCount += node.getChildCount();
        }

        stats.totalChildrenCount += node.getChildCount();

        for (Object object : node.getChildren()) {
            MindMapNode child = (MindMapNode) object;
            recurseIntoNode(child, stats, currentDepth + 1);
        }
    }

    ///////////////////////////////////////////////
    private static class OverallStats {
        public int selectedNodes;
        public int level1ChildrenCount;
        public int level2ChildrenCount;
        public int totalChildrenCount;
        public int maxDepth;
    }

    ///////////////////////////////////////////////
    private static class BranchStats {
        public int maxDepth;
        public int level1ChildrenCount;
        public int level2ChildrenCount;
        public int totalChildrenCount;
    }

}
