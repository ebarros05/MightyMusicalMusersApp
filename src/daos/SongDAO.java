package daos;

import models.Genre;
import models.Song;

import java.sql.*;
import java.sql.Date;
import java.util.*;

import static daos.PlaylistDAO.addSongToPlaylist;

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

    public static Song getSongById(Connection conn, int songId) throws SQLException {
        String query = "SELECT s.song_id, s.title, s.song_length, " +
                "       (SELECT MIN(a.release_date) " +
                "        FROM songs_on_album sa " +
                "        JOIN album a ON sa.album_id = a.album_id " +
                "        WHERE sa.song_id = s.song_id) AS release_date, " +
                "       g.genre_id, g.genre_type " +
                "FROM song s " +
                "JOIN genre g ON s.genre_id = g.genre_id " +
                "WHERE s.song_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, songId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("song_id");
                    String title = rs.getString("title");
                    int length = rs.getInt("song_length");
                    Date releaseDate = rs.getDate("release_date");
                    int genreId = rs.getInt("genre_id");
                    String genreType = rs.getString("genre_type");

                    Genre genre = new Genre(genreId, genreType);
                    return new Song(id, title, length, releaseDate, genre);
                } else {
                    return null;
                }
            }
        }
    }


    public static Genre getGenreById(Connection conn, int genreId) {
        String sql = "SELECT genre_id, name FROM genre WHERE genre_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, genreId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("genre_id");
                String name = rs.getString("name");

                return new Genre(id, name);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // or throw an exception if genre should *always* exist
    }

    public static List<Song> getSongsByAlbum(Connection conn, int albumId) {
        String albumSql = "SELECT release_date, genre_id FROM album WHERE album_id = ?";
        String songsSql = "SELECT s.song_id, s.title, s.song_length " +
                "FROM song s JOIN songs_on_album sa ON s.song_id = sa.song_id " +
                "WHERE sa.album_id = ? ORDER BY s.track_number";

        List<Song> songs = new ArrayList<>();

        Date albumReleaseDate = null;
        Genre albumGenre = null;

        // Get release date and genre from the album
        try (PreparedStatement stmt = conn.prepareStatement(albumSql)) {
            stmt.setInt(1, albumId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                albumReleaseDate = rs.getDate("release_date");
                int genreId = rs.getInt("genre_id");

                // Assuming you have a method to get a Genre object from its ID
                albumGenre = getGenreById(conn, genreId);

            } else {
                System.out.println("Album not found!");
                return songs; // empty list
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return songs;
        }

        // Get songs on the album
        try (PreparedStatement stmt = conn.prepareStatement(songsSql)) {
            stmt.setInt(1, albumId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int songId = rs.getInt("song_id");
                String title = rs.getString("title");
                int songLength = rs.getInt("song_length");

                Song song = new Song(songId, title, songLength, albumReleaseDate, albumGenre);
                songs.add(song);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return songs;
    }

    public static void addSongAlbumToPlaylist(Connection conn, String username, String playlistName, int albumId){
        String sql = "SELECT s.song_id FROM songs_on_album AS s INNER JOIN album AS a ON a.album_id = s.album_id where a.album_id = ?";

        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, albumId);
            try (ResultSet rs = stmt.executeQuery()) {
                ArrayList<Integer> songlistt = new ArrayList<>();
                while (rs.next()) {
                    songlistt.add(rs.getInt("song_id"));
                }
                int playlistNumber = 1;
                for (Integer song_id: songlistt) {
                    addSongToPlaylist(conn, username, playlistName, playlistNumber, song_id);
                    playlistNumber++;
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void top_genres(Connection conn){
        String sql = "SELECT " +
                "    g.genre_type, " +
                "    COUNT(h.song_id) " +
                "        AS play_count " +
                "    FROM " +
                "        play_history as h " +
                "        INNER JOIN song AS s " +
                "            ON h.song_id = s.song_id " +
                "        INNER JOIN genre as g " +
                "            ON s.genre_id = g.genre_id " +
                "    WHERE " +
                "        h.time >= date_trunc('month', CURRENT_DATE)" +
                "    AND " +
                "        h.time < date_trunc('month', CURRENT_DATE) + INTERVAL '1 month'" +
                "    GROUP BY " +
                "        g.genre_type " +
                "    ORDER BY " +
                "        play_count DESC " +
                "    LIMIT 5 ";

        try(PreparedStatement stmt  = conn.prepareStatement(sql)) {
            System.out.println("Top 5 most popular genres in the last month: ");
            try (ResultSet rs = stmt.executeQuery()) {
                int count = 1;
                while (rs.next()) {
                    System.out.println("#" + count + ": " + rs.getString("genre_type") + ", Play Count: " + rs.getInt("play_count"));
                    count++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void play_history_song_recommendations(Connection conn, String username) {
        String sql = "SELECT DISTINCT s.song_id, s.title " +
                "FROM song s " +
                "JOIN song_written_by sw ON s.song_id = sw.song_id " +
                "WHERE sw.artist_id IN ( " +
                "    SELECT DISTINCT swb.artist_id " +
                "    FROM play_history ph " +
                "    JOIN song_written_by swb ON ph.song_id = swb.song_id " +
                "    WHERE ph.username = ? " +
                ") " +
                "AND s.song_id NOT IN ( " +
                "    SELECT song_id " +
                "    FROM play_history " +
                "    WHERE username = ? )";

        try (Statement disableParallel = conn.createStatement()) {
            disableParallel.execute("SET max_parallel_workers_per_gather = 0");
        } catch (SQLException e) {
            System.err.println("Warning: Couldn't disable parallel workers.");
            e.printStackTrace();
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, username);

            try (ResultSet rs = stmt.executeQuery()) {
                List<String> songList = new ArrayList<>();

                while (rs.next()) {
                    int songId = rs.getInt("song_id");
                    String title = rs.getString("title");
                    songList.add(songId + ": " + title);
                }

                Collections.shuffle(songList);

                System.out.println("Here's 5 Random song recommendations based on your play history:");
                for (int i = 0; i < Math.min(5, songList.size()); i++) {
                    System.out.println(songList.get(i));
                }

            } catch (SQLException e) {
                System.out.println("bro what: " + e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public static List<String> followers_song_recommendations(Connection conn, String username) {
        List<String> titles = PlayHistoryDAO.displayTopSongsMyFollowers(conn,username, false);
        if(titles == null || titles.isEmpty()) {
            System.out.println("No Recommendations found for " + username);
            return titles;
        }

        int numSongs = 5;

        //List<String> temp = Arrays.asList(titles);
        Collections.shuffle(titles);
        //String[] finalRecs = temp.toArray(new String[0]);

        System.out.println();
        for(int i = 0; i < numSongs; i++){
            System.out.println(titles.get(i));
        }

        return titles;
    }
}