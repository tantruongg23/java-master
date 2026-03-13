package exercises.ddd.sales;

import java.time.LocalDateTime;

/**
 * Base interface for all domain events within the Sales bounded context.
 *
 * <p>Every domain event must carry:
 * <ul>
 *   <li>A unique event identifier.</li>
 *   <li>The timestamp when the event occurred.</li>
 * </ul>
 *
 * <p>Events are raised by aggregate roots and published after the
 * aggregate has been persisted successfully.
 */
public interface DomainEvent {

    /**
     * @return a globally unique identifier for this event
     */
    String getEventId();

    /**
     * @return the instant when this event occurred
     */
    LocalDateTime getTimestamp();
}
