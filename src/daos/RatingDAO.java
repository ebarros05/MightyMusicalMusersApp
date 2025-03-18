package daos;

import java.sql.*;

public class RatingDAO {

    public static void rateSong(Connection conn, Song song, User user, int rating) {

        String sql = "INSERT INTO rating (song_id, rating) VALUES (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stme.setInt(1, rating);
            stmt.setUsername(2, user.getUsername());
            stmt.setInt(3, song.getId());


            int rows = stmt.executeUpdate();

            if (rows != 1) {

                System.out.println("Rating not inserted");

            }else {

                System.out.println("Rating inserted");

            }

        } catch (SQLEXception e) {

            System.out.println("Error inserting rating: " + e.getMessage());

        }

    }

}
