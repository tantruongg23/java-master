package exercises;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A functional, in-process event bus.
 *
 * <p>Handlers are registered as {@link Consumer Consumer&lt;Event&gt;} instances.
 * Multiple handlers for the same event type are composed with
 * {@link Consumer#andThen}.</p>
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * EventBus bus = new EventBus();
 *
 * bus.on("order.created", event -> processOrder(event));
 * bus.on("order.created", event -> sendConfirmation(event));
 *
 * bus.emit("order.created", new Event("order.created", orderData));
 * }</pre>
 *
 * @see Event
 */
public class EventBus {

    /**
     * Simple event record carrying a type and a payload.
     *
     * @param type    the event type identifier (e.g. "order.created")
     * @param payload arbitrary data attached to the event
     */
    public record Event(String type, Object payload) {}

    private final Map<String, List<Consumer<Event>>> handlers = new ConcurrentHashMap<>();

    /**
     * Register a handler for the given event type.
     *
     * @param eventType the event type to listen for
     * @param handler   the handler to invoke when the event is emitted
     */
    public void on(String eventType, Consumer<Event> handler) {
        // TODO: add the handler to the list for the given event type.
        //       Use CopyOnWriteArrayList for thread safety.
        handlers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>())
                .add(handler);
    }

    /**
     * Register a handler that only fires when the event matches a filter.
     *
     * @param eventType the event type to listen for
     * @param filter    predicate the event must satisfy
     * @param handler   the handler to invoke
     */
    public void on(String eventType, Predicate<Event> filter, Consumer<Event> handler) {
        // TODO: wrap handler so it only executes if filter.test(event) is true
        throw new UnsupportedOperationException("TODO: implement filtered on");
    }

    /**
     * Register a handler that receives a <em>mapped</em> event.
     *
     * @param eventType the event type to listen for
     * @param mapper    transforms the event before the handler sees it
     * @param handler   the handler to invoke with the mapped event
     */
    public void on(String eventType, Function<Event, Event> mapper, Consumer<Event> handler) {
        // TODO: compose mapper with handler — handler receives mapper.apply(event)
        throw new UnsupportedOperationException("TODO: implement mapped on");
    }

    /**
     * Emit an event, invoking all registered handlers for its type.
     *
     * @param eventType the type of event being emitted
     * @param event     the event instance
     */
    public void emit(String eventType, Event event) {
        // TODO: look up all handlers for eventType, invoke each with the event
        throw new UnsupportedOperationException("TODO: implement emit");
    }

    /**
     * Remove all handlers for a given event type.
     *
     * @param eventType the event type to clear
     */
    public void off(String eventType) {
        handlers.remove(eventType);
    }

    // TODO: Implement debounce — only deliver the last event in a time window.
    //   public void onDebounced(String eventType, long windowMs, Consumer<Event> handler)
    //
    // TODO: Implement event aggregation — collect N events, then emit a summary.
    //   public void onAggregated(String eventType, int batchSize, Consumer<List<Event>> handler)
    //
    // Bonus: Implement back-pressure with a bounded BlockingQueue per event type.
    //   - emit() blocks (or drops) if the queue is full.
    //   - A background thread drains the queue and dispatches to handlers.
}
