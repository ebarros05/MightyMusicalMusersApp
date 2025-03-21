import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Pattern;

import daos.*;
import models.Song;
import models.User;
import models.Artist;

public class OLD_Main {
    private static String dbUrl = "";
    private static String dbUser = "";
    private static String dbPassword = "";

    public static void main(String[] args) throws SQLException {
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
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            System.out.println("Connected to database!");

            System.out.println("Welcome to the Mighter Musical Musers!");
            boolean running = true;
            User logged_in = null;
            Scanner in = new Scanner(System.in);

            while(running){
                //TODO revamp playlist editing(currently its very sloppy, you have to add song id, and playlist number)
                System.out.println("------------------------------------------");
                System.out.println("* Enter the number of your desired option.");

                // First checks if the user is logged in or not
                // Need to potentially define if create account will disappear if user logs in.
                // Currently, holds whether logged in, then calling switching what gets printed

                if(logged_in != null){
                    System.out.println("Welcome, "+logged_in.getFirst()+" \""+logged_in.getUsername()+"\" "+logged_in.getLast()+"!");
                    System.out.println("Please choose from one of the options below:");
                    System.out.println("1: Your Library");
                    System.out.println("2: Mighty Musical Musers Social™");
                    System.out.println("3: Search");
                    System.out.println("4: Rate a Song!");
                    System.out.println("5: Log out");
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
                            System.out.println("4: Edit a playlist name");
                            System.out.println("5: Add a song to a playlist");
                            System.out.println("6: Delete a song from a playlist");
                            System.out.println("7: Add an album to a playlist");
                            System.out.println("8: Create new playlist");
                            System.out.println("9: Delete playlist");
                            int libraryOption = in.nextInt();
                            in.nextLine();
                            String inp;
                            switch (libraryOption){
                                case 1:// list
                                    System.out.println("Here are your playlists:");
                                    PlaylistDAO.displayUserPlaylists(conn, logged_in.getUsername());
                                    break;
                                case 2://play playlist
                                    System.out.println("Please enter a playlist name to play");
                                    inp = in.nextLine();
                                    PlayHistoryDAO.playPlaylist(conn, logged_in.getUsername(), inp);
                                    break;
                                case 3://play song
                                    System.out.println("Please enter a song name to play");
                                    inp = in.nextLine();
                                    Song songz = SongDAO.getASong(conn, inp);
                                    System.out.println("Found "+songz+" songs with that name");
                                    PlayHistoryDAO.playSong(conn, logged_in.getUsername(), songz.getId());
                                    break;
                                case 4://edit playlist name
                                    String newName;
                                    System.out.println("Please enter the playlist name you would like to rename:");
                                    inp = in.nextLine();
                                    System.out.println("Please enter the new name for the playlist:");
                                    newName = in.nextLine();
                                    PlaylistDAO.renamePlaylist(conn, logged_in.getUsername(),inp, newName);
                                    break;
                                case 5://add new song to playlist
                                    int pnumber, songnumber;
                                    System.out.println("Please enter the playlist name to add a song to:");
                                    inp = in.nextLine();
                                    System.out.println("Please enter the position of the song in the playlist:");
                                    pnumber = in.nextInt();
                                    in.nextLine();
                                    System.out.println("Please enter the song id to add:");
                                    songnumber = in.nextInt();
                                    in.nextLine();
                                    PlaylistDAO.addSongToPlaylist(conn, logged_in.getUsername(), inp, pnumber, songnumber);
                                    break;
                                case 6://delete song to playlist
                                    int songsid;
                                    System.out.println("Please enter the playlist name to delete a song from:");
                                    inp = in.nextLine();
                                    System.out.println("Please enter the song id to delete:");
                                    songsid = in.nextInt();
                                    in.nextLine();
                                    PlaylistDAO.removeSongFromPlaylist(conn, logged_in.getUsername(), inp, songsid);
                                    break;
                                case 7:// Add an album to a playlist
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
                                case 8:// Create new playlist
                                    String playlistName;
                                    int pNumber, firstSong;
                                    System.out.println("Please enter a name for your new playlist:");
                                    playlistName = in.nextLine();
                                    System.out.println("Please enter the playlist number:");
                                    pNumber = in.nextInt();
                                    in.nextLine();
                                    System.out.println("Please enter a first song number to add:");
                                    firstSong = in.nextInt();
                                    in.nextLine();
                                    PlaylistDAO.createPlaylist(conn, playlistName, pNumber, logged_in.getUsername(), firstSong);
                                    break;
                                case 9:// Delete playlist
                                    System.out.println("Please enter the playlist name to delete:");
                                    inp = in.nextLine();
                                    PlaylistDAO.deletePlaylist(conn, logged_in.getUsername(), inp);
                                    break;
                            }
                            break;
                        case 2:
                            System.out.println("MIGHT MUSICAL MUSERS SOCIAL™");
                            System.out.println("-----------------------------");
                            System.out.println("Please choose from one of the options below:");
                            System.out.println("1: Follow user");
                            System.out.println("2: Unfollow user");
                            System.out.println("3: Follow artist");
                            System.out.println("4: Unfollow artist");
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
                            break;
                        case 3:
                            System.out.println("SEARCHING!");
                            System.out.println("----------");
                            searchHelperFunction(conn, logged_in);
                            break;
                        case 4:
                            // Performs the rating for songs
                            System.out.println("RATING");
                            System.out.println("------");
                            System.out.println("Please enter the name of the song you wish to rate:");
                            String song_name = in.nextLine();
                            Song songsToRate = daos.SongDAO.getASong(conn, song_name);
                            if(songsToRate != null){
                                System.out.println("How many stars do you want to rate '"+song_name+"'? [From 1 - 5 Stars]");
                                int rating = in.nextInt();
                                in.nextLine(); // eat the \n
                                daos.RatingDAO.rateSong(conn, songsToRate, logged_in, rating);
                            }
                            break;
                        case 5:
                            System.out.println("Goodbye, "+logged_in.getUsername()+" see you next time!");
                            logged_in = null;
                            // System.out.print("\033[H\033[2J");
                            break;
                    }
                    // Options for when user is not logged in (Login and Create Account)
                } else {
                    System.out.println("You are not currently logged in.");
                    System.out.println("Please choose from one of the options below:");
                    System.out.println("1: Log in");
                    System.out.println("2: Create Account");
                    System.out.println("3: Search");
                    int option = in.nextInt();
                    in.nextLine();
                    String username, password;
                    switch (option) {
                        case 1: //login
                            System.out.println("LOGIN");
                            System.out.println("-----");
                            boolean successful = false;
                            while (!successful){
                                System.out.println("Please input your username: ");
                                username = in.nextLine();
                                System.out.println("Please input your password: ");
                                password = in.nextLine();

                                successful = UserDAO.login(conn, username, password);
                                if(!successful){
                                    System.out.println("Username or password was incorrect.");
                                    System.out.println("Please try again, or enter q to stop the login process.");
                                } else {
                                    List<User> users = UserDAO.searchUsersByUsername(conn, username);
                                    if(users.isEmpty()){
                                        System.out.println("No Users were found with that username.");
                                        System.out.println("Please try again, or enter q to stop the login process.");
                                    } else {
                                        logged_in = users.get(0);
                                        System.out.println("Successfully logged in!");
                                        System.out.println("Please press enter to continue");
                                    }
                                }

                                String exit = in.nextLine();
                                if(exit.equals("q")){
                                    // Way to exit the loop without confusing breaks, simply states that process is
                                    // done, and loop will exit without logged_in being set: logged_in = null
                                    successful = true;
                                }
                            }

                            break;
                        case 2: //create acc
                            System.out.println("CREATING ACCOUNT");
                            System.out.println("----------------");
                            final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", Pattern.CASE_INSENSITIVE);
                            final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z\\d]).{8,}$");

                            System.out.println("Registering account...");

                            while(true){
                                System.out.println("Please enter a username:");
                                username = in.nextLine();

                                List<User> users = UserDAO.searchUsersByUsername(conn, username);
                                if(!users.isEmpty()){
                                    System.out.println("That username is already being used.");
                                    System.out.println("Please enter another username.");
                                } else {
                                    break;
                                }
                            }

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

                            System.out.println("Please enter your first name:");
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

                            UserDAO.registerUser(conn, logged_in);
                            break;
                        case 3: //search
                            System.out.println("SEARCHING!");
                            System.out.println("----------");
                            searchHelperFunction(conn, null);
                            break;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    /**
     * Below are functions that assist in the multi-level search functions, which would be hard to read otherwise.
     * The first function is used to call the subsequent levels of the search functions: passing in the User object
     * if logged in, or null if not logged in for some of the searches.
     */

    public static void searchHelperFunction(Connection conn, User logged_in){
        Scanner in = new Scanner(System.in);
        System.out.println("What would you like to search for?");
        System.out.println("1. Search for Songs");
        System.out.println("2. Search for Users");
        System.out.println("3. Search for Artists");
        int searchChoice = in.nextInt();
        in.nextLine();

        switch (searchChoice) {
            case 1:
                songSearcher(conn);
                break;
            case 2:
                userSearcher(conn, logged_in);
                break;
            case 3:
                artistSearcher(conn, logged_in);
                break;
        }
    }

    public static void songSearcher(Connection conn){
        Scanner in = new Scanner(System.in);
        System.out.println("How would you like to search for a song?");
        System.out.println("1: By Song title");
        System.out.println("2: By Artist name");
        System.out.println("3: By Album name");
        System.out.println("4: By Genre name");
        int option = in.nextInt();
        in.nextLine();
        String typeCase = "";

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
            default:
                System.out.println("That was not an available option.");
        }
        String keyword = in.nextLine();

        System.out.println("How would you like to sort these songs?");
        System.out.println("1. Song name");
        System.out.println("2. Artist name");
        System.out.println("3. Genre");
        System.out.println("4. Release Date");
        String typeSearch = in.nextLine();

        System.out.println("Would you like this in ascending or descending order?");
        System.out.println("Enter Y (for yes) or N (for no)");
        String result = in.nextLine();
        boolean asc = !result.equals("N");

        // I'm pretty sure that this will correctly print the list of songs from the search without any other
        // edits. This function came in clutch!
        SongDAO.searchSongs(conn, keyword, typeCase, typeSearch, asc);
    }

    public static void userSearcher(Connection conn, User logged_in) {
        Scanner in = new Scanner(System.in);
        System.out.println("Would you like to search by?");
        System.out.println("1. Username");
        System.out.println("2. Email");
        int searchChoice = in.nextInt();
        in.nextLine();
        List<User> foundUsers = new ArrayList<>();
        String response = "";

        switch (searchChoice) {
            case 1:
                System.out.println("Enter in the username:");
                String name_of_user = in.nextLine();
                foundUsers = daos.UserDAO.searchUsersByUsername(conn, name_of_user);
                if (!foundUsers.isEmpty()) {
                    System.out.println(foundUsers.get(0));
                    if (logged_in != null) {
                        System.out.println("Would you also like to follow this user? (Y/N)");
                        response = in.nextLine();
                        if (response.equals("Y")) {
                            daos.UserDAO.followUser(conn, logged_in.getUsername(), foundUsers.get(0).getUsername());
                        }
                    }
                } else {
                    System.out.println(name_of_user + " was not found.");
                }
                break;
            case 2:
                System.out.println("Enter in the email:");
                String email_of_user = in.nextLine();
                foundUsers = daos.UserDAO.searchUsersByEmail(conn, email_of_user);
                if (!foundUsers.isEmpty()) {
                    System.out.println(foundUsers.get(0));
                } else {
                    System.out.println(email_of_user + " was not found.");
                }
                break;
        }

        if(logged_in != null && !foundUsers.isEmpty()){
            System.out.println("Would you also like to follow this user? (Y/N)");
            response = in.nextLine();
            if(response.equals("Y")){
                daos.UserDAO.followUser(conn, logged_in.getUsername(), foundUsers.get(0).getUsername());
            }
        }
    }

    public static void artistSearcher(Connection conn, User logged_in) {
        Scanner in = new Scanner(System.in);
        System.out.println("Enter the name of the artist:");
        String artist_name = in.nextLine();

        List<Artist> foundArtists = daos.ArtistDAO.getArtist(conn, artist_name);
        if (!foundArtists.isEmpty()) {
            System.out.println(foundArtists.get(0));
        } else {
            System.out.println(artist_name + " was not found.");
        }

        if(logged_in != null && !foundArtists.isEmpty()){
            System.out.println("Would you also like to follow this artist? (Y/N)");
            String response = in.nextLine();
            if(response.equals("Y")){
                daos.ArtistDAO.followArtist(conn, logged_in.getUsername(), foundArtists.get(0).getId());
            }
        }

    }
}
