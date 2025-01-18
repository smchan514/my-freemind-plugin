package smchan.freemind_my_plugin.adv_search;

import java.io.File;
import java.text.SimpleDateFormat;

import javax.swing.JList;
import javax.swing.ListModel;

import freemind.modes.MindMapNode;
import smchan.freemind_my_plugin.NodePath;

/**
 * Utility class to render tool tip text is a {@linkplain JList} of
 * {@linkplain SearchResult}s
 */
class SearchResultToolTipRenderer {

    private final JList<SearchResult> _jlist;
    private final ListModel<SearchResult> _listModel;
    private int _lastIndex = -1;
    private final SimpleDateFormat _sdf = new SimpleDateFormat("E yyyy-MM-dd HH:mm:ss Z");

    public SearchResultToolTipRenderer(JList<SearchResult> jlist) {
        assert jlist != null;

        _jlist = jlist;
        _listModel = jlist.getModel();
    }

    public void updateToolTipText(int newIndex) {
        if (newIndex == _lastIndex) {
            return;
        }

        if (newIndex >= 0) {
            SearchResult elem = _listModel.getElementAt(newIndex);
            float score = elem.getScore();
            NodePath nodePath = elem.getNodePath();
            MindMapNode node = elem.getNode();
            String createdAt = _sdf.format(node.getHistoryInformation().getCreatedAt());
            String lastModified = _sdf.format(node.getHistoryInformation().getLastModifiedAt());
            File file = node.getMap().getFile();
            String filename = (file != null) ? file.getName() : "n/a";

            StringBuilder sb = new StringBuilder();
            sb.append("<html><style>th, td {padding: 0px; margin-right: 1px;} table {margin: 0px}</style>");
            sb.append("<body><table>");
            sb.append("<tr><th align='right'>Node path:</th><td>");
            sb.append(nodePath);
            sb.append("</td></tr>");
            sb.append("<tr><th align='right'>Score:</th><td>");
            sb.append(score);
            sb.append("</td></tr>");
            sb.append("<tr><th align='right'>Created at:</th><td><tt>");
            sb.append(createdAt);
            sb.append("</tt></td></tr>");
            sb.append("<tr><th align='right'>Last modified:</th><td><tt>");
            sb.append(lastModified);
            sb.append("</tt></td></tr>");
            sb.append("<tr><th align='right'>File name:</th><td>");
            sb.append(filename);
            sb.append("</td></tr>");
            
            _jlist.setToolTipText(sb.toString());
        } else {
            // Nothing at the new index position
            _jlist.setToolTipText(null);
        }
    }
}
