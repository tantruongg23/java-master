package exercises.banking;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A bank account that supports deposits, withdrawals, and transfers.
 *
 * <p>Business rules:
 * <ul>
 *   <li>Balance must never go negative.</li>
 *   <li>Deposit and withdrawal amounts must be positive.</li>
 *   <li>Every operation is recorded as a {@link Transaction}.</li>
 * </ul>
 *
 * <p><strong>Exercise 1:</strong> Implement each method using TDD.
 * Write the test first, watch it fail, then make it pass.
 */
public class BankAccount {

    private final String accountId;
    private BigDecimal balance;
    private final List<Transaction> transactionHistory;

    public BankAccount(String accountId) {
        this.accountId = accountId;
        this.balance = BigDecimal.ZERO;
        this.transactionHistory = new ArrayList<>();
    }

    public BankAccount(String accountId, BigDecimal initialBalance) {
        if (initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }
        this.accountId = accountId;
        this.balance = initialBalance;
        this.transactionHistory = new ArrayList<>();
    }

    /**
     * Deposits the given amount into this account.
     *
     * @param amount a positive value to deposit
     * @throws IllegalArgumentException if amount is null, zero, or negative
     */
    public void deposit(BigDecimal amount) {
        // TODO: Validate amount > 0
        // TODO: Add amount to balance
        // TODO: Record a DEPOSIT transaction
        throw new UnsupportedOperationException("TODO: implement deposit");
    }

    /**
     * Withdraws the given amount from this account.
     *
     * @param amount a positive value to withdraw
     * @throws IllegalArgumentException if amount is null, zero, or negative
     * @throws IllegalStateException    if balance would go negative
     */
    public void withdraw(BigDecimal amount) {
        // TODO: Validate amount > 0
        // TODO: Check sufficient balance
        // TODO: Subtract amount from balance
        // TODO: Record a WITHDRAWAL transaction
        throw new UnsupportedOperationException("TODO: implement withdraw");
    }

    /**
     * Transfers the given amount from this account to the target account.
     *
     * @param target the receiving account
     * @param amount a positive value to transfer
     * @throws IllegalArgumentException if target is null, amount is invalid
     * @throws IllegalStateException    if this account has insufficient funds
     */
    public void transfer(BankAccount target, BigDecimal amount) {
        // TODO: Validate target != null and target != this
        // TODO: Validate amount > 0
        // TODO: Check sufficient balance
        // TODO: Subtract from this, add to target
        // TODO: Record TRANSFER_OUT on this, TRANSFER_IN on target
        throw new UnsupportedOperationException("TODO: implement transfer");
    }

    /**
     * @return the current balance (never negative)
     */
    public BigDecimal getBalance() {
        return balance;
    }

    /**
     * @return an unmodifiable view of the transaction history
     */
    public List<Transaction> getTransactionHistory() {
        return Collections.unmodifiableList(transactionHistory);
    }

    public String getAccountId() {
        return accountId;
    }
}
