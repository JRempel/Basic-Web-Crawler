package hk.edu.hbku.crawler;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class Http {

    // Page timeout in milliseconds
    private static int timeout = 5000;

    /**
     * Retrieve html file of given url.
     *
     * @param url
     */
    public static PageResult get(String url, HashSet<String> ignoredWords) throws PageResultException {
        try {
            Connection.Response response = Jsoup.connect(url).timeout(timeout).execute();
            if (response.statusCode() == 200) {
                Document page = Jsoup.connect(url).get();
                if (page != null) {
                    // retrieve urls linked to on current page
                    Elements links = page.select("a[href]");
                    ArrayList<String> linkedUrls = new ArrayList<>();
                    for (Element link: links) {
                        if (StringUtils.isNotBlank(link.attr("abs:href"))) {
                            linkedUrls.add(link.attr("abs:href").replaceAll("#.*", ""));
                        }
                    }

                    // retrieve words on current page, along with their first index
                    // and number of occurrences if they are not ignored
                    ArrayList<String> words = new ArrayList<>(Arrays.asList(page.text().split("\\s+")));
                    HashMap<String, WordResult> wordList = new HashMap<>();
                    for (int i = 0; i < wordList.size(); i++) {
                        String currentWord = words.get(i);
                        if (!ignoredWords.contains(currentWord)) {
                            if (wordList.get(currentWord) == null) {
                                wordList.put(currentWord, new WordResult(currentWord, i, 1));
                            } else {
                                WordResult existing = wordList.get(currentWord);
                                existing.increaseCount();
                                wordList.put(currentWord, existing);
                            }
                        }
                    }

                    return new PageResult(linkedUrls, new ArrayList<>(wordList.values()));
                }
            } else {
                // some sort of Http error response
                throw new PageResultException("Failed to retrieve page: " + url + " due to HTTP Response " + response.statusCode() + response.statusMessage());
            }

        } catch (IOException e) {
            throw new PageResultException(e.getMessage(), e.getCause());
        }
        throw new PageResultException("Unknown Error occurred while attempting to process " + url + ".");
    }
}
