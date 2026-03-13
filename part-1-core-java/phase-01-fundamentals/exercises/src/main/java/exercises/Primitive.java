package exercises;

import java.time.Duration;
import java.time.Instant;

public class Primitive {
    public static void main(String[] args) {
        // BAD: Creates ~1 million Integer objects due to autoboxing
        System.out.println(sum());
        // GOOD: Use primitives for computation
        System.out.println(sumPrimitive());
    }

    public static Long sum() {
        Instant start = Instant.now();
        Long sum = 0L;
        for (int i = 0; i < 1_000_000; i++) {
            sum += i; // sum = Long.valueOf(sum.longValue() + i) — box, unbox, rebox
        }
        Instant end = Instant.now();

        Duration duration = Duration.between(start, end);
        System.out.println("Execution time: " + duration.toMillis() + " ms");
        return sum;

    }

    public static long sumPrimitive() {
        Instant start = Instant.now();
        long sum = 0L;
        for (int i = 0; i < 1_000_000; i++) {
            sum += i; // Pure primitive arithmetic, no objects created
        }
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        System.out.println("Execution time: " + duration.toMillis() + " ms");
        return sum;
    }
}