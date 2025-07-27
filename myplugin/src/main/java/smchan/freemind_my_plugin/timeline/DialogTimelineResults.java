package smchan.freemind_my_plugin.timeline;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Main window for timeline results display
 */
class DialogTimelineResults extends JDialog {

    interface IUserActionHandler {
        void gotoNode(JDialog owner, String mapPath, String nodeId);
    }

    private static final long serialVersionUID = 1L;
    private static final Insets DEFAULT_INSETS = new Insets(5, 5, 5, 5);

    private ITimeScaleSource _timeScaleSourceOverall;
    private ITimelineModel<?> _timelineModelOverall;
    private JTimeRuler _jRulerOverall;
    private JTimeline _jTimelineOverall;

    private ITimeScaleSource _timeScaleSourceDetails;
    private ITimelineModel<NodeEvent> _timelineModelDetails;
    private JTimeRuler _jRulerDetails;
    private JTimeline _jTimelineDetails;
    private JList<NodeEvent> _jlistDetails;

    private DefaultListModel<NodeEvent> _listModel;
    private IUserActionHandler _userActionHandler;

    public DialogTimelineResults(Frame owner, ITimeScaleSource tssOverall, ITimeScaleSource tssDetails,
            ITimelineModel<?> tmOverall, ITimelineModel<NodeEvent> tmDetails, IUserActionHandler userActionHandler) {
        super(owner);

        assert tssOverall != null;
        assert tssDetails != null;
        assert tmOverall != null;
        assert tmDetails != null;
        assert userActionHandler != null;

        _timeScaleSourceOverall = tssOverall;
        _timeScaleSourceDetails = tssDetails;
        _timelineModelOverall = tmOverall;
        _timelineModelDetails = tmDetails;
        _userActionHandler = userActionHandler;

        initComponents();
    }

    public void showTimeline() {
        pack();
        this.setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        setTitle("Timeline");
        setIconImage(new ImageIcon(this.getClass().getResource("/images/FreeMindWindowIcon.png")).getImage());
        getContentPane().add(createTopPanel(), BorderLayout.NORTH);
        getContentPane().add(createMainPanel(), BorderLayout.CENTER);
        getContentPane().add(createButtonsPanel(), BorderLayout.SOUTH);

        KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getRootPane().registerKeyboardAction(new CloseAction(), keyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private Component createTopPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        JComponent comp;
        GridBagConstraints gbc;

        ///////////////////////////////////////////////////////////////
        gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.BOTH;
        comp = _jRulerOverall = new JTimeRuler();
        panel.add(comp, gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.BOTH;
        comp = _jTimelineOverall = new JTimeline();
        _jTimelineOverall.setTimelineModel(_timelineModelOverall);
        panel.add(comp, gbc);

        _jRulerOverall.setTimeScaleSource(_timeScaleSourceOverall);
        _jTimelineOverall.setTimeScaleSource(_timeScaleSourceOverall);

        _jTimelineOverall.setTimelineEventRenderer(IntValueEvent.class,
                new IntValueEventRenderer(new ColormapGeneratorInterpolated()));
        _jTimelineOverall.setTimelineEventRenderer(SelectionEvent.class, new SelectionEventRenderer());

        new DefaultTimelineMouseAdapter(_jRulerOverall, _timeScaleSourceOverall);
        new OverallTimelineMouseAdapter(_jTimelineOverall, _timeScaleSourceOverall, _timeScaleSourceDetails);

        ///////////////////////////////////////////////////////////////
        gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.BOTH;
        comp = _jRulerDetails = new JTimeRuler();
        panel.add(comp, gbc);

        gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.BOTH;
        comp = _jTimelineDetails = new JTimeline();
        _jTimelineDetails.setTimelineModel(_timelineModelDetails);
        panel.add(comp, gbc);

        _jRulerDetails.setTimeScaleSource(_timeScaleSourceDetails);
        _jTimelineDetails.setTimeScaleSource(_timeScaleSourceDetails);
        _timeScaleSourceDetails.addTimeScaleChangedListener(new ITimeScaleChangedListener() {
            @Override
            public void updateTimeScale(ITimeScale timeScale) {
                _jlistDetails.repaint();
            }
        });

        _timelineModelDetails.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                _listModel.clear();
                _listModel.addAll(_timelineModelDetails.getEvents());
            }

        });

        new DefaultTimelineMouseAdapter(_jRulerDetails, _timeScaleSourceDetails);
        new DefaultTimelineMouseAdapter(_jTimelineDetails, _timeScaleSourceDetails);

        return panel;
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc;
        JComponent comp;

        gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.BOTH;
        _listModel = new DefaultListModel<>();
        _jlistDetails = new JList<>(_listModel);
        _jlistDetails.setCellRenderer(new NodeEventListCellRenderer(_timeScaleSourceDetails));
        comp = new JScrollPane(_jlistDetails);
        panel.add(comp, gbc);

        // Handle "go to node"
        _jlistDetails.addMouseListener(new GotoNodeMouseListener());

        return panel;
    }

    private Component createButtonsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        JComponent comp;
        GridBagConstraints gbc;

        ///////////////////////
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = DEFAULT_INSETS;
        comp = new JButton(new CloseAction());
        panel.add(comp, gbc);

        return panel;
    }

    //////////////////////
    private class CloseAction extends AbstractAction {
        private static final long serialVersionUID = 1L;

        public CloseAction() {
            super("Close");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            doCloseAction();
        }

    }

    void doCloseAction() {
        setVisible(false);
        dispose();
    }

    //////////////////////
    private class GotoNodeMouseListener extends MouseAdapter {
        public GotoNodeMouseListener() {
            // ...
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                NodeEvent nodeEvent = _jlistDetails.getSelectedValue();
                doGotoNode(nodeEvent);
            }
        }
    }

    void doGotoNode(NodeEvent nodeEvent) {
        _userActionHandler.gotoNode(this, nodeEvent.getPath(), nodeEvent.getNodeId());
    }

}
