package exercises;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.*;

/**
 * Sales analytics built on the Stream API.
 *
 * <p>Each method takes a list of {@link Transaction} records and produces
 * an analytical result using stream pipelines, collectors, and reductions.</p>
 *
 * <p>Implement every method marked with {@code TODO}.</p>
 */
public class SalesAnalytics {

    private final List<Transaction> transactions;

    public SalesAnalytics(List<Transaction> transactions) {
        this.transactions = List.copyOf(transactions);
    }

    /**
     * Return the top N customers ranked by total spend (descending).
     *
     * <p>Hints: {@code groupingBy} customer, {@code reducing} amounts,
     * sort the entry set, limit to N.</p>
     *
     * @param n number of top customers to return
     * @return ordered list of (customer, totalSpend) pairs
     */
    public List<Map.Entry<String, BigDecimal>> topCustomersBySpend(int n) {
        // TODO: implement using groupingBy + reducing + sorting + limit
        throw new UnsupportedOperationException("TODO");
    }

    /**
     * Revenue by category per month.
     *
     * <p>Outer key: {@link YearMonth}, inner key: category, value: total revenue.</p>
     *
     * <p>Hints: nested {@code groupingBy} with a downstream collector.</p>
     *
     * @return nested map of monthly revenue by category
     */
    public Map<YearMonth, Map<String, BigDecimal>> revenueByCategPerMonth() {
        // TODO: implement with nested groupingBy
        throw new UnsupportedOperationException("TODO");
    }

    /**
     * Compute the running (cumulative) average of transaction amounts,
     * ordered by date.
     *
     * <p>Returns a list where index <i>i</i> is the average of
     * transactions 0 through <i>i</i>.</p>
     *
     * @return list of running averages
     */
    public List<BigDecimal> runningAverage() {
        // TODO: sort by date, then compute cumulative average
        throw new UnsupportedOperationException("TODO");
    }

    /**
     * Moving-window average: for each region, compute the average amount
     * over the last {@code windowSize} transactions (ordered by date).
     *
     * @param windowSize number of most-recent transactions to include
     * @return map of region → moving average
     */
    public Map<String, BigDecimal> movingAverageByRegion(int windowSize) {
        // TODO: group by region, sort each group by date, take last N, average
        throw new UnsupportedOperationException("TODO");
    }

    // Bonus: parallelTopCustomers(int n) — parallel stream version of topCustomersBySpend.
    //        Compare wall-clock time against the sequential version for a large dataset.
}
