package crawler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.HashSet;
import org.apache.commons.io.IOUtils;
import java.util.StringTokenizer;

public class SearchServer {
    private static String initialURL;
    private static int maxURLS;
    private static int maxPoolSize;
    private static String templateName;
    private static HashSet<String> ignoreList;
    private static int daysBeforeRecrawl;

    private static String INITAL_URL = "initialURL";
    private static String MAX_URLS = "maxURLs";
    private static String MAX_POOL_SIZE = "maxPoolSize";
    private static String TEMPLATE_NAME = "templateName";
    private static String DAYS_BEFORE_RECRAWL = "daysBeforeRecrawl";

    private static final String CRAWLER_KEY = "THISISNOTASEARCHTERM_INDEXINSTEAD";
    private static boolean isCrawler;

    public static void main(String[] args) {
//        args = new String[1];
//        args[0] = "THISISNOTASEARCHTERM_INDEXINSTEAD";
//        args = new String[1];
//        args[0] = "hkbu";
        isCrawler = args[0].equals(CRAWLER_KEY);

        loadProperties();
        Storage storage = new Storage();

        if (isCrawler){
            Crawler crawler = new Crawler(initialURL, maxURLS, maxPoolSize, storage, ignoreList, daysBeforeRecrawl);
            crawler.start();
        } else {
            System.out.print("Content-Type: text/html\n\n");
            System.out.println(new TemplateResponse().createHTML(storage.find(args), templateName));
        }

        storage.close();
        // TODO: Need to figure out why some thread isn't exiting properly
        System.exit(0);
    }

    /**
     * Read config.properties file in.
     */
    private static void loadProperties() {
        Properties config = new Properties();
        InputStream inputStream = null;
        inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties");
        try {
            config.load(inputStream);
            if (isCrawler){
                initialURL = config.getProperty(INITAL_URL);
                maxURLS = Integer.parseInt(config.getProperty(MAX_URLS));
                maxPoolSize = Integer.parseInt(config.getProperty(MAX_POOL_SIZE));
                templateName = config.getProperty(TEMPLATE_NAME);
                daysBeforeRecrawl = Integer.parseInt(config.getProperty(DAYS_BEFORE_RECRAWL));

                inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("IgnoreList.csv");
                ignoreList = new HashSet<>();
                StringTokenizer stringTokenizer = new StringTokenizer(IOUtils.toString(inputStream),",");
                while (stringTokenizer.hasMoreTokens()) {
                    ignoreList.add(stringTokenizer.nextToken());
                }
            } else {
                templateName = config.getProperty(TEMPLATE_NAME);
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (isCrawler & (initialURL == null || maxURLS == 0 || maxPoolSize == 0)) {
            throw new RuntimeException("Could not load web-crawler properties or config was missing a property.");
        }
    }
}
