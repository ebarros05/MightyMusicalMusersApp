import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import daos.PlayHistoryDAO;
import daos.PlaylistDAO;
import daos.SongDAO;
import daos.UserDAO;
import models.User;

public class Main {
    private static String dbUrl = "";
    private static String dbUser = "";
    private static String dbPassword = "";

    public static void main(String[] args) {
        // Load the database connection information from the properties file
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            Properties properties = new Properties();
            properties.load(fis);
            dbUrl = properties.getProperty("db.url");
            dbUser = properties.getProperty("db.user");
            dbPassword = properties.getProperty("db.password");
        } catch (IOException e) {
            System.out.println("Error loading database configuration: " + e.getMessage());
            return;
        }
        // Load PostgreSQL JDBC driver (not necessary? but good practice)
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("PostgreSQL JDBC Driver not found!");
            e.printStackTrace();
            return; // Exit program if driver isn't found
        }
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
            System.out.println("Connected to database!");

            // Example user data
            // User newUser = new User(
            //         "usernname",
            //         "Password123!",
            //         "first name",
            //         "last name",
            //         "email@aol.com",
            //         Date.valueOf("1969-10-28"),
            //         new Timestamp(System.currentTimeMillis()),
            //         new Timestamp(System.currentTimeMillis())
            // );

            // UserDAO.registerUser(conn, newUser);
            
            UserDAO.login(conn, "jtAIwKgjnY", "qkrC-O&0");

            //List<User> users = getAllUsers(conn);
            //System.out.println("All Users:");
            //for (User user : users) {
            //    System.out.println(user);
            // }
            //boolean res = UserDAO.login(conn, "mordoplays","Password@123");

            //test listen to 25 songs
            // for (int x = 1; x < 25; x++) {
            //     PlayHistoryDAO.playSong(conn, "mordoplays", x);
            // }

            //playlist CRUD
            //boolean res = PlaylistDAO.createPlaylist(conn, "biblioteca", 1, "mordoplays", 1);
            // PlaylistDAO.displayPlaylist(conn, "mordoplays", "biblioteca");
            // PlaylistDAO.addSongToPlaylist(conn, "mordoplays", "biblioteca", 2, 15);
            // PlaylistDAO.addSongToPlaylist(conn, "mordoplays", "biblioteca", 20, 1738);
            // // add song in an already taken playlist position:
            // PlaylistDAO.addSongToPlaylist(conn, "mordoplays", "biblioteca", 20, 221);
            // PlaylistDAO.removeSongFromPlaylist(conn, "mordoplays", "biblioteca", 20);
            // boolean res = PlaylistDAO.createPlaylist(conn, "justtogetdeleted", 1, "mordoplays", 1);
            // PlaylistDAO.renamePlaylist(conn, "mordoplays", "justtogetdeleted", "goobye");
            // PlaylistDAO.deletePlaylist(conn, "mordoplays", "goobye");
            // PlaylistDAO.displayPlaylist(conn, "mordoplays", "biblioteca");

            // //playing song increases play count, and searching by genre works
            // SongDAO.searchSongs(conn, "jeans", "title","title", true);
            // PlayHistoryDAO.playSong(conn, "mordoplays", 10001);
            // SongDAO.searchSongs(conn, "20", "genre","title", true);
            
            // // search by artist
            // SongDAO.searchSongs(conn, "2hol", "artist","title", true);

            //search by album name
            //SongDAO.searchSongs(conn, "10", "album", "title", false);

            //System.out.println("The action was successful:"+res);

            //TODO check if AlbumDAO is working as intended

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
