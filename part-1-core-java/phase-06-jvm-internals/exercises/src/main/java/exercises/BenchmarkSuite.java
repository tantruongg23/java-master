package exercises;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

/**
 * Exercise 3 — JMH Benchmarking Suite
 *
 * <p>Benchmark common Java operations to build intuition about performance.
 * Each benchmark method compares two or more approaches to the same problem.</p>
 *
 * <h3>Running</h3>
 * <pre>
 * mvn clean package
 * java -jar target/benchmarks.jar
 * </pre>
 *
 * Or run specific benchmarks:
 * <pre>
 * java -jar target/benchmarks.jar "BenchmarkSuite.arrayList.*"
 * </pre>
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(3)
@State(Scope.Benchmark)
public class BenchmarkSuite {

    // ──────────────────────────────────────────────────────────────
    // Shared state
    // ──────────────────────────────────────────────────────────────

    @Param({"1000", "10000", "100000"})
    private int size;

    private ArrayList<Integer> arrayList;
    private LinkedList<Integer> linkedList;
    private HashMap<String, Integer> hashMap;
    private TreeMap<String, Integer> treeMap;
    private String[] wordsForConcat;

    @Setup(Level.Trial)
    public void setup() {
        Random rng = new Random(42);

        arrayList = new ArrayList<>(size);
        linkedList = new LinkedList<>();
        hashMap = new HashMap<>(size);
        treeMap = new TreeMap<>();

        for (int i = 0; i < size; i++) {
            arrayList.add(rng.nextInt());
            linkedList.add(rng.nextInt());
            String key = "key-" + i;
            hashMap.put(key, rng.nextInt());
            treeMap.put(key, rng.nextInt());
        }

        wordsForConcat = new String[100];
        for (int i = 0; i < wordsForConcat.length; i++) {
            wordsForConcat[i] = "word" + i;
        }
    }

    // ──────────────────────────────────────────────────────────────
    // Benchmark 1 — ArrayList vs LinkedList iteration
    // ──────────────────────────────────────────────────────────────

    @Benchmark
    public void arrayListIteration(Blackhole bh) {
        // TODO: Iterate arrayList with a for-each loop.
        // TODO: Consume each element with bh.consume().
        throw new UnsupportedOperationException("TODO — implement arrayListIteration");
    }

    @Benchmark
    public void linkedListIteration(Blackhole bh) {
        // TODO: Iterate linkedList with a for-each loop.
        // TODO: Consume each element with bh.consume().
        throw new UnsupportedOperationException("TODO — implement linkedListIteration");
    }

    // ──────────────────────────────────────────────────────────────
    // Benchmark 2 — HashMap vs TreeMap lookup
    // ──────────────────────────────────────────────────────────────

    @Benchmark
    public void hashMapLookup(Blackhole bh) {
        // TODO: Look up a random key in hashMap.
        // TODO: Consume the result with bh.consume().
        throw new UnsupportedOperationException("TODO — implement hashMapLookup");
    }

    @Benchmark
    public void treeMapLookup(Blackhole bh) {
        // TODO: Look up the same random key in treeMap.
        // TODO: Consume the result with bh.consume().
        throw new UnsupportedOperationException("TODO — implement treeMapLookup");
    }

    // ──────────────────────────────────────────────────────────────
    // Benchmark 3 — String concatenation vs StringBuilder
    // ──────────────────────────────────────────────────────────────

    @Benchmark
    public String stringConcatenation() {
        // TODO: Concatenate all elements of wordsForConcat using +.
        throw new UnsupportedOperationException("TODO — implement stringConcatenation");
    }

    @Benchmark
    public String stringBuilderConcat() {
        // TODO: Concatenate all elements of wordsForConcat using StringBuilder.
        throw new UnsupportedOperationException("TODO — implement stringBuilderConcat");
    }

    // ──────────────────────────────────────────────────────────────
    // Benchmark 4 — Stream vs for-loop
    // ──────────────────────────────────────────────────────────────

    @Benchmark
    public long streamSum() {
        // TODO: Sum arrayList using stream().mapToInt(Integer::intValue).sum().
        throw new UnsupportedOperationException("TODO — implement streamSum");
    }

    @Benchmark
    public long forLoopSum() {
        // TODO: Sum arrayList using a traditional for loop.
        throw new UnsupportedOperationException("TODO — implement forLoopSum");
    }

    // ──────────────────────────────────────────────────────────────
    // Benchmark 5 — synchronized vs Lock vs Atomic
    // ──────────────────────────────────────────────────────────────

    private int synchronizedCounter;
    private final ReentrantLock lock = new ReentrantLock();
    private int lockCounter;
    private final AtomicInteger atomicCounter = new AtomicInteger();

    @Benchmark
    public int synchronizedIncrement() {
        // TODO: Increment synchronizedCounter inside a synchronized block.
        throw new UnsupportedOperationException("TODO — implement synchronizedIncrement");
    }

    @Benchmark
    public int lockIncrement() {
        // TODO: Increment lockCounter using lock.lock() / unlock().
        throw new UnsupportedOperationException("TODO — implement lockIncrement");
    }

    @Benchmark
    public int atomicIncrement() {
        // TODO: Increment atomicCounter using incrementAndGet().
        throw new UnsupportedOperationException("TODO — implement atomicIncrement");
    }

    // ──────────────────────────────────────────────────────────────
    // Bonus — Virtual threads vs platform threads (I/O bound)
    // ──────────────────────────────────────────────────────────────

    @Benchmark
    public void platformThreadsIO(Blackhole bh) {
        // TODO: Create 100 platform threads, each sleeping 1ms (simulating I/O).
        // TODO: Join all threads.
        throw new UnsupportedOperationException("TODO — implement platformThreadsIO");
    }

    @Benchmark
    public void virtualThreadsIO(Blackhole bh) {
        // TODO: Create 100 virtual threads, each sleeping 1ms.
        // TODO: Join all threads.
        throw new UnsupportedOperationException("TODO — implement virtualThreadsIO");
    }

    // ──────────────────────────────────────────────────────────────
    // Main — Run benchmarks programmatically
    // ──────────────────────────────────────────────────────────────

    public static void main(String[] args) throws RunnerException {
        Options opts = new OptionsBuilder()
                .include(BenchmarkSuite.class.getSimpleName())
                .build();
        new Runner(opts).run();
    }
}
