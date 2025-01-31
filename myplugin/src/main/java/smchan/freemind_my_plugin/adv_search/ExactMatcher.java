package smchan.freemind_my_plugin.adv_search;

import java.util.LinkedList;
import java.util.List;

/**
 * Implementation of the {@linkplain IMatcher} based on simple whole text
 * matching
 */
class ExactMatcher implements IMatcher {
    private final String _searchTerm;
    private final boolean _isCaseSensitive;

    public ExactMatcher(String searchTerm, boolean isCaseSensitive) {
        assert searchTerm != null;

        if (!isCaseSensitive) {
            // Case-insensitive search: convert all text to lowercase
            searchTerm = searchTerm.toLowerCase();
        }

        _searchTerm = searchTerm;
        _isCaseSensitive = isCaseSensitive;
    }

    @Override
    public int getMatchScore(String text) {
        int score = 0;

        if (!_isCaseSensitive) {
            // Case-insensitive search: convert all text to lowercase
            text = text.toLowerCase();
        }

        if (_searchTerm != null) {
            if (text.contains(_searchTerm)) {
                // If the search term is found as-is, score 10
                score += 10;
            }
        }

        return score;
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

}
