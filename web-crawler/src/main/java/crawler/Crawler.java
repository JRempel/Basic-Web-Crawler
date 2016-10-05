package crawler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

public class Crawler {
    private ArrayList<String> urlPool = new ArrayList<>();
    private HashSet<String> processedUrlPool = new HashSet<>();
    private HashSet<String> ignoredWords = new HashSet<String>(Arrays.asList("and", "the", "for", "did", "does", "are", "was", "has", "had", "have", "that", "this", "these", "which", "whose", "who", "whom", "what", "why", "she", "he", "they", "them"));
    private int maxURLsToCrawl;
    private int maxURLPoolSize;

    public Crawler(String url, int maxURLsToCrawl, int maxURLPoolSize) {
        urlPool.add(url);
        this.maxURLsToCrawl = maxURLsToCrawl;
        this.maxURLPoolSize = maxURLPoolSize;
    }

    public void start() {
        while (!urlPool.isEmpty() && processedUrlPool.size() < maxURLsToCrawl) {
            if (processedUrlPool.size() % 10 == 0)
                System.out.println("[Crawler] Crawled " + processedUrlPool.size() + " pages.");

            // Get next URL and update pools
            String currentUrl = urlPool.get(0);
            processedUrlPool.add(urlPool.get(0));
            urlPool.remove(0);

            try {
                PageResult pageResult = Http.get(currentUrl, ignoredWords);

                // Add new URLs to the pool to be processed
                if (pageResult.getLinkedURLS() != null) {
                    for(String url: pageResult.getLinkedURLS()) {
                        if (!urlPool.contains(url) && !processedUrlPool.contains(url) && urlPool.size() < maxURLPoolSize) {
                            urlPool.add(url);
                        }
                    }
                }

                // Store processed words
                if (pageResult.getWords() != null) {

                }
            } catch (PageResultException pe) {
                System.out.println(pe.getMessage());
                pe.printStackTrace();
            }
        }

        ArrayList<String> list = new ArrayList<>(processedUrlPool);
        Collections.sort(list);
        for (String s: list) {
            System.out.println(s);
        }
    }
}