public class DBOps {

    public static void createUser(String newUsername, String newPassword){

        String stmt =  "INSERT INTO users(username, password) VALUES (newUsername, newPassword)";

    }

    public static void login(String enteredUsername, String enteredPassword){

        String loginStmt = "SELECT COUNT(*) FROM users WHERE username = enteredUsername AND password = enteredPassword";

        // We need to change this to check if the statement returned 1
        // (i.e. it found one user with that username and password
        if(loginStmt != null){

            String updateStmt = "UPDATE users SET last_accessed_date = CURRENT_TIMESTAMP WHERE username = enteredUsername";

        }

    }

    public static void addSongToPlaylist(String playlistName, String username, int songID){

        String stmt =  "INSERT INTO playlist (username, playlist_name, playlist_number, song_id) VALUES (username, playlistName, (SELECT COUNT(*) FROM playlist WHERE playlist_name = playlistName AND username = username;) + 1, songID)";

    }

    public static void displayPlaylistsForUser(String username){

        String stmt = "SELECT p.playlist_name AS 'Playlist Name', " +
                "COUNT(*) AS 'Number of Songs on Playlist', " +
                "SUM(s.length) AS 'Length of Playlist (m)' " +
                "FROM playlist AS p " +
                "INNER JOIN song AS s ON p.song_id = s.song_id " +
                "WHERE p.username = username " +
                "GROUP BY p.playlist_name";

    }

    public static void changePlaylistName(String username, String oldPlaylistName, String newPlaylistName){

        String stmt = "UPDATE playlist SET playlist_name = newPlaylistName WHERE username = username AND playlist_name = oldPlaylistName;";

    }

    public static void playSong(String username, int songID){

        String stmt = "INSERT INTO play_history (username, song_id, time) VALUES (username, song_id, CURRENT_TIMESTAMP)";

    }

    public static void playPlaylist(String username, String playlistName){

        String stmt = "SELECT song_id FROM playlist WHERE username = username AND playlist_name = playlistName";

        // Need to update this so that results contains the song id's returned by the select
        int[] results = new int[0];
        for(int result : results){

            playSong(username, result);

        }

    }

    public static void followUser(String username, String followedUsername){

        String stmt = "INSERT INTO following_user (username, following) VALUES (username, followedUsername";

    }

    public static void unfollowUser(String username, String followedUsername){

        String stmt = "DELETE FROM following_user WHERE username = username AND following = followedUsername";

    }

    public static void followArtist(String username, int artistID){

        String stmt = "INSERT  INTO following_artist (username, following) VALUES (username, artist_id)";

    }

    public static void unfollowArtist(String username, int artistID){

        String stmt = "DELETE FROM following_artist WHERE username = username AND following = artist_id";

    }

    // Add ability to lookup an artist by name
    // Add song lookup ability - should be able to lookup/filter by title, genre, etc.

}
