package smchan.freemind_my_plugin.mru.test;

public class MMNode
{
    private final String _text;
    private final String _id;

    public MMNode(String text, String id)
    {
        _text = text;
        _id = id;
    }

    public String getText()
    {
        return _text;
    }

    public String getId()
    {
        return _id;
    }

}
