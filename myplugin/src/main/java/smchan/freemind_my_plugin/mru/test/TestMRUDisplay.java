package smchan.freemind_my_plugin.mru.test;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

public class TestMRUDisplay extends JFrame implements MouseListener
{
    private static final long serialVersionUID = 1L;

    public TestMRUDisplay()
    {
        initComponents();
    }

    private void initComponents()
    {
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        getContentPane().add(createMainPanel(), BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private Component createMainPanel()
    {
        JPanel panel = new JPanel(new BorderLayout());

        MMNodeListModel listModel = new MMNodeListModel();
        listModel.addElement(new MMNode("Root", "#1"));
        listModel.addElement(new MMNode("123", "#2"));
        listModel.addElement(new MMNode("456", "#3"));
        listModel.addElement(new MMNode("<html><body><p>Here is<br>a first<br>one", "#4"));
        listModel.addElement(new MMNode("<html><body><pre>Here is\nanother one\nright here", "#5"));
        JList<MMNode> jlist = new JList<>(listModel);
        jlist.setCellRenderer(new MMNodeCellRenderer());

        jlist.addMouseListener(this);

        panel.add(new JScrollPane(jlist));

        return panel;
    }

    public static void main(String[] args)
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e)
        {

        }

        TestMRUDisplay appl = new TestMRUDisplay();
        appl.doStuff();
    }

    private void doStuff()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        JList<?> list = (JList<?>) e.getSource();
        int idx = list.getSelectedIndex();
        MMNodeListModel model = (MMNodeListModel) list.getModel();
        model.putNode(model.get(idx));
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e)
    {
        // TODO Auto-generated method stub

    }

}
