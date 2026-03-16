package exercises.payment;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Test Suite for Payment Processing System
 * 
 * Runs all payment method tests and processor tests in a single suite.
 * Execute this to run the complete test suite.
 */
@Suite
@SuiteDisplayName("Payment Processing System - Complete Test Suite")
@SelectClasses({
        CreditCardPaymentTest.class,
        DebitCardPaymentTest.class,
        PaypalPaymentTest.class,
        BankTransferPaymentTest.class,
        CryptoPaymentTest.class,
        PaymentProcessorTest.class
})
public class PaymentTestSuite {
    // This class remains empty, it is used only as a holder for the above annotations
}
