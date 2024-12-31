package smchan.freemind_my_plugin.adv_search;

import java.util.LinkedList;
import java.util.Set;

import javax.swing.AbstractListModel;

class SearchResultsListModel extends AbstractListModel<SearchResult> {

    private static final long serialVersionUID = 1L;
    private final LinkedList<SearchResult> _results = new LinkedList<>();

    public SearchResultsListModel() {
        // ...
    }

    public void clear() {
        int oldSize = _results.size();
        _results.clear();
        fireIntervalRemoved(this, 0, oldSize);
    }

    public void setResults(Set<SearchResult> results) {
        clear();

        _results.addAll(results);
        fireContentsChanged(this, 0, _results.size());
    }

    @Override
    public int getSize() {
        return _results.size();
    }

    @Override
    public SearchResult getElementAt(int index) {
        return _results.get(index);
    }

}
