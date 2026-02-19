package fun.ninth;

import java.sql.Connection;

public class ThreadAction implements Runnable {
    private static final int CHUNK_SIZE = 100;
    private static final String PURPOSE = "lock_test";
    private static final int PARTITION_ID = 1;
    private static final int CYCLES = 100;

    @Override
    public void run() {
        try {
            for (int i = 0; i < CYCLES; ++i) {
                int currentId = getCurrentId();
                int lastId = currentId + CHUNK_SIZE;
                try (Connection connection = DbConnection.getConnection()) {
                    for (int itemId = currentId; itemId < lastId; itemId++) {
                        ItemDataRepository.addItem(connection, PARTITION_ID, itemId);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int getCurrentId() throws Exception {
        try (Connection connection = DbConnection.getConnection()) {

            TransactionLock transactionLock = new TransactionLock(connection, PURPOSE);
            try {
                transactionLock.acquire();
                Thread.sleep(1000);
                int currentSequenceId = CustomSequenceRepository.getSequence(connection, PARTITION_ID);
                // Allocate chunk of size CHUNK_SIZE for Partition=PARTITION_ID.
                CustomSequenceRepository.setSequence(connection, PARTITION_ID, currentSequenceId + CHUNK_SIZE);
                transactionLock.release();
                return currentSequenceId;
            } catch (Exception e) {
                transactionLock.rollback();
                throw e;
            }
        }
    }
}
