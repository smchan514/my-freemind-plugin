package smchan.freemind_my_plugin;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import freemind.extensions.ExportHook;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;

public class InsertRandomString extends ExportHook {
    private static final char CHAR_LETTERS[] = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
            'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
            'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', };

    private static final char CHAR_NUMBERS[] = { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', };

    private static final char CHAR_PUNCTUATIONS[] = { '!', '@', '#', '$', '%', '+', '-', '*', '/', };

    // Plugin resource names
    private static final String RES_KEY_DEFAULT_MAX_TOTAL = "default_max_total";
    private static final String RES_KEY_DEFAULT_MIN_LETTERS = "default_min_letters";
    private static final String RES_KEY_DEFAULT_MIN_DIGITS = "default_min_digits";
    private static final String RES_KEY_DEFAULT_MIN_PUNCTS = "default_min_puncts";

    ////////////////////////////////////////////////////////////
    // Configurations

    private boolean _initialized = false;
    private int _defaultMaxTotal = 64;
    private int _defaultMinLetters = 20;
    private int _defaultMinDigits = 2;
    private int _defaultMinPuncts = 2;

    public InsertRandomString() {
        // ...
    }

    @Override
    public void startupMapHook() {
        super.startupMapHook();

        // Lazy init of this action
        if (!_initialized) {
            performInit();
        }

        // Get user input from a modal dialog
        InsertRandomStringConfigDialog dialog = new InsertRandomStringConfigDialog(_defaultMaxTotal, _defaultMinLetters,
                _defaultMinDigits, _defaultMinPuncts);
        
        // Skip the rest if the user cancels
        if (!dialog.showDialog()) {
            return;
        }
        
        try {
            int maxTotal = dialog.getMaxTotal();
            int nbrLetters = dialog.getNumberLetters();
            int nbrDigits = dialog.getNumberDigits();
            int nbrPunctuations = dialog.getNumberPuncts();
            String str = generateString(maxTotal, nbrLetters, nbrDigits, nbrPunctuations);

            // Assign the new text to the current node
            List<?> list = getController().getSelecteds();
            MindMapNode selected = (MindMapNode) list.get(0);
            MindMapController mmc = (MindMapController) getController();
            mmc.edit.setNodeText(selected, str);
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performInit() {
        String str;

        if ((str = getResourceString(RES_KEY_DEFAULT_MAX_TOTAL)) != null) {
            _defaultMaxTotal = Integer.parseInt(str);
        }

        if ((str = getResourceString(RES_KEY_DEFAULT_MIN_LETTERS)) != null) {
            _defaultMinLetters = Integer.parseInt(str);
        }

        if ((str = getResourceString(RES_KEY_DEFAULT_MIN_DIGITS)) != null) {
            _defaultMinDigits = Integer.parseInt(str);
        }

        if ((str = getResourceString(RES_KEY_DEFAULT_MIN_PUNCTS)) != null) {
            _defaultMinPuncts = Integer.parseInt(str);
        }

        _initialized = true;
    }

    private static String generateString(int maxTotal, int minLetters, int minDigits, int minPuncts) {
        // Check for the preconditions
        assert (minLetters >= 0);
        assert (minDigits >= 0);
        assert (minPuncts >= 0);
        assert (maxTotal > (minLetters + minDigits + minPuncts));

        SecureRandom sr = new SecureRandom();
        sr.setSeed(System.currentTimeMillis());

        // Compute the delta between the currently specified minima and the total max
        int minLength = minLetters + minDigits + minPuncts;
        int maxDelta = maxTotal - minLength;

        // Compute how many extra characters to create for each category
        int delta = sr.nextInt(maxDelta + 1);
        int extraDigits = delta / 3;
        int extraPuncts = delta / 3;
        int extraLetters = delta - extraDigits - extraPuncts;

        // Compute the number of characters for each category
        int nLetters = minLetters + extraLetters;
        int nDigits = minDigits + extraDigits;
        int nPuncts = minPuncts + extraPuncts;
        int length = nLetters + nDigits + nPuncts;

        // Create an array list to hold the characters
        ArrayList<Character> chars = new ArrayList<>(length);

        for (int i = 0; i < nLetters; i++) {
            // Add a character from the source array at random index
            chars.add(CHAR_LETTERS[sr.nextInt(CHAR_LETTERS.length)]);
        }

        for (int i = 0; i < nDigits; i++) {
            // Add a character from the source array at random index
            chars.add(CHAR_NUMBERS[sr.nextInt(CHAR_NUMBERS.length)]);
        }

        for (int i = 0; i < nPuncts; i++) {
            // Add a character from the source array at random index
            chars.add(CHAR_PUNCTUATIONS[sr.nextInt(CHAR_PUNCTUATIONS.length)]);
        }

        // Build the string
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            // Pick a character from the array list
            sb.append(chars.remove(sr.nextInt(chars.size())));
        }

        return sb.toString();
    }

}
