package smchan.freemind_my_plugin;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;

import javax.swing.JOptionPane;

import freemind.extensions.ExportHook;
import freemind.modes.MindMapNode;

/**
 * Put into system clip-board the URL to the currently selected node or, if no nodes are selected, the currently visible
 * mind map.
 */
public class PutMindMapNameInClipboard extends ExportHook implements ClipboardOwner
{
    public PutMindMapNameInClipboard()
    {
        // ...
    }

    @Override
    public void startupMapHook()
    {
        super.startupMapHook();

        File file = getController().getMap().getFile();
        if (file == null)
        {
            JOptionPane.showMessageDialog(null, "Unable to get mindmap file name (probably not saved yet?)", "Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try
        {
            // Put the full file name into system clipboard
            String filePath = file.getAbsolutePath();
            MindMapNode selNode = getController().getSelected();

            if (selNode != null)
            {
                // If a node is selected, convert the filePath to point to the selected node
                StringBuilder sb = new StringBuilder();
                sb.append("file:///");
                sb.append(filePath);
                sb.append('#');
                sb.append(getController().getNodeID(selNode));
                filePath = sb.toString();
            }

            StringSelection stringSelection = new StringSelection(filePath);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, this);
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(null, "Failed to open explorer " + e.getMessage(), "Error",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents)
    {
        // Nothing to do
    }

}
