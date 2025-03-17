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
}
