package daos;
import java.sql.*;

public class SongDAO {

    public static void searchSongs(Connection conn, String keyword, String sortBy, boolean ascending) {
        String baseSQL = "SELECT s.title, a.name AS artist_name, al.name AS album_name, s.song_length, " +
                         "(SELECT COUNT(*) FROM play_history ph WHERE ph.song_id = s.song_id) AS listen_count " +
                         "FROM song s " +
                         "LEFT JOIN song_written_by swb ON s.song_id = swb.song_id " +
                         "LEFT JOIN artist a ON swb.artist_id = a.id " +
                         "LEFT JOIN songs_on_album soa ON s.song_id = soa.song_id " +
                         "LEFT JOIN album al ON soa.album_id = al.album_id " +
                         "WHERE s.title LIKE ? OR a.name LIKE ? OR al.name LIKE ?";

        String sortClause = String.format(" ORDER BY %s %s", sortBy, ascending ? "ASC" : "DESC");

        try (PreparedStatement stmt = conn.prepareStatement(baseSQL + sortClause)) {

            String searchParam = "%" + keyword + "%";
            stmt.setString(1, searchParam);
            stmt.setString(2, searchParam);
            stmt.setString(3, searchParam);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String title = rs.getString("title");
                String artistName = rs.getString("artist_name");
                String albumName = rs.getString("album_name");
                int length = rs.getInt("song_length");
                int listenCount = rs.getInt("listen_count");

                System.out.printf("Song: %s | Artist: %s | Album: %s | Length: %d secs | Plays: %d\n",
                        title, artistName, albumName, length, listenCount);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
