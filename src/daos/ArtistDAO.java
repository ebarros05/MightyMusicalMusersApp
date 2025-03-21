package daos;

import models.Artist;
import models.Genre;
import models.Song;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArtistDAO {

    public static boolean followArtist(Connection conn, String followUsername, int artistID) {
        String sql = "INSERT INTO following_artist (username, following) VALUES (?,?)";

        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, followUsername);
            stmt.setInt(2, artistID);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("Error following artist: " + e.getMessage());
            return false;
        }
    }

    public static boolean unfollowArtist(Connection conn, String followUsername, int artistID) {
        String sql = "DELETE FROM following_artist WHERE username = ? AND following = ?";

        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, followUsername);
            stmt.setInt(2, artistID);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("Error unfollowing artist: " + e.getMessage());
            return false;
        }
    }

    public static List<Artist> getArtist(Connection conn, String artist_name){
        String sql = "Select id, name FROM artist WHERE name = ?";
        List<Artist> artists = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, artist_name);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Artist artist = new Artist(
                            rs.getInt("id"),
                            rs.getString("name")
                    );
                    artists.add(artist);
                }

                if (artists.isEmpty()) {
                    System.out.println("No Artist has the name " + artist_name);
                }

            }

        } catch (SQLException e) {
            System.out.println("Error searching by Artists Name: " + e.getMessage());
        }

        return artists;
    }

}