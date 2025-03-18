package daos;

import java.sql.*;

public class ArtistDAO {

    public boolean followArtist(Connection conn, String followUsername, int artistID) {
        String sql = "INSERT INTO following_artist (username, following) VALUES (?,?)";

        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, followUsername);
            stmt.executeUpdate();
            return true;
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean unfollowArtist(Connection conn, String followUsername, int artistID) {
        String sql = "DELETE FROM following_artist WHERE username = ? AND following = ?";

        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, followUsername);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

}