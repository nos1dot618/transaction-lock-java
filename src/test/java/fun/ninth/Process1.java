package fun.ninth;

import java.sql.Connection;
import java.sql.SQLException;

public class Process1 {
    private static final String PURPOSE = "lock_test";

    public static void main(String[] args) {
        try (Connection connection = DbConnection.getConnection()) {

            TransactionLock transactionLock = new TransactionLock(connection, PURPOSE);
            try {
                transactionLock.acquire();
                // Add breakpoint below.
                System.out.println("Lock acquired.");
                transactionLock.release();
                System.out.println("Lock released.");
            } catch (Exception e) {
                transactionLock.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
