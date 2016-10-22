package crawler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class Crawler {
    private Storage storage;
    private ArrayList<String> urlPool = new ArrayList<>();
    private HashSet<String> processedUrlPool = new HashSet<>();
    private HashMap<String, Long> previouslyCrawled;
    private HashSet<String> ignoredWords = new HashSet<String>();
    private int maxURLsToCrawl;
    private int maxURLPoolSize;
    private int daysBeforeReCrawl;
    private long millisecondsPerDay = 86400000L;


    public Crawler(String url, int maxURLsToCrawl, int maxURLPoolSize, Storage storage, HashSet<String> ignoreList, int daysBeforeReCrawl) {
        urlPool.add(url);
        this.storage = storage;
        this.maxURLsToCrawl = maxURLsToCrawl;
        this.maxURLPoolSize = maxURLPoolSize;
        this.ignoredWords = ignoreList;
        this.daysBeforeReCrawl = daysBeforeReCrawl;
    }

    public void start() {
        // get timestamp list
        previouslyCrawled = storage.getLastCrawled();
        long currentTime = System.currentTimeMillis();

        while (!urlPool.isEmpty() && processedUrlPool.size() < maxURLsToCrawl) {
            // Get next URL and update pools
            String currentUrl = urlPool.get(0);
            processedUrlPool.add(urlPool.get(0));
            urlPool.remove(0);

            if (previouslyCrawled.get(currentUrl) == null || Math.abs(currentTime - previouslyCrawled.get(currentUrl))> daysBeforeReCrawl * millisecondsPerDay) {
                try {
                    // check timestamp

                    PageResult pageResult = Http.get(currentUrl, ignoredWords);

                    // Add new URLs to the pool to be processed
                    if (pageResult.getLinkedURLS() != null) {
                        for(String url: pageResult.getLinkedURLS()) {
                            if (!urlPool.contains(url) && !processedUrlPool.contains(url) && urlPool.size() < maxURLPoolSize) {
                                urlPool.add(url);
                            }
                        }
                    }

                    // Store processed words & metadata
                    if (pageResult.getWords() != null) {
                        for (WordResult wordResult: pageResult.getWords()) {
                            // convert to PreparedResult later
                            // TODO: Don't re-insert URLS for each word-result
                            storage.insert(currentUrl, wordResult, pageResult.getLastCrawled());
                        }
                    }
                } catch (PageResultException pe) {
                    System.out.println(pe.getMessage());
                    pe.printStackTrace();
                }
            }
        }
    }
}