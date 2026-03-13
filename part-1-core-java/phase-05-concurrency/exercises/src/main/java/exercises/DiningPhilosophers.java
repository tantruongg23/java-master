package exercises;

import java.time.Duration;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Exercise 5 — Dining Philosophers
 *
 * <p>Classic concurrency problem: five philosophers sit at a round table.
 * Each needs two forks to eat. Demonstrates deadlock and three different solutions.</p>
 *
 * <h3>Implementations</h3>
 * <ol>
 *   <li>Naive — leads to deadlock.</li>
 *   <li>Resource ordering — pick up the lower-numbered fork first.</li>
 *   <li>tryLock with timeout — retry on failure.</li>
 *   <li>Conductor/Arbitrator — a semaphore limits concurrent eaters.</li>
 * </ol>
 *
 * <h3>Bonus</h3>
 * Re-implement with virtual threads.
 */
public class DiningPhilosophers {

    // ──────────────────────────────────────────────────────────────
    // Fork (shared resource)
    // ──────────────────────────────────────────────────────────────

    /**
     * Represents a fork on the table. Wraps a {@link ReentrantLock}.
     */
    static class Fork {
        private final int id;
        private final ReentrantLock lock = new ReentrantLock();

        Fork(int id) { this.id = id; }

        public int getId() { return id; }

        public void pickUp() { lock.lock(); }

        public void putDown() { lock.unlock(); }

        /**
         * Try to pick up within the given timeout.
         *
         * @return true if acquired
         */
        public boolean tryPickUp(Duration timeout) throws InterruptedException {
            return lock.tryLock(timeout.toMillis(), TimeUnit.MILLISECONDS);
        }
    }

    // ──────────────────────────────────────────────────────────────
    // Philosopher (thread task)
    // ──────────────────────────────────────────────────────────────

    /**
     * A philosopher that alternates between thinking and eating.
     */
    static class Philosopher implements Runnable {
        private final int id;
        private final Fork leftFork;
        private final Fork rightFork;
        private final int meals;
        private volatile boolean running = true;

        Philosopher(int id, Fork leftFork, Fork rightFork, int meals) {
            this.id = id;
            this.leftFork = leftFork;
            this.rightFork = rightFork;
            this.meals = meals;
        }

        @Override
        public void run() {
            // TODO: Override in each strategy subclass or pass a strategy function.
            throw new UnsupportedOperationException("TODO — implement run");
        }

        void think() {
            // TODO: Print state "THINKING", sleep random 50–200ms.
            throw new UnsupportedOperationException("TODO — implement think");
        }

        void eat() {
            // TODO: Print state "EATING", sleep random 50–200ms.
            throw new UnsupportedOperationException("TODO — implement eat");
        }

        void log(String state) {
            System.out.printf("[Philosopher %d] %s%n", id, state);
        }
    }

    // ──────────────────────────────────────────────────────────────
    // Strategy 1 — Naive (deadlock-prone)
    // ──────────────────────────────────────────────────────────────

    /**
     * Run the naive version where each philosopher picks up left fork first,
     * then right fork. This will likely deadlock.
     *
     * @param numPhilosophers number of philosophers (and forks)
     * @param mealsEach       number of meals each philosopher tries to eat
     */
    public void runNaive(int numPhilosophers, int mealsEach) {
        // TODO: Create forks and philosophers.
        // TODO: Each philosopher: pickUp(left), pickUp(right), eat, putDown(right), putDown(left).
        // TODO: Start threads and observe deadlock.
        throw new UnsupportedOperationException("TODO — implement runNaive");
    }

    // ──────────────────────────────────────────────────────────────
    // Strategy 2 — Resource Ordering
    // ──────────────────────────────────────────────────────────────

    /**
     * Fix deadlock by always picking up the lower-numbered fork first.
     */
    public void runResourceOrdering(int numPhilosophers, int mealsEach) {
        // TODO: Create forks and philosophers.
        // TODO: Each philosopher picks up min(left,right) first, then max(left,right).
        // TODO: This breaks the circular wait condition.
        throw new UnsupportedOperationException("TODO — implement runResourceOrdering");
    }

    // ──────────────────────────────────────────────────────────────
    // Strategy 3 — tryLock with Timeout
    // ──────────────────────────────────────────────────────────────

    /**
     * Fix deadlock by using {@code tryLock} with a timeout.
     * If a philosopher can't get both forks, they release and retry.
     */
    public void runTryLock(int numPhilosophers, int mealsEach) {
        // TODO: Each philosopher: tryPickUp(left, 100ms).
        // TODO: If successful, tryPickUp(right, 100ms).
        // TODO: If second fails, putDown(left) and retry after a random back-off.
        throw new UnsupportedOperationException("TODO — implement runTryLock");
    }

    // ──────────────────────────────────────────────────────────────
    // Strategy 4 — Conductor / Arbitrator
    // ──────────────────────────────────────────────────────────────

    /**
     * Fix deadlock by allowing at most (N-1) philosophers to attempt eating
     * simultaneously, using a {@link Semaphore}.
     */
    public void runConductor(int numPhilosophers, int mealsEach) {
        // TODO: Create a Semaphore with (numPhilosophers - 1) permits.
        // TODO: Each philosopher must acquire a permit before picking up forks.
        // TODO: Release the permit after putting down forks.
        throw new UnsupportedOperationException("TODO — implement runConductor");
    }

    // ──────────────────────────────────────────────────────────────
    // Bonus — Virtual Threads
    // ──────────────────────────────────────────────────────────────

    /**
     * Re-implement one of the above solutions using virtual threads.
     */
    public void runWithVirtualThreads(int numPhilosophers, int mealsEach) {
        // TODO: Use Thread.ofVirtual().start(...) for each philosopher.
        throw new UnsupportedOperationException("TODO — implement runWithVirtualThreads");
    }

    // ──────────────────────────────────────────────────────────────
    // Main
    // ──────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        DiningPhilosophers dp = new DiningPhilosophers();

        System.out.println("=== Strategy: Resource Ordering ===");
        dp.runResourceOrdering(5, 3);

        System.out.println("\n=== Strategy: tryLock ===");
        dp.runTryLock(5, 3);

        System.out.println("\n=== Strategy: Conductor ===");
        dp.runConductor(5, 3);
    }
}
