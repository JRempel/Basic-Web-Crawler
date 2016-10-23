package crawler;

/**
 * A POJO to store search results retrieved from the Storage object.
 */
public class SearchResult {
    private String url;
    private int firstOccurrence;
    private int numOccurrences;

    public SearchResult(String url, int numOccurrences, int firstOccurrence) {
        this.url = url;
        this.firstOccurrence = firstOccurrence;
        this.numOccurrences = numOccurrences;
    }

    public String getUrl() {
        return url;
    }

    public int getFirstOccurrence() {
        return firstOccurrence;
    }

    public int getNumOccurrences() {
        return numOccurrences;
    }
}
