package fun.ninth;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomSequenceRepository {

    public static int getSequence(Connection connection, int id) throws SQLException {
        String query = "SELECT Seq FROM CustomSequence WHERE Id=?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);

            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) throw new SQLException(String.format("Sequence with Id '%d' not found.", id));
                return rs.getInt("Seq");
            }
        }
    }

    public static void setSequence(Connection connection, int id, int seq) throws SQLException {
        String sql = "UPDATE CustomSequence SET Seq=? WHERE Id=?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, seq);
            statement.setInt(2, id);

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated == 0) throw new SQLException(String.format("Sequence with Id '%d' not found.", id));
        }
    }
}
