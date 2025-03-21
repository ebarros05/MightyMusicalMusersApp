package daos;
import java.sql.*;

public class PlaylistDAO {

    public static boolean createPlaylist(Connection conn, String playlistName, int playlistNumber, String username, int firstSong) {
        String sql = "INSERT INTO playlist (playlist_name, playlist_number, username, song_id) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, playlistName);
            stmt.setInt(2, playlistNumber);
            stmt.setString(3, username);
            stmt.setInt(4, firstSong); // can't have empty playlist, must add a first song
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void renamePlaylist(Connection conn, String username, String oldName, String newName) {
        String sql = "UPDATE playlist SET playlist_name = ? WHERE username = ? AND playlist_name = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newName);
            stmt.setString(2, username);
            stmt.setString(3, oldName);
            stmt.executeUpdate();
            System.out.println("Renamed playlist: '"+oldName+"'' to: '"+newName+"'");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deletePlaylist(Connection conn, String username, String playlistName) {
        String sql = "DELETE FROM playlist WHERE username = ? AND playlist_name = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, playlistName);
            stmt.executeUpdate();
            System.out.println("User: "+username+" deleted playlist: "+playlistName);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addSongToPlaylist(Connection conn, String username, String playlistName, int playlistNumber, int songId) {
        String sql = "INSERT INTO playlist (playlist_name, playlist_number, username, song_id) VALUES (?, ?, ?, ?)";
    
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setString(1, playlistName);
            stmt.setInt(2, playlistNumber);
            stmt.setString(3, username);
            stmt.setInt(4, songId);
            stmt.executeUpdate();
    
            System.out.println("Added song #" + songId + " to playlist: " + playlistName);
    
        } catch (SQLException e) {
            // PostgreSQL duplicate key violation SQLState is 23505
            if (e.getSQLState().equals("23505")) {
                System.out.println("There is already a song in that playlist-song-id spot: (" 
                    + playlistName + ", " + playlistNumber + ")");
            } else {
                // For other SQL exceptions, just print the stack trace
                e.printStackTrace();
            }
        }
    }
    

    public static void removeSongFromPlaylist(Connection conn, String username, String playlistName, int song_id) {
        String sql = "DELETE FROM playlist WHERE username = ? AND playlist_name = ? AND song_id = ?";
    
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setString(1, username);
            stmt.setString(2, playlistName);
            stmt.setInt(3, song_id);
    
            int rowsDeleted = stmt.executeUpdate();
    
            if (rowsDeleted > 0) {
                System.out.println("Deleted song ID #" + song_id + " from playlist: " + playlistName);
            } else {
                System.out.println("No song found with song ID #" + song_id + " in playlist: " + playlistName);
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addAlbumToPlaylist(Connection conn, String username, String playlistName, int playlistNumber, int albumId) {
        String sql = "SELECT s.song_id FROM songs_on_album AS s INNER JOIN album AS a ON a.album_id = s.album_id where a.album_id = ?";

        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, albumId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int songId = rs.getInt("song_id");
                    addSongToPlaylist(conn, username, playlistName, playlistNumber, songId);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteAlbumFromPlaylist(Connection conn, String username, String playlistName, int playlistNumber, int albumId) {
        String sql = "SELECT s.song_id FROM songs_on_album AS s INNER JOIN album AS a ON a.album_id = s.album_id where a.album_id = ?";

        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, albumId);
            try(ResultSet rs = stmt.executeQuery()) {
                while(rs.next()) {
                    int songId = rs.getInt("song_id");
                    removeSongFromPlaylist(conn, username, playlistName, songId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void displayPlaylist(Connection conn, String username, String playlistName) {
        String sql = "SELECT playlist_number, song_id FROM playlist WHERE username = ? AND playlist_name = ? ORDER BY playlist_number ASC";
    
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setString(1, username);
            stmt.setString(2, playlistName);
    
            try (ResultSet rs = stmt.executeQuery()) {
    
                System.out.println("Playlist: " + playlistName + " (User: " + username + ")");
                boolean hasSongs = false;
    
                while (rs.next()) {
                    int playlistNumber = rs.getInt("playlist_number");
                    int songId = rs.getInt("song_id");
    
                    System.out.println("  [" + playlistNumber + "] Song ID: " + songId);
                    hasSongs = true;
                }
    
                if (!hasSongs) {
                    System.out.println("  (No songs in this playlist.)");
                }
    
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void displayUserPlaylists(Connection conn, String username) {
        String sql = "SELECT p.playlist_name, MAX(p.playlist_number) + 1 AS Number_of_Songs, SUM(s.song_length) AS Total_Duration FROM playlist as p INNER JOIN song as s ON p.song_id = s.song_id WHERE p.username = ? GROUP BY p.playlist_name";

        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("User " + username + "'s Playlists: ");
                boolean hasPlaylists = false;
                while (rs.next()) {
                    String playlistName = rs.getString("playlist_name");
                    int numSongs = rs.getInt("Number_of_Songs");
                    int totalDuration = rs.getInt("Total_Duration");

                    System.out.println("  [" + playlistName + "] Num Songs: " + numSongs + " Total Length: " + totalDuration);

                    hasPlaylists = true;
                }

                if (!hasPlaylists) {
                    System.out.println("  (No playlists for this user.)");
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
    
}
