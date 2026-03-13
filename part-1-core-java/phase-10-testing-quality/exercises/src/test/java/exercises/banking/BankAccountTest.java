package exercises.banking;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD test suite for {@link BankAccount}.
 *
 * <p>Exercise 1 — write every test BEFORE implementing the production code.
 * Follow the Red-Green-Refactor cycle strictly.
 */
@DisplayName("BankAccount")
class BankAccountTest {

    private BankAccount account;

    @BeforeEach
    void setUp() {
        account = new BankAccount("ACC-001");
    }

    // ------------------------------------------------------------------
    // Deposit Tests
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("deposit()")
    class DepositTests {

        @Test
        @DisplayName("should increase balance by the deposited amount")
        void shouldIncreaseBalance() {
            // TODO: deposit 100, assert balance == 100
        }

        @Test
        @DisplayName("should record a DEPOSIT transaction")
        void shouldRecordTransaction() {
            // TODO: deposit, then check transactionHistory contains one DEPOSIT entry
        }

        @ParameterizedTest(name = "depositing {0} should succeed")
        @CsvSource({"0.01", "1.00", "999999.99"})
        @DisplayName("should accept various valid amounts")
        void shouldAcceptValidAmounts(String amountStr) {
            // TODO: deposit each amount, assert no exception and correct balance
        }

        @ParameterizedTest(name = "depositing {0} should throw")
        @CsvSource({"0", "-1", "-0.01"})
        @DisplayName("should reject zero or negative amounts")
        void shouldRejectInvalidAmounts(String amountStr) {
            // TODO: assertThrows(IllegalArgumentException.class, ...)
        }

        @Test
        @DisplayName("should throw when amount is null")
        void shouldRejectNullAmount() {
            // TODO: assertThrows(IllegalArgumentException.class, ...)
        }
    }

    // ------------------------------------------------------------------
    // Withdraw Tests
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("withdraw()")
    class WithdrawTests {

        @BeforeEach
        void fundAccount() {
            // Pre-fund with a known balance for withdrawal tests
            // TODO: deposit some initial amount once deposit() is implemented
        }

        @Test
        @DisplayName("should decrease balance by the withdrawn amount")
        void shouldDecreaseBalance() {
            // TODO: withdraw from funded account, assert new balance
        }

        @Test
        @DisplayName("should record a WITHDRAWAL transaction")
        void shouldRecordTransaction() {
            // TODO: withdraw, then check transactionHistory
        }

        @Test
        @DisplayName("should throw when withdrawing more than balance")
        void shouldRejectOverdraft() {
            // TODO: assertThrows(IllegalStateException.class, ...)
        }

        @ParameterizedTest(name = "withdrawing {0} from balance 100 should throw")
        @CsvSource({"0", "-5", "-0.01"})
        @DisplayName("should reject zero or negative amounts")
        void shouldRejectInvalidAmounts(String amountStr) {
            // TODO: assertThrows(IllegalArgumentException.class, ...)
        }

        @Test
        @DisplayName("should allow withdrawing the exact balance (zero remaining)")
        void shouldAllowFullWithdrawal() {
            // TODO: withdraw exact balance, assert balance == 0
        }
    }

    // ------------------------------------------------------------------
    // Transfer Tests
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("transfer()")
    class TransferTests {

        private BankAccount target;

        @BeforeEach
        void setUp() {
            target = new BankAccount("ACC-002");
            // TODO: fund source account once deposit() works
        }

        @Test
        @DisplayName("should move funds from source to target")
        void shouldTransferFunds() {
            // TODO: transfer amount, assert both balances changed correctly
        }

        @Test
        @DisplayName("should record TRANSFER_OUT on source and TRANSFER_IN on target")
        void shouldRecordTransactions() {
            // TODO: transfer, check transaction types on both accounts
        }

        @Test
        @DisplayName("should throw when source has insufficient funds")
        void shouldRejectInsufficientFunds() {
            // TODO: assertThrows(IllegalStateException.class, ...)
        }

        @Test
        @DisplayName("should throw when target is null")
        void shouldRejectNullTarget() {
            // TODO: assertThrows(IllegalArgumentException.class, ...)
        }

        @Test
        @DisplayName("should throw when transferring to self")
        void shouldRejectSelfTransfer() {
            // TODO: assertThrows(IllegalArgumentException.class, ...)
        }

        @ParameterizedTest(name = "transferring {0} should throw")
        @ValueSource(strings = {"0", "-1"})
        @DisplayName("should reject zero or negative transfer amounts")
        void shouldRejectInvalidAmounts(String amountStr) {
            // TODO: assertThrows(IllegalArgumentException.class, ...)
        }
    }

    // ------------------------------------------------------------------
    // Transaction History Tests
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("getTransactionHistory()")
    class TransactionHistoryTests {

        @Test
        @DisplayName("should return empty list for a new account")
        void shouldBeEmptyForNewAccount() {
            assertTrue(account.getTransactionHistory().isEmpty());
        }

        @Test
        @DisplayName("should return an unmodifiable list")
        void shouldBeUnmodifiable() {
            // TODO: try to add to the returned list, expect UnsupportedOperationException
        }

        @Test
        @DisplayName("should preserve chronological order")
        void shouldPreserveOrder() {
            // TODO: perform multiple operations, verify order
        }
    }
}
