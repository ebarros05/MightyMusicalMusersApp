import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Pattern;

import daos.*;
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


            //TODO check if AlbumDAO is working as intended

            // TODO: While Loop that handles user inputs.
            // Everything below will be place holder messages, just acting and existing
            // for the loop. Any desires to change what is printed is completely fine.
            // Additionally, responsibility, whilst could be considered bad practice, is
            // sometimes placed within smaller functions in order to increase readability.

            // Placeholder welcome message (Not sure what to put here)
            System.out.println("Welcome to the Mighter Musical Musers!");
            boolean running = true;
            User logged_in = null;
            Scanner in = new Scanner(System.in);

            while(running){
                System.out.println("Enter the number of your desired option.");

                // First checks if the user is logged in or not
                // Need to potentially define if create account will disappear if user logs in.
                // Currently, holds whether logged in, then calling switching what gets printed

                if(logged_in != null){
                    // TODO: Gives user the option to listen to a playlist or song
                    System.out.println("Welcome, "+logged_in.getFirst()+" \""+logged_in.getUsername()+"\" "+logged_in.getLast()+"!");
                    System.out.println("Please choose from one of the options below:");
                    System.out.println("1: Your Library");
                    System.out.println("2: Mighty Musical Musers Social™");
                    System.out.println("3: Search");
                    System.out.println("4: log out");
                    int uoption = in.nextInt();
                    in.nextLine();
                    switch (uoption) {
                        case 1:
                            System.out.println("YOUR LIBRARY");
                            System.out.println("-------------");
                            System.out.println("Please choose from one of the options below:");
                            System.out.println("1: List your playlists");
                            System.out.println("2: Play a playlist");
                            System.out.println("3: Play a song");
                            System.out.println("4: Edit a playlist");
                            System.out.println("5: Add an album to a playlist");
                            System.out.println("6: Create new playlist");
                            System.out.println("7: Delete playlist");
                            int libraryOption = in.nextInt();
                            in.nextLine();
                            String inp;
                            switch (libraryOption){
                                case 1:
                                    System.out.println("Here are your playlists:");
                                    PlaylistDAO.displayUserPlaylists(conn, logged_in.getUsername());
                                    break;
                                case 2:
                                    System.out.println("Please enter a playlist name to play");
                                    inp = in.nextLine();
                                    PlayHistoryDAO.playPlaylist(conn, logged_in.getUsername(), inp);
                                    break;
                                case 3:
                                    System.out.println("Please enter a song name  to play");
                                    inp = in.nextLine();
                                    PlayHistoryDAO.playPlaylist(conn, logged_in.getUsername(), inp);
                                    break;
                                case 4:
                                    String newName;
                                    System.out.println("Please enter the playlist you would like to rename:");
                                    inp = in.nextLine();
                                    System.out.println("Please enter the new name for the playlist:");
                                    newName = in.nextLine();
                                    PlaylistDAO.renamePlaylist(conn, logged_in.getUsername(),inp, newName);
                                    break;
                                case 5:
                                    int playlistNumber, albumId;
                                    System.out.println("Please enter a playlist name to add to:");
                                    inp = in.nextLine();
                                    System.out.println("Please the playlist number:");
                                    playlistNumber = in.nextInt();
                                    in.nextLine();
                                    System.out.println("Please enter the album Id to be added:");
                                    albumId = in.nextInt();
                                    in.nextLine();
                                    PlaylistDAO.addAlbumToPlaylist(conn, logged_in.getUsername(), inp, playlistNumber, albumId);
                                    break;
                                case 6:
                                    String playlistName;
                                    int pNumber, firstSong;
                                    System.out.println("Please enter a name for your new playlist:");
                                    playlistName = in.nextLine();
                                    System.out.println("Please enter the playlist number:");
                                    pNumber = in.nextInt();
                                    System.out.println("Please enter a first song number to add:");
                                    firstSong = in.nextInt();
                                    in.nextLine();
                                    PlaylistDAO.createPlaylist(conn, playlistName, pNumber, logged_in.getUsername(), firstSong);
                                    break;
                                case 7:
                                    System.out.println("Please enter the playlist name to delete:");
                                    inp = in.nextLine();
                                    PlaylistDAO.deletePlaylist(conn, logged_in.getUsername(), inp);
                                    break;
                            }

                        case 2:
                            System.out.println("MIGHT MUSICAL MUSERS SOCIAL™");
                            System.out.println("-----------------------------");
                            System.out.println("Please choose from one of the options below:");
                            System.out.println("1: follow user");
                            System.out.println("2: unfollow user");
                            System.out.println("3: follow artist");
                            System.out.println("4: unfollow artist");
                            int socialChoice = in.nextInt();
                            in.nextLine();
                            String socialInp;
                            int artistID;
                            switch (socialChoice) {
                                case 1:
                                    System.out.println("Please enter the name of the user you would like to follow:");
                                    socialInp = in.nextLine();
                                    UserDAO.followUser(conn, logged_in.getUsername(), socialInp);
                                    break;
                                case 2:
                                    System.out.println("Please enter the name of the user you would like to unfollow:");
                                    socialInp = in.nextLine();
                                    UserDAO.unfollowUser(conn, logged_in.getUsername(), socialInp);
                                    break;
                                case 3:
                                    System.out.println("Please enter the ID of the artist you would like to follow:");
                                    artistID = in.nextInt();
                                    in.nextLine();
                                    ArtistDAO.followArtist(conn, logged_in.getUsername(), artistID);
                                    break;
                                case 4:
                                    System.out.println("Please enter the ID of the artist you would like to unfollow:");
                                    artistID = in.nextInt();
                                    in.nextLine();
                                    ArtistDAO.unfollowArtist(conn, logged_in.getUsername(), artistID);
                                    break;
                            }
                        case 3:
                            System.out.println("Goodbye, "+logged_in.getUsername()+" see you next time!");
                            logged_in = null;
                    }
                } else {
                    System.out.println("1: Login");
                    System.out.println("2: Create Account");
                    System.out.println("3: Song Search Options");
                }
                // TODO: Need to implement the log out case
                System.out.println("9998: Log out of your account");
                System.out.println("9999: Exit the Program");
                int option = in.nextInt();

                // Options for when user is not logged in (Login and Create Account)
                if(logged_in == null){
                    String username, password;
                    switch (option) {
                        case 1:
                            System.out.println("Logging in:");
                            System.out.println("Please input your username: ");
                            username = in.nextLine();
                            System.out.println("Please input your password: ");
                            password = in.nextLine();

                            UserDAO.login(conn, username, password);
                            logged_in = UserDAO.searchUsersByUsername(conn, username).get(0);
                            //TODO add checks if the login information doesn't exist
                            break;
                        case 2:
                            final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", Pattern.CASE_INSENSITIVE);
                            final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z\\d]).{8,}$");

                            System.out.println("Registering account...");

                            System.out.println("Please enter a username:");
                            username = in.nextLine();

                            String pword;
                            while (true) {
                                System.out.println("Please enter a secure password:");
                                pword = in.nextLine();

                                if (PASSWORD_PATTERN.matcher(pword).matches()) {
                                    break;
                                } else {
                                    System.out.println("Invalid password! It must have at least:\n- 8 characters\n- One lowercase\n- One uppercase\n- One digit\n- One special character.\nPlease try again.");
                                }
                            }

                            System.out.println("Please enter your first name last name:");
                            String f_name = in.nextLine();

                            System.out.println("Please enter your last name:");
                            String l_name = in.nextLine();


                            String email;
                            while (true) {
                                System.out.println("Please enter your email:");
                                email = in.nextLine().trim();

                                if (EMAIL_PATTERN.matcher(email).matches()) {
                                    break;
                                } else {
                                    System.out.println("Invalid email format! Please enter a valid email (e.g., user@example.com).");
                                }
                            }

                            String date_of_birth;
                            Date dateObj = null;
                            while (true) {
                                System.out.println("Please enter your date of birth (yyyy-mm-dd):");
                                date_of_birth = in.nextLine().trim();

                                try {
                                    dateObj = Date.valueOf(date_of_birth);
                                    break;
                                } catch (IllegalArgumentException e) {
                                    System.out.println("Invalid date format! Please enter date in yyyy-mm-dd format.");
                                }
                            }
                            logged_in = new User(
                                    username,
                                    pword,
                                    f_name,
                                    l_name,
                                    email,
                                    dateObj,
                                    new Timestamp(System.currentTimeMillis()),
                                    new Timestamp(System.currentTimeMillis()));
                            // ^^^ updating 'logged_in' is more important for CLI
                            // VVV not neccesary, as logging in through the DB side just updates last access time
                            // UserDAO.login(conn, logged_in.getUsername(), logged_in.getPassword());

                            daos.UserDAO.registerUser(conn, logged_in);
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

    public static void searchHelperFunction(Connection conn){
        Scanner in = new Scanner(System.in);
        System.out.println("How would you like to search?");
        System.out.println("1: By Song title");
        System.out.println("2: By Artist name");
        System.out.println("3: By Album name");
        System.out.println("4: By Genre name");
        int option = in.nextInt();
        String typeCase;

        switch (option) {
            case 1:
                System.out.println("Enter the title of the Song you're searching for:");
                typeCase = "title";
                break;
            case 2:
                System.out.println("Enter the name of the Artist you're searching for:");
                typeCase = "artist";
                break;
            case 3:
                System.out.println("Enter the name of the Album you're searching for:");
                typeCase = "album";
                break;
            case 4:
                System.out.println("Enter the Genre of songs you're searching for:");
                typeCase = "genre";
                break;
        }

        String keyword = in.nextLine();
        System.out.println("How would you like to search?");
        System.out.println("1: Song Title");
        System.out.println("2: Artist Name");
        System.out.println("3: Album Name");
        System.out.println("4: Release Year");
        option = in.nextInt();
        String typeSearch;

        switch (option) {
            case 1:
                typeSearch = "title";
                break;
            case 2, 3:
                typeSearch = "name";
                break;
            case 4:
                typeSearch = "releaseDate";
        }

        String relativeString = in.nextLine();
        //daos.SongDAO.searchSongs(conn, keyword, typeCase, typeSearch, );
    }
}
