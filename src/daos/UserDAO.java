package daos;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import models.User;

public class UserDAO {

    public static void registerUser(Connection conn, User user) {
        String sql = "INSERT INTO users (username, password, first, last, email, date_of_birth, last_accessed_date, creation_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getFirst());
            stmt.setString(4, user.getLast());
            stmt.setString(5, user.getEmail());
            stmt.setDate(6, user.getDateOfBirth());
            stmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("User inserted successfully!");
            } else {
                System.out.println("User insertion failed.");
            }
        } catch (SQLException e) {
            System.out.println("Error inserting user: " + e.getMessage());
        }
    }

    public static boolean login(Connection conn, String username, String password) {
        String sql = "SELECT password FROM users WHERE username = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String dbPassword = rs.getString("password");

                    if (dbPassword.equals(password)) {
                        updateLastAccess(conn, username);
                        return true;
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private static void updateLastAccess(Connection conn, String username) {
        String sql = "UPDATE users SET last_accessed_date = ? WHERE username = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setString(2, username);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean followUser(Connection conn, String username, String followUsername) {
        String sql = "INSERT INTO following_user (username, following) VALUES (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, followUsername);
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean unfollowUser(Connection conn, String username, String followUsername) {
        String sql = "DELETE FROM following_user WHERE username = ? AND following = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, followUsername);
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
    public static List<User> getAllUsers(Connection conn) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT username, password, first, last, email, date_of_birth, last_accessed_date, creation_date FROM users";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                User user = new User(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("first"),
                        rs.getString("last"),
                        rs.getString("email"),
                        rs.getDate("date_of_birth"),
                        rs.getTimestamp("last_accessed_date"),
                        rs.getTimestamp("creation_date")
                );
                users.add(user);
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving users: " + e.getMessage());
        }

        return users;
    }
}
