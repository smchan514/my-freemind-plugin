package smchan.freemind_my_plugin.adv_search;

/**
 * Utility class to render highlighted text in HTML
 */
class StyledText {
    private static final char STYLE_HIGHLIGHT = 1;

    private final char[] _text;
    private final char[] _style;

    public StyledText(String text) {
        _text = text.toCharArray();
        _style = new char[_text.length];
    }

    public void highlightRange(int start, int end) {
        for (int i = start; i < end; i++) {
            _style[i] = STYLE_HIGHLIGHT;
        }
    }

    public String toHtml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<body>");

        boolean wasHL = false;
        for (int i = 0; i < _text.length; i++) {
            // Apply text style if changed
            char newStyle = _style[i];
            boolean isHL = (newStyle & STYLE_HIGHLIGHT) != 0;

            if (wasHL ^ isHL) {
                if (isHL) {
                    sb.append("<font style=\"background-color: orange;\">");
                } else {
                    sb.append("</font>");
                }
            }

            wasHL = isHL;

            // Add the character
            sb.append(_text[i]);
        }

        if (wasHL) {
            sb.append("</font>");
        }

        return sb.toString();
    }
}
