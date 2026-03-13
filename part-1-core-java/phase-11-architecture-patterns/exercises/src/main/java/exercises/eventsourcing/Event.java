package exercises.eventsourcing;

import java.time.Instant;
import java.util.UUID;

/**
 * Base class for all events in the event-sourced bank ledger.
 *
 * <p>Every event carries:
 * <ul>
 *   <li>{@code eventId} — globally unique event identifier</li>
 *   <li>{@code aggregateId} — the aggregate this event belongs to</li>
 *   <li>{@code timestamp} — when the event occurred</li>
 *   <li>{@code version} — sequential version within the aggregate stream</li>
 * </ul>
 *
 * <p>Subclasses add event-specific fields.
 */
public abstract class Event {

    private final String eventId;
    private final String aggregateId;
    private final Instant timestamp;
    private final int version;

    protected Event(String aggregateId, int version) {
        this.eventId = UUID.randomUUID().toString();
        this.aggregateId = aggregateId;
        this.timestamp = Instant.now();
        this.version = version;
    }

    public String getEventId() { return eventId; }
    public String getAggregateId() { return aggregateId; }
    public Instant getTimestamp() { return timestamp; }
    public int getVersion() { return version; }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "eventId='" + eventId + '\'' +
                ", aggregateId='" + aggregateId + '\'' +
                ", version=" + version +
                ", timestamp=" + timestamp +
                '}';
    }

    // ---- Concrete event types ----

    /** Raised when a new bank account is opened. */
    public static class AccountOpened extends Event {
        private final String ownerName;
        private final java.math.BigDecimal initialBalance;

        public AccountOpened(String aggregateId, int version,
                             String ownerName, java.math.BigDecimal initialBalance) {
            super(aggregateId, version);
            this.ownerName = ownerName;
            this.initialBalance = initialBalance;
        }

        public String getOwnerName() { return ownerName; }
        public java.math.BigDecimal getInitialBalance() { return initialBalance; }
    }

    /** Raised when money is deposited into an account. */
    public static class MoneyDeposited extends Event {
        private final java.math.BigDecimal amount;
        private final String description;

        public MoneyDeposited(String aggregateId, int version,
                              java.math.BigDecimal amount, String description) {
            super(aggregateId, version);
            this.amount = amount;
            this.description = description;
        }

        public java.math.BigDecimal getAmount() { return amount; }
        public String getDescription() { return description; }
    }

    /** Raised when money is withdrawn from an account. */
    public static class MoneyWithdrawn extends Event {
        private final java.math.BigDecimal amount;
        private final String description;

        public MoneyWithdrawn(String aggregateId, int version,
                              java.math.BigDecimal amount, String description) {
            super(aggregateId, version);
            this.amount = amount;
            this.description = description;
        }

        public java.math.BigDecimal getAmount() { return amount; }
        public String getDescription() { return description; }
    }

    /** Raised when a transfer between accounts is initiated. */
    public static class TransferInitiated extends Event {
        private final String targetAccountId;
        private final java.math.BigDecimal amount;

        public TransferInitiated(String aggregateId, int version,
                                 String targetAccountId, java.math.BigDecimal amount) {
            super(aggregateId, version);
            this.targetAccountId = targetAccountId;
            this.amount = amount;
        }

        public String getTargetAccountId() { return targetAccountId; }
        public java.math.BigDecimal getAmount() { return amount; }
    }

    /** Raised when a transfer completes successfully. */
    public static class TransferCompleted extends Event {
        private final String transferId;

        public TransferCompleted(String aggregateId, int version, String transferId) {
            super(aggregateId, version);
            this.transferId = transferId;
        }

        public String getTransferId() { return transferId; }
    }
}
