package fun.ninth;

import java.util.concurrent.CountDownLatch;

public class TransactionLockTest {
    public static void main(String[] args) throws InterruptedException {
        int threadCount = 20;
        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);

        Runnable task = () -> {
            try {
                ready.countDown(); // Signal ready.
                start.await(); // Wait for simultaneous start.
                new ThreadAction().run();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };

        for (int i = 0; i < threadCount; i++) {
            new Thread(task, "Worker-" + i).start();
        }

        ready.await(); // Wait for all threads to be ready.
        System.out.println("All threads ready. STARTING...");
        start.countDown(); // Unleash all threads at once.
    }
}
