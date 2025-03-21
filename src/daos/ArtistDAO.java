package daos;

import java.sql.*;

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

}