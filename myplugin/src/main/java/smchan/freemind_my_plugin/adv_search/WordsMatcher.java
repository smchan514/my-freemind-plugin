package smchan.freemind_my_plugin.adv_search;

import java.util.LinkedList;
import java.util.List;

/**
 * Implementation of the {@linkplain IMatcher} based on simple text matching
 */
class WordsMatcher implements IMatcher {
    private static final String REGEX_WHITESPACE = "\\s+";

    private final String _searchTerm;
    private final String[] _parts;
    private final boolean _isCaseSensitive;

    public WordsMatcher(String searchTerm, boolean isCaseSensitive) {
        assert searchTerm != null;

        if (!isCaseSensitive) {
            // Case-insensitive search: convert all text to lowercase
            searchTerm = searchTerm.toLowerCase();
        }

        // Split searchTerm into distinct words
        _parts = searchTerm.split(REGEX_WHITESPACE);
        if (_parts.length == 1) {
            // If there is only one word in the search term, then set _searchTerm to null so
            // that it won't trigger the big score
            _searchTerm = null;

        } else {
            _searchTerm = searchTerm;
        }

        _isCaseSensitive = isCaseSensitive;
    }

    @Override
    public int getMatchRelevance(String text) {
        int relevance = 0;

        if (!_isCaseSensitive) {
            // Case-insensitive search: convert all text to lowercase
            text = text.toLowerCase();
        }

        if (_searchTerm != null) {
            if (text.contains(_searchTerm)) {
                // If the search term is found as-is, score 10
                relevance += 10;
            }
        }

        String[] words = text.split(REGEX_WHITESPACE);
        boolean allPartsFouund = true;

        // For each part of the search term...
        for (String str : _parts) {
            if (hasExactMatch(str, words)) {
                // Exact word match scores 5
                relevance += 5;
            } else if (text.contains(str)) {
                // Partial word match scores 1
                relevance += 1;
            } else {
                // Losing bonus...
                allPartsFouund = false;
            }
        }

        // Special bonus if all parts of the search term are found
        if (allPartsFouund) {
            relevance += _parts.length * 5;
        }

        return relevance;
    }

    @Override
    public List<MatchRange> getMatchRanges(String text) {
        LinkedList<MatchRange> list = new LinkedList<>();

        if (!_isCaseSensitive) {
            // Case-insensitive search: convert all text to lowercase
            text = text.toLowerCase();
        }

        if (_searchTerm != null) {
            findAll(_searchTerm, text, list);
        }

        for (String str : _parts) {
            findAll(str, text, list);
        }

        return list;
    }

    private static void findAll(String searchTerm, String text, LinkedList<MatchRange> list) {
        if (searchTerm.isEmpty() || text.isEmpty()) {
            return;
        }

        int index = 0;
        while (index >= 0) {
            index = text.indexOf(searchTerm, index);
            if (index >= 0) {
                list.add(new MatchRange(index, index + searchTerm.length()));
                index += searchTerm.length() + 1;
            }
        }
    }

    private static boolean hasExactMatch(String searchTerm, String[] words) {
        for (String word : words) {
            if (searchTerm.equals(word)) {
                // Found one matching word
                return true;
            }
        }

        // If we get here, that means no match found
        return false;
    }

}
