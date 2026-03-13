package exercises.eventsourcing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Event-sourced bank account aggregate.
 *
 * <p>State is never stored directly. Instead, all state changes are
 * represented as {@link Event}s. The current state is derived by
 * replaying the event history.
 *
 * <p><strong>Exercise 3:</strong> Implement the {@code apply} methods and
 * the {@code rehydrate} factory method.
 */
public class BankAccount {

    private String accountId;
    private String ownerName;
    private BigDecimal balance;
    private int version;
    private final List<Event> uncommittedEvents;

    private BankAccount() {
        this.balance = BigDecimal.ZERO;
        this.version = 0;
        this.uncommittedEvents = new ArrayList<>();
    }

    /**
     * Rehydrates an account from a sequence of persisted events.
     *
     * @param events the full event history for this aggregate (in order)
     * @return a fully reconstructed BankAccount
     */
    public static BankAccount rehydrate(List<Event> events) {
        // TODO: Create a blank account
        // TODO: Loop through events and call the appropriate apply method
        // TODO: Return the account with correct state
        throw new UnsupportedOperationException("TODO: implement rehydrate");
    }

    /**
     * Opens a new account by raising an {@link Event.AccountOpened} event.
     *
     * @param accountId      unique account identifier
     * @param ownerName      the account holder's name
     * @param initialBalance starting balance (may be zero)
     * @return the new account with one uncommitted event
     */
    public static BankAccount open(String accountId, String ownerName,
                                   BigDecimal initialBalance) {
        // TODO: Create blank account
        // TODO: Raise AccountOpened event
        // TODO: Apply the event to set internal state
        // TODO: Add to uncommittedEvents
        throw new UnsupportedOperationException("TODO: implement open");
    }

    /**
     * Deposits money into this account.
     *
     * @param amount      the amount to deposit (must be positive)
     * @param description optional description
     */
    public void deposit(BigDecimal amount, String description) {
        // TODO: Validate amount > 0
        // TODO: Raise MoneyDeposited event
        // TODO: Apply event and add to uncommitted list
        throw new UnsupportedOperationException("TODO: implement deposit");
    }

    /**
     * Withdraws money from this account.
     *
     * @param amount      the amount to withdraw (must be positive)
     * @param description optional description
     * @throws IllegalStateException if insufficient funds
     */
    public void withdraw(BigDecimal amount, String description) {
        // TODO: Validate amount > 0
        // TODO: Check sufficient balance
        // TODO: Raise MoneyWithdrawn event
        // TODO: Apply event and add to uncommitted list
        throw new UnsupportedOperationException("TODO: implement withdraw");
    }

    // ---- Event application methods ----

    private void apply(Event.AccountOpened event) {
        // TODO: Set accountId, ownerName, balance from event fields
    }

    private void apply(Event.MoneyDeposited event) {
        // TODO: Add event.amount to balance
    }

    private void apply(Event.MoneyWithdrawn event) {
        // TODO: Subtract event.amount from balance
    }

    private void apply(Event.TransferInitiated event) {
        // TODO: Subtract transfer amount from balance
    }

    private void apply(Event.TransferCompleted event) {
        // TODO: No state change — marker event
    }

    // ---- Queries ----

    public String getAccountId() { return accountId; }
    public String getOwnerName() { return ownerName; }
    public BigDecimal getBalance() { return balance; }
    public int getVersion() { return version; }

    /**
     * @return events that have been raised but not yet persisted
     */
    public List<Event> getUncommittedEvents() {
        return Collections.unmodifiableList(uncommittedEvents);
    }

    /**
     * Clears the uncommitted events after they have been persisted.
     */
    public void markCommitted() {
        uncommittedEvents.clear();
    }
}
