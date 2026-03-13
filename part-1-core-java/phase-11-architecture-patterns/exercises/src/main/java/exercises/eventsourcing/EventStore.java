package exercises.eventsourcing;

import java.util.List;

/**
 * Persistence port for the event-sourced system.
 *
 * <p>An event store is an append-only log. Events, once written,
 * are immutable. The store supports loading the full history for
 * an aggregate or loading events after a given version (for
 * snapshot-based rehydration).
 *
 * <p><strong>Exercise 3:</strong> Implement an in-memory version
 * of this interface, then use it to rebuild account state.
 */
public interface EventStore {

    /**
     * Appends one or more events to the store.
     *
     * <p>Events must be appended atomically. If the expected version
     * does not match the current version in the store, an optimistic
     * concurrency exception should be thrown.
     *
     * @param events the events to append (in order)
     */
    void append(List<Event> events);

    /**
     * Convenience overload for appending a single event.
     *
     * @param event the event to append
     */
    default void append(Event event) {
        append(List.of(event));
    }

    /**
     * Loads all events for a given aggregate, in version order.
     *
     * @param aggregateId the aggregate identifier
     * @return all events for that aggregate (may be empty)
     */
    List<Event> getEvents(String aggregateId);

    /**
     * Loads events for an aggregate starting after the given version.
     *
     * <p>Used in combination with snapshots: load the snapshot (which
     * contains the version it was taken at), then load only the events
     * that occurred after that version.
     *
     * @param aggregateId the aggregate identifier
     * @param version     load events with version strictly greater than this
     * @return events after the given version (may be empty)
     */
    List<Event> getEventsAfterVersion(String aggregateId, int version);
}
