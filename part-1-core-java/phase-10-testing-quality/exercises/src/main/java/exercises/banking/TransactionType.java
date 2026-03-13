package exercises.banking;

/**
 * The type of a bank account transaction.
 */
public enum TransactionType {

    /** Funds added to the account. */
    DEPOSIT,

    /** Funds removed from the account. */
    WITHDRAWAL,

    /** Funds received from another account. */
    TRANSFER_IN,

    /** Funds sent to another account. */
    TRANSFER_OUT
}
