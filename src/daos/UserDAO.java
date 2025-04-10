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

    public static boolean followUser(Connection conn, String username, String followUsername) {
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

    public static boolean unfollowUser(Connection conn, String username, String followUsername) {
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

    public static List<User> searchUsersByUsername(Connection conn, String username) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT username, password, first, last, email, date_of_birth, last_accessed_date, creation_date " +
                "FROM users WHERE username = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
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

                if (users.isEmpty()) {
                    System.out.println("No users found with username: " + username);
                }

            }

        } catch (SQLException e) {
            System.out.println("Error searching users by username: " + e.getMessage());
        }

        return users;
    }
    public static List<User> searchUsersByEmail(Connection conn, String email) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT username, password, first, last, email, date_of_birth, last_accessed_date, creation_date " +
                "FROM users WHERE email = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
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

                if (users.isEmpty()) {
                    System.out.println("No users found with the email: " + email);
                }

            }

        } catch (SQLException e) {
            System.out.println("Error searching users with the email: " + e.getMessage());
        }

        return users;
    }

    // Add to PlaylistDAO class
    public static int getUserPlaylistCount(Connection conn, String username) {
        String sql = "SELECT COUNT(DISTINCT playlist_name) AS playlist_count FROM playlist WHERE username = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("playlist_count");
            }
        } catch (SQLException e) {
            System.out.println("Error getting playlist count: " + e.getMessage());
        }
        return 0;
    }

    // Add to UserDAO class
    public static int getFollowingCount(Connection conn, String username) {
        String sql = "SELECT COUNT(*) AS following_count FROM following_user WHERE username = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("following_count");
            }
        } catch (SQLException e) {
            System.out.println("Error getting following count: " + e.getMessage());
        }
        return 0;
    }

    public static int getFollowerCount(Connection conn, String username) {
        String sql = "SELECT COUNT(*) AS follower_count FROM following_user WHERE following = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("follower_count");
            }
        } catch (SQLException e) {
            System.out.println("Error getting follower count: " + e.getMessage());
        }
        return 0;
    }



}
