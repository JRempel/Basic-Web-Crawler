package crawler;

import java.util.ArrayList;

public class PageResult {
    private ArrayList<String> linkedURLS;
    private ArrayList<WordResult> words;

    public PageResult(ArrayList<String> linkedURLS, ArrayList<WordResult> words) {
        this.linkedURLS = linkedURLS;
        this.words = words;
    }

    public ArrayList<String> getLinkedURLS() {
        return linkedURLS;
    }

    public ArrayList<WordResult> getWords() {
        return words;
    }
}
