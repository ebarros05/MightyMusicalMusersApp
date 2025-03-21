package daos;

import models.Song;
import models.User;

import java.sql.PreparedStatement;
import java.sql.*;

public class RatingDAO {

    public static void rateSong(Connection conn, Song song, User user, int rating) {

        String sql = "INSERT INTO rating (stars, username, song_id) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, rating);
            stmt.setString(2, user.getUsername());
            stmt.setInt(3, song.getId());


            int rows = stmt.executeUpdate();

            if (rows != 1) {

                System.out.println("Rating not inserted");

            }else {

                System.out.println("Rating inserted");

            }

        } catch (SQLException e) {

            System.out.println("Error inserting rating: " + e.getMessage());

        }

    }

}
