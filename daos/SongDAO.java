package daos;

import java.sql.*;

public class SongDAO {

    public static void searchSongs(Connection conn, String keyword, String searchField, String sortBy, boolean ascending) {
        String baseSQL = "SELECT s.song_id, s.title, a.name AS artist_name, al.name AS album_name, " +
                        "s.song_length, g.genre_type, " +
                        "(SELECT COUNT(*) FROM play_history ph WHERE ph.song_id = s.song_id) AS listen_count " +
                        "FROM song s " +
                        "LEFT JOIN song_written_by swb ON s.song_id = swb.song_id " +
                        "LEFT JOIN artist a ON swb.artist_id = a.id " +
                        "LEFT JOIN songs_on_album soa ON s.song_id = soa.song_id " +
                        "LEFT JOIN album al ON soa.album_id = al.album_id " +
                        "LEFT JOIN genre g ON s.genre_id = g.genre_id ";

        String whereClause;
        boolean useIntParam = false;

        switch (searchField.toLowerCase()) {
            case "title":
                whereClause = "WHERE s.title LIKE ?";
                break;
            case "artist":
                whereClause = "WHERE a.name LIKE ?";
                break;
            case "album":
                whereClause = "WHERE al.name LIKE ?";
                break;
            case "genre":
                whereClause = "WHERE s.genre_id = ?";
                useIntParam = true;
                break;
            default:
                whereClause = "WHERE s.title LIKE ? OR a.name LIKE ? OR al.name LIKE ?";
        }

        String sortClause = String.format(" ORDER BY %s %s", sortBy, ascending ? "ASC" : "DESC");
        String fullSQL = baseSQL + whereClause + sortClause;

        try (PreparedStatement stmt = conn.prepareStatement(fullSQL)) {
            if (useIntParam) {
                int genreId = Integer.parseInt(keyword);
                stmt.setInt(1, genreId);
            } else if (whereClause.contains("OR")) {
                String searchParam = "%" + keyword + "%";
                stmt.setString(1, searchParam);
                stmt.setString(2, searchParam);
                stmt.setString(3, searchParam);
            } else {
                String searchParam = "%" + keyword + "%";
                stmt.setString(1, searchParam);
            }

            ResultSet rs = stmt.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("No songs found for " + searchField + ": " + keyword);
                return;
            }

            while (rs.next()) {
                int songId = rs.getInt("song_id");
                String title = rs.getString("title");
                String artistName = rs.getString("artist_name") != null ? rs.getString("artist_name") : "Unknown Artist";
                String albumName = rs.getString("album_name") != null ? rs.getString("album_name") : "Unknown Album";
                int length = rs.getInt("song_length");
                String genreType = rs.getString("genre_type") != null ? rs.getString("genre_type") : "Unknown Genre";
                int listenCount = rs.getInt("listen_count");

                System.out.printf("Song: %s (ID: %d) | Artist: %s | Album: %s | Genre: %s | Length: %d secs | Plays: %d\n",
                        title, songId, artistName, albumName, genreType, length, listenCount);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.out.println("Error: Genre search requires a numeric ID");
        }
    }
}