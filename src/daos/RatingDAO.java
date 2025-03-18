package daos;

import java.sql.*;

public class RatingDAO {

    public static void rateSong(Connection conn, Song song, int rating) {

        String sql = "INSERT INTO rating (song_id, rating) VALUES (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, song.getId());
            stme.setInt(2, rating);

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
