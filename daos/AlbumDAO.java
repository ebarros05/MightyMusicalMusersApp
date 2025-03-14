package daos;

import java.sql.*;
import java.util.Date;

public class AlbumDAO {

    public static boolean createAlbum(Connection conn, int albumId, String name, Date releaseDate, int genreId) {
        String sql = "INSERT INTO album (album_id, name, release_date, genre_id) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, albumId);
            stmt.setString(2, name);
            stmt.setDate(3, new java.sql.Date(releaseDate.getTime()));
            stmt.setInt(4, genreId);
            stmt.executeUpdate();
            System.out.println("Created album: " + name + " (ID: " + albumId + ")");
            return true;
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                System.out.println("Album ID " + albumId + " already exists");
            } else {
                e.printStackTrace();
            }
            return false;
        }
    }

    public static void renameAlbum(Connection conn, int albumId, String newName) {
        String sql = "UPDATE album SET name = ? WHERE album_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newName);
            stmt.setInt(2, albumId);
            int rowsUpdated = stmt.executeUpdate();
            
            if (rowsUpdated > 0) {
                System.out.println("Renamed album ID " + albumId + " to: " + newName);
            } else {
                System.out.println("No album found with ID: " + albumId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteAlbum(Connection conn, int albumId) {
        String deleteArtistsSql = "DELETE FROM album_written_by WHERE album_id = ?";
        String deleteSongsSql = "DELETE FROM songs_on_album WHERE album_id = ?";
        String deleteAlbumSql = "DELETE FROM album WHERE album_id = ?";

        try {
            conn.setAutoCommit(false);

            // Delete associated artists
            try (PreparedStatement stmt = conn.prepareStatement(deleteArtistsSql)) {
                stmt.setInt(1, albumId);
                stmt.executeUpdate();
            }

            // Delete associated songs
            try (PreparedStatement stmt = conn.prepareStatement(deleteSongsSql)) {
                stmt.setInt(1, albumId);
                stmt.executeUpdate();
            }

            // Delete album
            try (PreparedStatement stmt = conn.prepareStatement(deleteAlbumSql)) {
                stmt.setInt(1, albumId);
                int rowsDeleted = stmt.executeUpdate();
                
                if (rowsDeleted > 0) {
                    System.out.println("Deleted album ID: " + albumId);
                } else {
                    System.out.println("No album found with ID: " + albumId);
                }
            }

            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
                System.out.println("Transaction rolled back due to error");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void addSongToAlbum(Connection conn, int albumId, int songId) {
        String sql = "INSERT INTO songs_on_album (song_id, album_id) VALUES (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, songId);
            stmt.setInt(2, albumId);
            stmt.executeUpdate();
            System.out.println("Added song #" + songId + " to album #" + albumId);
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                System.out.println("Song #" + songId + " already exists in album #" + albumId);
            } else if (e.getSQLState().equals("23503")) {
                System.out.println("Invalid song or album ID");
            } else {
                e.printStackTrace();
            }
        }
    }

    public static void removeSongFromAlbum(Connection conn, int albumId, int songId) {
        String sql = "DELETE FROM songs_on_album WHERE song_id = ? AND album_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, songId);
            stmt.setInt(2, albumId);
            int rowsDeleted = stmt.executeUpdate();
            
            if (rowsDeleted > 0) {
                System.out.println("Removed song #" + songId + " from album #" + albumId);
            } else {
                System.out.println("Song #" + songId + " not found in album #" + albumId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addArtistToAlbum(Connection conn, int albumId, int artistId) {
        String sql = "INSERT INTO album_written_by (album_id, artist_id) VALUES (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, albumId);
            stmt.setInt(2, artistId);
            stmt.executeUpdate();
            System.out.println("Added artist #" + artistId + " to album #" + albumId);
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                System.out.println("Artist #" + artistId + " already associated with album #" + albumId);
            } else if (e.getSQLState().equals("23503")) {
                System.out.println("Invalid artist or album ID");
            } else {
                e.printStackTrace();
            }
        }
    }

    public static void removeArtistFromAlbum(Connection conn, int albumId, int artistId) {
        String sql = "DELETE FROM album_written_by WHERE album_id = ? AND artist_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, albumId);
            stmt.setInt(2, artistId);
            int rowsDeleted = stmt.executeUpdate();
            
            if (rowsDeleted > 0) {
                System.out.println("Removed artist #" + artistId + " from album #" + albumId);
            } else {
                System.out.println("Artist #" + artistId + " not associated with album #" + albumId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void displayAlbum(Connection conn, int albumId) {
        String albumSql = "SELECT name, release_date, genre_id FROM album WHERE album_id = ?";
        String songsSql = "SELECT s.song_id, s.title, s.track_number, s.song_length " +
                          "FROM song s JOIN songs_on_album sa ON s.song_id = sa.song_id " +
                          "WHERE sa.album_id = ? ORDER BY s.track_number";
        String artistsSql = "SELECT a.id, a.name FROM artist a " +
                            "JOIN album_written_by awb ON a.id = awb.artist_id " +
                            "WHERE awb.album_id = ?";

        try {
            // Album Info
            String albumName = null;
            Date releaseDate = null;
            int genreId = -1;
            
            try (PreparedStatement stmt = conn.prepareStatement(albumSql)) {
                stmt.setInt(1, albumId);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    albumName = rs.getString("name");
                    releaseDate = rs.getDate("release_date");
                    genreId = rs.getInt("genre_id");
                } else {
                    System.out.println("Album not found");
                    return;
                }
            }

            System.out.println("\nAlbum Details:");
            System.out.println("ID: " + albumId);
            System.out.println("Name: " + albumName);
            System.out.println("Release Date: " + releaseDate);
            System.out.println("Genre ID: " + genreId);

            // Songs
            System.out.println("\nSongs:");
            try (PreparedStatement stmt = conn.prepareStatement(songsSql)) {
                stmt.setInt(1, albumId);
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    System.out.printf("Track %d: %s (ID: %d, %d seconds)%n",
                            rs.getInt("track_number"),
                            rs.getString("title"),
                            rs.getInt("song_id"),
                            rs.getInt("song_length"));
                }
            }

            // Artists
            System.out.println("\nArtists:");
            try (PreparedStatement stmt = conn.prepareStatement(artistsSql)) {
                stmt.setInt(1, albumId);
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    System.out.printf("ID: %d - %s%n",
                            rs.getInt("id"),
                            rs.getString("name"));
                }
            }

            System.out.println();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
