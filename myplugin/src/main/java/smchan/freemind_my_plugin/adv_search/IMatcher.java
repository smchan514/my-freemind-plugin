package smchan.freemind_my_plugin.adv_search;

import java.util.List;

/**
 * Generic interface for a search term matching component
 */
interface IMatcher {
    /**
     * Compute the match score for the argument text using the matcher as configured
     * 
     * @param  text a non-null instance of String
     * @return      match score
     */
    int getMatchScore(String text);

    /**
     * Compute the match ranges, i.e. start and end indexes of matched words, in the
     * argument text using the matcher as configured
     * 
     * @param  text a non-null instance of String
     * @return      a non-null list of {@linkplain MatchRange}s
     */
    List<MatchRange> getMatchRanges(String text);
}
