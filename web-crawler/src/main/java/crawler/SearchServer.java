package crawler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SearchServer {
    private static String initalURL;
    private static int maxURLS;
    private static int maxPoolSize;

    private static String INITAL_URL = "initialURL";
    private static String MAX_URLS = "maxURLs";
    private static String MAX_POOL_SIZE = "maxPoolSize";

    public static void main(String[] args) {
        loadProperties();
        Storage storage = new Storage();
        Crawler crawler = new Crawler(initalURL, maxURLS, maxPoolSize, storage);
        crawler.start();

        for (String s: storage.find("staff")) {
            System.out.println(s);
        }

        storage.close();
        // TODO: Need to figure out why some thread isn't exiting properly
        System.exit(0);
    }

    private static void loadProperties() {
        Properties config = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties");
            config.load(inputStream);
            initalURL = config.getProperty(INITAL_URL);
            maxURLS = Integer.parseInt(config.getProperty(MAX_URLS));
            maxPoolSize = Integer.parseInt(config.getProperty(MAX_POOL_SIZE));
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
        if (initalURL == null || maxURLS == 0 || maxPoolSize == 0) {
            throw new RuntimeException("Could not load web-crawler properties or config was missing a property.");
        }
    }
}
