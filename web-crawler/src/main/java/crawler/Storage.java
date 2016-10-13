package crawler;

import org.h2.tools.Server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Storage {
    private static final String DB_DRIVER = "org.h2.Driver";
    private static final String DB_CONNECTION = "jdbc:h2:./CrawlerStorage";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";

    private static final String CREATE_URL_TABLE = "CREATE TABLE IF NOT EXISTS URLS(ID BIGINT AUTO_INCREMENT PRIMARY KEY, SITE VARCHAR(512));";
    private static final String CREATE_WORD_TABLE = "CREATE TABLE IF NOT EXISTS WORDS(ID BIGINT AUTO_INCREMENT PRIMARY KEY, WORD VARCHAR(255));";
    private static final String CREATE_META_TABLE = "CREATE TABLE IF NOT EXISTS META(URL_INDEX BIGINT, WORD_INDEX BIGINT, FIRST_OCCURRENCE INT, " +
            "NUM_OCCURRENCES INT, FOREIGN KEY (URL_INDEX) REFERENCES PUBLIC.URLS(ID), FOREIGN KEY (WORD_INDEX) REFERENCES PUBLIC.WORDS(ID));";

    private static final String INSERT_URL = "INSERT INTO URLS (SITE) VALUES(?)";
    private static final String GET_URL_INDEX = "SELECT ID FROM URLS WHERE SITE=?";
    private static final String INSERT_WORD = "INSERT INTO WORDS (WORD) VALUES(?)";
    private static final String GET_WORD_INDEX = "SELECT ID FROM WORDS WHERE WORD=?";
    private static final String INSERT_META = "INSERT INTO META (URL_INDEX, WORD_INDEX, FIRST_OCCURRENCE, NUM_OCCURRENCES) VALUES(?,?,?,?)";

    private static final String SELECT_SEARCHTERM = "SELECT U.SITE FROM URLS AS U JOIN META AS M ON U.ID=M.URL_INDEX JOIN WORDS AS W ON W.ID=M.WORD_INDEX WHERE W.WORD=?";

    private Connection connection;

    public Storage() {
        try {
            Server db = Server.createTcpServer().start();
            Class.forName(DB_DRIVER);
            connection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        createTables();
    }

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

    public void insert(String url, WordResult wordResult) {
        try {
            ResultSet resultSet;
            int urlId;
            int wordId;

            PreparedStatement statement = connection.prepareStatement(INSERT_URL);
            statement.setString(1, url);
            statement.execute();

            statement = connection.prepareStatement(INSERT_WORD);
            statement.setString(1, wordResult.getWord());
            statement.execute();

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

            statement = connection.prepareStatement(INSERT_META);
            statement.setInt(1, urlId);
            statement.setInt(2, wordId);
            statement.setInt(3, wordResult.getPosition());
            statement.setInt(4, wordResult.getCount());
            statement.execute();

        } catch (Exception e) {
            System.out.println("Error inserting " + wordResult.getWord() + " with count " + wordResult.getCount() + " and position " + wordResult.getPosition() + ".");
            e.printStackTrace();
        }
    }

    public ArrayList<String> find (String searchTerm) {
        ArrayList<String> results = new ArrayList<>();

        try {
            PreparedStatement statement = connection.prepareStatement(SELECT_SEARCHTERM);
            statement.setString(1, searchTerm);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                results.add(resultSet.getString("SITE"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
