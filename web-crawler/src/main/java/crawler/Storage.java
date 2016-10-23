package crawler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Storage {
    private static final String DB_DRIVER = "org.h2.Driver";
    private static final String DB_CONNECTION = "jdbc:h2:./CrawlerStorage";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";

    private static final String CREATE_URL_TABLE = "CREATE TABLE IF NOT EXISTS URLS(ID BIGINT AUTO_INCREMENT PRIMARY KEY, " +
            "SITE VARCHAR(512), TIMESTAMP LONG);";
    private static final String CREATE_WORD_TABLE = "CREATE TABLE IF NOT EXISTS WORDS(ID BIGINT AUTO_INCREMENT PRIMARY KEY, " +
            "WORD VARCHAR(255));";
    private static final String CREATE_META_TABLE = "CREATE TABLE IF NOT EXISTS META(URL_INDEX BIGINT, WORD_INDEX BIGINT, " +
            "FIRST_OCCURRENCE INT, NUM_OCCURRENCES INT, PRIMARY KEY (URL_INDEX, WORD_INDEX), FOREIGN KEY (URL_INDEX) " +
            "REFERENCES PUBLIC.URLS(ID), FOREIGN KEY (WORD_INDEX) REFERENCES PUBLIC.WORDS(ID));";

    private static final String INSERT_URL = "INSERT INTO URLS (SITE, TIMESTAMP) VALUES(?,?)";
    private static final String SELECT_URL_COUNT = "SELECT COUNT (*) FROM URLS WHERE SITE=?";
    private static final String UPDATE_URL = "UPDATE URLS SET TIMESTAMP=? WHERE SITE=?";
    private static final String GET_URL_INDEX = "SELECT ID FROM URLS WHERE SITE=?";

    private static final String INSERT_WORD = "INSERT INTO WORDS (WORD) VALUES(?)";
    private static final String SELECT_WORD_COUNT = "SELECT COUNT (*) FROM WORDS WHERE WORD=?";
    private static final String GET_WORD_INDEX = "SELECT ID FROM WORDS WHERE WORD=?";

    private static final String INSERT_META = "INSERT INTO META (URL_INDEX, WORD_INDEX, FIRST_OCCURRENCE, NUM_OCCURRENCES) VALUES(?,?,?,?)";
    private static final String SELECT_META_COUNT = "SELECT COUNT (*) FROM META WHERE URL_INDEX=? AND WORD_INDEX=?";
    private static final String UPDATE_META = "UPDATE META SET FIRST_OCCURRENCE=? AND NUM_OCCURRENCES=? WHERE URL_INDEX=? AND WORD_INDEX=?";

    private static final String SELECT_SEARCHTERM = "SELECT U.SITE, M.NUM_OCCURRENCES, M.FIRST_OCCURRENCE FROM URLS AS U JOIN META AS M " +
            "ON U.ID=M.URL_INDEX JOIN WORDS AS W ON W.ID=M.WORD_INDEX WHERE W.WORD=?";
    private static final String GET_LASTCRAWLED = "SELECT SITE, TIMESTAMP FROM URLS";
    private static final String GET_ALL = "SELECT M.FIRST_OCCURRENCE, M.NUM_OCCURRENCES, W.WORD FROM URLS AS U JOIN META AS M ON U.ID=M.URL_INDEX" +
            " JOIN WORDS AS W ON W.ID=M.WORD_INDEX WHERE U.SITE=?";

    private Connection connection;

    /**
     * Create a connection to an embedded H2 DB, or create one if
     * one doesn't yet exist.
     */
    public Storage() {
        try {
            Class.forName(DB_DRIVER);
            connection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        createTables();
    }

    /**
     * Create the appropriate tables if they don't exist already.
     */
    private void createTables() {
        try {
            Statement statement = connection.createStatement();
            statement.execute(CREATE_URL_TABLE);
            statement.execute(CREATE_WORD_TABLE);
            statement.execute(CREATE_META_TABLE);

            statement.close();
            connection.commit();
        } catch (Exception e) {
            throw new RuntimeException("Unable to initialize H2 tables.");
        }
    }

    /**
     * Insert / update the tables based upon crawling a page.
     * @param url
     * @param wordResult
     * @param timestamp
     */
    public void insert(String url, WordResult wordResult, long timestamp) {
        try {
            ResultSet resultSet;
            int urlId;
            int wordId;
            PreparedStatement statement;

            // Update the URL entry if it exists, else create it
            statement = connection.prepareStatement(SELECT_URL_COUNT);
            statement.setString(1, url);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                if (resultSet.getInt(1) != 0) {
                    statement = connection.prepareStatement(UPDATE_URL);
                    statement.setLong(1, timestamp);
                    statement.setString(2, url);
                    statement.execute();
                } else {
                    statement = connection.prepareStatement(INSERT_URL);
                    statement.setString(1, url);
                    statement.setLong(2, timestamp);
                    statement.execute();
                }
            }

            // Insert a Word entry if it doesn't exist
            statement = connection.prepareStatement(SELECT_WORD_COUNT);
            statement.setString(1, wordResult.getWord());
            resultSet = statement.executeQuery();
            if (resultSet.next() && resultSet.getInt(1) == 0) {
                statement = connection.prepareStatement(INSERT_WORD);
                statement.setString(1, wordResult.getWord());
                statement.execute();
            }

            // Get index columns from previously inserted data for
            // linking in the Meta table.
            statement = connection.prepareStatement(GET_URL_INDEX);
            statement.setString(1, url);
            resultSet = statement.executeQuery();
            resultSet.next();
            urlId = resultSet.getInt("ID");

            statement = connection.prepareStatement(GET_WORD_INDEX);
            statement.setString(1, wordResult.getWord());
            resultSet = statement.executeQuery();
            resultSet.next();
            wordId = resultSet.getInt("ID");

            // Update a meta-entry if it exists, else create it
            statement = connection.prepareStatement(SELECT_META_COUNT);
            statement.setInt(1, urlId);
            statement.setInt(2, wordId);
            if (resultSet.next() && resultSet.getInt(1) != 0) {
                statement = connection.prepareStatement(UPDATE_META);
                statement.setInt(1, wordResult.getPosition());
                statement.setInt(2, wordResult.getCount());
                statement.setInt(3, urlId);
                statement.setInt(4, wordId);
            } else {
                statement = connection.prepareStatement(INSERT_META);
                statement.setInt(1, urlId);
                statement.setInt(2, wordId);
                statement.setInt(3, wordResult.getPosition());
                statement.setInt(4, wordResult.getCount());
                statement.execute();
            }

        } catch (Exception e) {
            System.out.println("Error inserting " + wordResult.getWord() + " with count " + wordResult.getCount() + " and position " + wordResult.getPosition() + ".");
            e.printStackTrace();
        }
    }

    /**
     * Get the list of sorted URLs that contain
     * all given search-terms.
     * @param searchTerms
     * @return
     */
    public ArrayList<String> find(String[] searchTerms) {
        ArrayList<SearchResult> searchResults;
        ArrayList<String> results = new ArrayList<>();
        HashSet<SearchResult> intersect = new HashSet<>();
        ArrayList<HashSet<SearchResult>> mergeList = new ArrayList<>();

        // Get the results for each individual search-term.
        for (String searchTerm : searchTerms) {
            HashSet<SearchResult> tempResults = new HashSet<>();
            try {
                PreparedStatement statement = connection.prepareStatement(SELECT_SEARCHTERM);
                statement.setString(1, searchTerm.toLowerCase());
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    tempResults.add(new SearchResult(resultSet.getString("SITE"), resultSet.getInt("NUM_OCCURRENCES"), resultSet.getInt("FIRST_OCCURRENCE")));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            mergeList.add(tempResults);
        }

        // Find the intersect between all search-result lists.
        if (!mergeList.isEmpty()) {
            intersect = mergeList.get(0);
            mergeList.remove(0);
            for (HashSet hs : mergeList) {
                intersect.retainAll(hs);
            }
        }

        // Sort any elements that are in the intersect list.
        if (!intersect.isEmpty()) {
            searchResults = new ArrayList<>(intersect);
            searchResults.sort((SearchResult s1, SearchResult s2) -> {
                if (s1.getNumOccurrences() != s2.getNumOccurrences())
                    return s2.getNumOccurrences() - s1.getNumOccurrences();
                else if (s1.getFirstOccurrence() != s2.getFirstOccurrence())
                    return s1.getFirstOccurrence() - s2.getFirstOccurrence();
                else
                    return s1.getUrl().compareTo(s2.getUrl());
            } );
            for (SearchResult s: searchResults) {
                results.add(s.getUrl());
            }
        }
        return results;
    }

    /**
     * Get a list of all previously crawled URLs and the timestamp
     * of when they were last crawled.
     * @return
     */
    public HashMap<String, Long> getLastCrawled() {
        HashMap<String, Long> results = new HashMap<>();
        PreparedStatement statement;

        try {
            statement = connection.prepareStatement(GET_LASTCRAWLED);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                results.put(resultSet.getString("SITE"), resultSet.getLong("TIMESTAMP"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    /**
     * Print all relations in the DB.
     */
    public void dumpResults() {
        HashMap<String, Long> urls = getLastCrawled();
        System.out.println("URL LAST_CRAWLED(EPOCH) WORD FIRST_OCCURRENCE NUM_OCCURRENCES");
        for (String url : urls.keySet()) {
            PreparedStatement statement;
            ResultSet resultSet;
            try {
                statement = connection.prepareStatement(GET_ALL);
                statement.setString(1, url);
                resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    System.out.println(url + " " + urls.get(url) + " "  + resultSet.getString("WORD") +  " " +
                            resultSet.getInt("FIRST_OCCURRENCE") + " " + resultSet.getInt("NUM_OCCURRENCES"));
                }
            } catch (Exception e) {

            }
        }
    }

    /**
     * Close the connection to the H2 DB so that the tables aren't locked.
     */
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
