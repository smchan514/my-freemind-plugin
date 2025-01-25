package freemind.modes;

import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

import freemind.modes.mindmapmode.MindMapNodeModel;

/**
 * Simple HTML converter for Teams/Outlook copy paste compatibility
 */
public class SimpleHtmlConverter {

    public static String convertHtml(List selectedNodes) {
        StringWriter sw = new StringWriter();
        sw.write("<html><body><ol>");

        Iterator<?> it = selectedNodes.iterator();
        while (it.hasNext()) {
            MindMapNodeModel node = (MindMapNodeModel) it.next();
            writeHTML(sw, node);
        }
        sw.write("</ol></body></html>");
        return sw.toString();
    }

    private static void writeHTML(StringWriter sw, MindMapNodeModel node) {
        sw.write("<li>");
        if (hasExportableLink(node)) {
            sw.write("<a href=\"");
            sw.write(node.getLink());
            sw.write("\">");
            sw.write(node.getPlainTextContent());
            sw.write("</a>");
        } else {
            sw.write(node.getPlainTextContent());
        }
        sw.write("</li>");

        sw.write("<ol>");
        Iterator<?> it = node.children.iterator();
        while (it.hasNext()) {
            MindMapNodeModel child = (MindMapNodeModel) it.next();
            writeHTML(sw, child);
        }
        sw.write("</ol>");
    }

    private static boolean hasExportableLink(MindMapNodeModel node) {
        String link = node.getLink();
        if (link == null || link.isEmpty()) {
            return false;
        }

        if (link.startsWith("file:") || link.startsWith("#")) {
            return false;
        }

        return true;
    }

}
