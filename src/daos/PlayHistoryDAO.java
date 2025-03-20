package daos;
import java.sql.*;
import java.time.LocalDateTime;

public class PlayHistoryDAO {

    public static void playSong(Connection conn, String username, int songId) {
        String sql = "INSERT INTO play_history (time, username, song_id) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setString(2, username);
            stmt.setInt(3, songId);
            stmt.executeUpdate();

            System.out.println("Song " + songId + " marked as played by " + username);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void playPlaylist(Connection conn, String username, String playlistName) {
        String sql = "SELECT s.song_id FROM playlist AS p INNER JOIN song AS s ON p.song_id = s.song_id WHERE p.username = ? AND playlist_name = ?";

        try(PreparedStatement stmt =  conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, playlistName);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int songId = rs.getInt("song_id");
                    playSong(conn, username, songId);
                }
            }

        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
