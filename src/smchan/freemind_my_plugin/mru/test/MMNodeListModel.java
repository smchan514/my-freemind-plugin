package smchan.freemind_my_plugin.mru.test;

import javax.swing.DefaultListModel;

public class MMNodeListModel extends DefaultListModel<MMNode>
{
    private static final long serialVersionUID = 1L;

    public MMNodeListModel()
    {
        // TODO Auto-generated constructor stub
    }

    public void putNode(MMNode node)
    {
        super.removeElement(node);
        super.add(0, node);
    }

    public void removeNode(MMNode node)
    {
        super.removeElement(node);
    }

}
