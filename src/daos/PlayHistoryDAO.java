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

    public static String[] displayTopSongsMonth(Connection conn) {

        String sql = "SELECT\n" +
                "    counts.title,\n" +
                "    counts.Count\n" +
                "FROM\n" +
                "    (SELECT\n" +
                "         --p.song_id,\n" +
                "         s.title,\n" +
                "         COUNT(p.song_id) AS Count\n" +
                "    FROM\n" +
                "        play_history as p\n" +
                "    INNER JOIN\n" +
                "        song as s\n" +
                "    ON\n" +
                "        p.song_id = s.song_id\n" +
                "    WHERE\n" +
                "        p.time >= CURRENT_TIMESTAMP - INTERVAL '30 days'\n" +
                "    GROUP BY\n" +
                "        --p.song_id,\n" +
                "        s.title\n" +
                "    ORDER BY\n" +
                "        Count DESC\n" +
                "    LIMIT 50\n" +
                "         ) as counts\n";

        String[] titles = null;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = stmt.executeQuery()) {

                titles = new String[rs.getFetchSize()];

                for (int i = 0; i < titles.length; i++) {

                    titles[i] = rs.getString("title");

                }

            }

        } catch (SQLException e) {

            e.printStackTrace();

        }

        if (titles != null) {

            System.out.println("Top 50 Songs in the Last 30 Days:");
            for (int i = 0; i < titles.length; i++) {

                System.out.println(i + ": " + titles[i]);

            }

        }

        return titles;

    }

}
