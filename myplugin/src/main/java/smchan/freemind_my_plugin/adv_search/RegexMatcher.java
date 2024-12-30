package smchan.freemind_my_plugin.adv_search;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of the {@linkplain IMatcher} based on regular expression
 * 
 * Avoided the use of the Java 9 streaming interface, e.g.
 * {@linkplain java.util.regex.Matcher.results()} for backward compatibility
 * with Java 1.8
 */
class RegexMatcher implements IMatcher {
    private final Pattern _pattern;

    public RegexMatcher(String searchTerm, boolean isCaseSensitive) {
        assert searchTerm != null;

        int flags = 0;
        if (!isCaseSensitive) {
            flags |= Pattern.CASE_INSENSITIVE;
        }

        // The compile method may raise unchecked exception
        _pattern = Pattern.compile(searchTerm, flags);
    }

    @Override
    public int getMatchScore(String text) {
        Matcher matcher = _pattern.matcher(text);
        int index = 0;
        int count = 0;
        while (matcher.find(index)) {
            index = matcher.end();
            ++count;
        }
        return count;
    }

    @Override
    public List<MatchRange> getMatchRanges(String text) {
        LinkedList<MatchRange> list = new LinkedList<>();
        Matcher matcher = _pattern.matcher(text);
        int index = 0;
        while (matcher.find(index)) {
            index = matcher.end();
            list.add(new MatchRange(matcher.start(), matcher.end()));
        }
        return list;
    }

}
