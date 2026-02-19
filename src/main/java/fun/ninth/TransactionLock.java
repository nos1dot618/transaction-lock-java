package fun.ninth;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionLock {
    private static final String LOCK_ACQUISITION_QUERY =
            "SELECT * FROM TransactionLock WITH (ROWLOCK, UPDLOCK, HOLDLOCK) WHERE Purpose=?";

    private final Connection connection;
    private final String purpose;
    private boolean lockAcquired = false;

    public TransactionLock(Connection connection, String purpose) {
        this.connection = connection;
        this.purpose = purpose;

    }

    public void acquire() throws SQLException {
        if (lockAcquired) throw new IllegalStateException("Lock already acquired.");

        connection.setAutoCommit(false);
        PreparedStatement statement = connection.prepareStatement(LOCK_ACQUISITION_QUERY);
        statement.setString(1, purpose);

        ResultSet resultSet = statement.executeQuery();
        if (!resultSet.next()) throw new SQLException("Row not found for locking.");

        lockAcquired = true;
    }

    public void release() throws SQLException {
        if (!lockAcquired) return;

        connection.commit();
        connection.setAutoCommit(true);
        lockAcquired = false;
    }

    public void rollback() throws SQLException {
        if (!lockAcquired) return;

        connection.rollback();
        connection.setAutoCommit(true);
        lockAcquired = false;
    }
}
