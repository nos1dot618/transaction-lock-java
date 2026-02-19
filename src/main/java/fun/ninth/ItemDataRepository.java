package fun.ninth;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ItemDataRepository {
    public static void addItem(Connection connection, int partitionId, int itemId) throws SQLException {
        String sql = "INSERT INTO ItemData (PartitionId, ItemId) VALUES (?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, partitionId);
            statement.setInt(2, itemId);
            statement.executeUpdate();
        }
    }
}
