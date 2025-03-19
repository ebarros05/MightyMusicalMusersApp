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

public class OLD_Main {
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
            
            daos.UserDAO.login(conn, "mordoplays", "Password@123");

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

            // TODO: While Loop that handles user inputs.
            // Everything below will be place holder messages, just acting and existing
            // for the loop. Any desires to change what is printed is completely fine.
            // Additionally, responsibility, whilst could be considered bad practice, is
            // sometimes placed within smaller functions in order to increase readability.

            // Placeholder welcome message (Not sure what to put here:
            System.out.println("The Mighter Musical Musers!");
            boolean running = true; boolean logged_in = false;
            Scanner in = new Scanner(System.in);

            while(running){
                System.out.println("Options: \nEnter the number of your desired option.");

                // First checks if the user is logged in or not
                // Need to potentially define if create account will disappear if user logs in.
                // Currently, holds whether logged in, then calling switching what gets printed

                if(logged_in){
                    // Order is up to debate, can be changed at some point

                    // TODO: Gives user the option to listen to a playlist or song
                    System.out.println("1: Listen to Some Music!");

                    // Within the Collections menu (option 1): pulls up the options to modify the name,
                    // delete certain collections, add and delete songs and albums from a collection
                    System.out.println("2: View List of Collections");

                    System.out.println("3: Song Search Options");

                    System.out.println("4: Create New Collection of Songs");

                    // This will lead into a function call which will prompt the user to follow via email or username
                    System.out.println("4: Follow a User");
                } else {
                    System.out.println("1: Login");
                    System.out.println("2: Create Account");
                    System.out.println("3: Song Search Options");
                }
                System.out.println("9999: Exit the Program");
                int option = in.nextInt();

                // Options for when user is not logged in (Login and Create Account)
                if(logged_in){
                    // TODO: Implement user input handling when logged in
                } else {
                    String username, password;
                    switch (option) {
                        case 1:
                            System.out.println("Logging in:");
                            System.out.println("Please input your username: ");
                            username = in.nextLine();
                            System.out.println("Please input your password: ");
                            password = in.nextLine();

                            // TODO: Should we store the current user logging in to be used later?
                            daos.UserDAO.login(conn, username, password);
                            logged_in = true;

                            break;
                        case 2:
                            // TODO: Might need to place checks that user's input is valid.
                            System.out.println("Registering account:");

                            System.out.println("Please enter a username:");
                            username = in.nextLine();

                            System.out.println("Please enter a secure password:");
                            password = in.nextLine();

                            System.out.println("Please enter your name, using a space between your first and last name:");
                            String full_name = in.nextLine();
                            // Use split to separate first and last names, using the array in the constructor.
                            String[] name_parts = full_name.split(" ");

                            System.out.println("Please enter in your email:");
                            String email = in.nextLine();

                            System.out.println("Please enter in your date-of-birth:");
                            String date_of_birth = in.nextLine();
                            java.sql.Date dateObj = java.sql.Date.valueOf(date_of_birth);

                            User new_User = new User(
                                    username,
                                    password,
                                    name_parts[0],
                                    name_parts[1],
                                    email,
                                    dateObj,
                                    new Timestamp(System.currentTimeMillis()),
                                    new Timestamp(System.currentTimeMillis()));

                            daos.UserDAO.registerUser(conn, new_User);
                            // After the user is registered, are we assuming that the User is also being logged in?
                            break;
                    }
                }

                // Universal Options
                switch (option) {
                    case 3:
                        // Call function that repeatedly allows for song searches
                        break;
                    case 9999:
                        running = false;
                        // Another placeholder just because not sure what to put.
                        System.out.println("Exiting the database.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
