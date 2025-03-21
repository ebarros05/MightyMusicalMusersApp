package daos;

import models.Genre;
import models.Song;
import models.User;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

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
    public static Song getASong(Connection conn, String songName) throws SQLException {
        String query = "SELECT s.song_id, s.title, s.song_length, " +
                "       (SELECT MIN(a.release_date) " +
                "        FROM songs_on_album sa " +
                "        JOIN album a ON sa.album_id = a.album_id " +
                "        WHERE sa.song_id = s.song_id) AS release_date, " +
                "       g.genre_id, g.genre_type " +
                "FROM song s " +
                "JOIN genre g ON s.genre_id = g.genre_id " +
                "WHERE s.title = ? AND s.song_id = (SELECT MIN(song_id) FROM song WHERE title = ?)";

        // Use try-with-resources to auto-close statement and result set
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            // Set parameters for the song title
            stmt.setString(1, songName);
            stmt.setString(2, songName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Extract song attributes
                    int id = rs.getInt("song_id");
                    String title = rs.getString("title");
                    int length = rs.getInt("song_length");
                    Date releaseDate = rs.getDate("release_date");
                    int genreId = rs.getInt("genre_id");
                    String genreType = rs.getString("genre_type");

                    // Create Genre and Song objects
                    Genre genre = new Genre(genreId, genreType);
                    return new Song(id, title, length, releaseDate, genre);
                } else {
                    // No song found
                    return null;
                }
            }
        }
    }

    public static List<Song> getSong(Connection conn, String song_name){
        String sql = "Select song_id, title, song_length, genre FROM song WHERE title = ?";
        List<Song> songs = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, song_name);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Song song = new Song(
                            rs.getInt("song_id"),
                            rs.getString("title"),
                            rs.getInt("song_length"),
                            rs.getDate("releaseDate"),
                            (Genre) rs.getObject("genre")
                    );
                    songs.add(song);
                }

                if (songs.isEmpty()) {
                    System.out.println("No Songs found with Song title: " + song_name);
                }

            }

        } catch (SQLException e) {
            System.out.println("Error searching songs by Song title: " + e.getMessage());
        }

        return songs;
    }
}