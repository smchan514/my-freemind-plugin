package smchan.freemind_my_plugin.mru;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import freemind.modes.MindMapNode;

/**
 * MRU nodes display.
 */
public class MRUNodesView extends JDialog {
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger
            .getLogger(MRUNodesView.class.getName());

    private static final long serialVersionUID = 1L;
    private static final Insets DEFAULT_INSETS = new Insets(5, 5, 5, 5);
    private static final int ALL_MODFIERS = KeyEvent.SHIFT_MASK | KeyEvent.CTRL_MASK | KeyEvent.META_MASK
            | KeyEvent.ALT_MASK;

    private JList<MindMapNode> _jlistMRUNodes;

    private MRUNodesModel _mruModel;

    public MRUNodesView(JFrame owner) {
        super(owner);

        LOGGER.info("Creating MRUNodesView");
        initComponents();
    }

    public void setMRUNodesModel(MRUNodesModel mruModel) {
        if (mruModel != null) {
            _jlistMRUNodes.setModel(mruModel);
        }

        _mruModel = mruModel;
    }

    private void initComponents() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle("MRU nodes");
        setMinimumSize(new Dimension(320, 240));

        getContentPane().add(createMainPanel(), BorderLayout.CENTER);
        getContentPane().add(createControlPanel(), BorderLayout.SOUTH);
    }

    private Component createControlPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc;
        JComponent comp;

        gbc = new GridBagConstraints();
        gbc.insets = DEFAULT_INSETS;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        comp = new JButton(new ClearAction());
        panel.add(comp, gbc);

        return panel;
    }

    private Component createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        _jlistMRUNodes = new JList<>();
        _jlistMRUNodes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        _jlistMRUNodes.setCellRenderer(new MindMapNodeCellRenderer());

        // Listen for user inputs
        _jlistMRUNodes.addKeyListener(new MyKeyListener());
        _jlistMRUNodes.addMouseListener(new MyMouseListener());

        panel.add(new JScrollPane(_jlistMRUNodes));

        return panel;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private class MyMouseListener extends MouseAdapter {
        public MyMouseListener() {
            // ...
        }

        @Override
        public void mouseClicked(MouseEvent evt) {
            // Accept CTRL-click or double-click
            if ((evt.getModifiers() & ALL_MODFIERS) == KeyEvent.CTRL_MASK || evt.getClickCount() == 2) {
                MindMapNode node = _jlistMRUNodes.getSelectedValue();
                if (node != null) {
                    _mruModel.selectNode(node);
                }
                evt.consume();
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private class MyKeyListener extends KeyAdapter {

        public MyKeyListener() {
            // ...
        }

        @Override
        public void keyPressed(KeyEvent evt) {
            if ((evt.getModifiers() & ALL_MODFIERS) == 0 && evt.getKeyCode() == KeyEvent.VK_ENTER) {
                MindMapNode node = _jlistMRUNodes.getSelectedValue();
                if (node != null) {
                    _mruModel.selectNode(node);
                }
                evt.consume();
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private class ClearAction extends AbstractAction {
        private static final long serialVersionUID = 1L;

        public ClearAction() {
            super("Clear");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            _mruModel.clear();
        }

    }

}
