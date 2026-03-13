package exercises.reactive;

import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * Reactive REST controller for the Stock Ticker (Exercise 2).
 *
 * <p>Streams real-time stock prices via Server-Sent Events using
 * Project Reactor's {@link Flux}.</p>
 *
 * TODO:
 * <ul>
 *   <li>Extract the price generator into a {@code StockPriceService} bean.</li>
 *   <li>Inject a {@code ReactiveCrudRepository<StockPrice, Long>} and persist
 *       every generated price.</li>
 *   <li>Implement the history endpoint using the reactive repository.</li>
 *   <li>Add backpressure handling ({@code onBackpressureDrop()}) for slow clients.</li>
 *   <li>Implement the portfolio aggregation endpoint that merges multiple symbol streams.</li>
 *   <li>Bonus: add a simple moving average (SMA) reactive pipeline.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/stocks")
public class StockPriceController {

    private static final List<String> DEFAULT_SYMBOLS = List.of(
            "AAPL", "GOOG", "MSFT", "AMZN", "META",
            "TSLA", "NVDA", "NFLX", "JPM", "V"
    );

    private final Random random = new Random();

    /**
     * Streams stock prices for all (or selected) symbols via SSE.
     *
     * <p>Each event is a {@link ServerSentEvent} containing a {@link StockPrice}
     * record. Events are emitted every 500 ms.</p>
     *
     * @param symbols optional comma-separated list of symbols to filter
     * @return an infinite Flux of SSE stock price events
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<StockPrice>> streamPrices(
            @RequestParam(required = false) List<String> symbols) {

        List<String> activeSymbols = (symbols != null && !symbols.isEmpty())
                ? symbols
                : DEFAULT_SYMBOLS;

        // TODO: replace with a shared hot Flux from StockPriceService
        // TODO: persist each price to the reactive repository
        // TODO: add onBackpressureDrop() for slow consumers

        return Flux.interval(Duration.ofMillis(500))
                .flatMap(tick -> Flux.fromIterable(activeSymbols)
                        .map(this::generatePrice))
                .map(price -> ServerSentEvent.<StockPrice>builder()
                        .id(String.valueOf(System.nanoTime()))
                        .event("stock-price")
                        .data(price)
                        .build());
    }

    /**
     * Returns recent price history for a single symbol.
     *
     * @param symbol stock ticker symbol
     * @param minutes how many minutes of history to return (default 30)
     * @return Flux of recent prices
     */
    @GetMapping("/{symbol}/history")
    public Flux<StockPrice> history(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "30") int minutes) {
        // TODO: query reactive repository for prices within the time range
        //
        // return stockPriceRepository
        //         .findBySymbolAndTimestampAfter(symbol, LocalDateTime.now().minusMinutes(minutes));

        return Flux.empty();
    }

    /**
     * Streams aggregated portfolio value for the given symbols.
     *
     * @param symbols comma-separated list of symbols
     * @return Flux of portfolio snapshots
     */
    @GetMapping(value = "/portfolio", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<Object>> portfolio(@RequestParam List<String> symbols) {
        // TODO: merge individual symbol streams, compute total portfolio value,
        //       emit aggregated snapshots via SSE

        return Flux.empty();
    }

    private StockPrice generatePrice(String symbol) {
        double basePrice = switch (symbol) {
            case "AAPL" -> 178.0;
            case "GOOG" -> 141.0;
            case "MSFT" -> 378.0;
            case "AMZN" -> 185.0;
            case "META" -> 505.0;
            case "TSLA" -> 248.0;
            case "NVDA" -> 880.0;
            case "NFLX" -> 610.0;
            case "JPM"  -> 198.0;
            case "V"    -> 280.0;
            default      -> 100.0;
        };
        double variation = basePrice * 0.02 * (random.nextDouble() - 0.5);
        double price = Math.round((basePrice + variation) * 100.0) / 100.0;

        return new StockPrice(symbol, price, LocalDateTime.now());
    }

    /**
     * Immutable record representing a stock price at a point in time.
     *
     * TODO: move to its own file and add R2DBC annotations
     * ({@code @Table}, {@code @Id}) for persistence.
     */
    public record StockPrice(String symbol, double price, LocalDateTime timestamp) {}
}
