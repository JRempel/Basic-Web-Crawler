package crawler;

import java.util.ArrayList;

/**
 * A POJO to store the result of crawling a web-page.
 */
public class PageResult {
    private ArrayList<String> linkedURLS;
    private ArrayList<WordResult> words;
    private long lastCrawled;

    public PageResult(ArrayList<String> linkedURLS, ArrayList<WordResult> words, long timeStamp) {
        this.linkedURLS = linkedURLS;
        this.words = words;
        this.lastCrawled = timeStamp;
    }

    public ArrayList<String> getLinkedURLS() {
        return linkedURLS;
    }

    public ArrayList<WordResult> getWords() {
        return words;
    }

    public long getLastCrawled() {
        return lastCrawled;
    }
}
