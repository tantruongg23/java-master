# Payment Processing System - Test Documentation

## Overview

This test suite provides comprehensive coverage for the Payment Processing System implementation, testing all payment methods and the processor orchestration layer.

## Test Structure

```
src/test/java/exercises/payment/
├── CreditCardPaymentTest.java      - Tests for credit card payments
├── DebitCardPaymentTest.java       - Tests for debit card payments
├── PaypalPaymentTest.java          - Tests for PayPal payments
├── BankTransferPaymentTest.java    - Tests for bank transfer payments
├── CryptoPaymentTest.java          - Tests for cryptocurrency payments
├── PaymentProcessorTest.java       - Tests for payment processor orchestration
└── PaymentTestSuite.java           - Complete test suite runner
```

## Test Coverage

### Total Test Cases: 150+

#### CreditCardPayment (29 tests)
- ✅ Valid card validation (Luhn algorithm)
- ✅ Expiry date validation
- ✅ CVV format validation (3 digits, numeric only)
- ✅ Card holder name validation
- ✅ 2% fee calculation
- ✅ $10,000 transaction limit
- ✅ Null safety checks

#### DebitCardPayment (25 tests)
- ✅ Card number validation (16 digits + Luhn)
- ✅ PIN format validation (4 digits, numeric only)
- ✅ Expiry date validation
- ✅ Flat $0.50 fee (not percentage!)
- ✅ $5,000 transaction limit
- ✅ Null safety checks

#### PaypalPayment (20 tests)
- ✅ Email format validation
- ✅ Auth token length validation (>10 chars)
- ✅ 2.9% + $0.30 fee calculation
- ✅ $25,000 transaction limit
- ✅ Small amount handling

#### BankTransferPayment (28 tests)
- ✅ Bank code validation (8 or 11 chars)
- ✅ Account number validation (8-20 digits)
- ✅ Account holder name validation
- ✅ Flat $5 fee
- ✅ $100,000 transaction limit
- ✅ **PENDING status** (not COMPLETED)

#### CryptoPayment (30 tests)
- ✅ Wallet address validation (26-62 chars)
- ✅ Prefix validation (0x, 1, 3, bc1)
- ✅ Cryptocurrency whitelist (BTC, ETH, USDT)
- ✅ 1% network fee
- ✅ No upper limit
- ✅ **PENDING status** (blockchain confirmation)

#### PaymentProcessor (18 tests)
- ✅ SOLID Principles verification
  - DIP: Works with PaymentMethod interface
  - OCP: CryptoPayment added without modifying processor
  - LSP: All subtypes substitutable
- ✅ Amount validation (null, zero, negative)
- ✅ Payment method validation
- ✅ Complete integration flows for all payment types
- ✅ Error handling and edge cases

## Running Tests

### Run All Tests
```bash
# Using Maven
mvn test

# Using Gradle
gradle test

# Using JUnit Console Launcher
java -jar junit-platform-console-standalone.jar --scan-classpath

# Using IDE
# Right-click on PaymentTestSuite.java → Run Tests
```

### Run Specific Test Class
```bash
mvn test -Dtest=CreditCardPaymentTest
mvn test -Dtest=PaymentProcessorTest
```

### Run Specific Test Method
```bash
mvn test -Dtest=CreditCardPaymentTest#shouldValidateSuccessfully
```

## Test Scenarios Covered

### 1. Business Logic Validation
- ✅ Fee calculations (percentage vs flat)
- ✅ Transaction limits (per payment method)
- ✅ Status returns (COMPLETED vs PENDING)
- ✅ Transaction ID formats

### 2. Input Validation
- ✅ Null values
- ✅ Empty strings
- ✅ Invalid formats (email, card numbers, etc.)
- ✅ Length constraints
- ✅ Regex patterns (CVV, PIN, wallet addresses)

### 3. Edge Cases
- ✅ Amounts at exact limits
- ✅ Very small amounts ($0.01)
- ✅ Very large amounts ($1,000,000+)
- ✅ Expired cards
- ✅ Invalid prefixes (crypto wallets)

### 4. SOLID Principles
- ✅ Dependency Inversion (interface-based)
- ✅ Open/Closed (extensibility without modification)
- ✅ Liskov Substitution (subtype substitutability)

### 5. Error Handling
- ✅ Graceful failure handling
- ✅ Meaningful error messages
- ✅ Null safety throughout

## Expected Test Results

All tests should **PASS** ✅

If any tests fail, check:
1. **DebitCardPayment fee calculation** - Should be flat $0.50, not 50%
2. **CryptoPayment status** - Should return PENDING, not COMPLETED
3. **BankTransferPayment status** - Should return PENDING, not COMPLETED
4. **PayPalPayment fee** - Should be 2.9% + $0.30, not 129%

## Test Data

### Valid Test Credit Cards (Luhn-valid)
- `4111111111111111` - Visa test card
- `4111111111111112` - Visa test card (alternative)

### Valid Test Email
- `user@example.com`

### Valid Test Wallet Addresses
- Ethereum: `0x1234567890123456789012345678901234567890`
- Bitcoin (legacy): `1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa`
- Bitcoin (P2SH): `3J98t1WpEZ73CNmYviecrnyiWrnqRhWNLy`
- Bitcoin (SegWit): `bc1qar0srrr7xfkvy5l643lydnw9re59gtzzwf5mdq`

## Fee Calculation Examples

| Payment Method | Amount | Fee Calculation | Fee | Total |
|---|---|---|---|---|
| Credit Card | $100 | 2% | $2.00 | $102.00 |
| Debit Card | $100 | Flat | $0.50 | $100.50 |
| PayPal | $100 | 2.9% + $0.30 | $3.20 | $103.20 |
| Bank Transfer | $100 | Flat | $5.00 | $105.00 |
| Crypto | $100 | 1% | $1.00 | $101.00 |

## Transaction Limits

| Payment Method | Minimum | Maximum | Status |
|---|---|---|---|
| Credit Card | $0.01 | $10,000 | COMPLETED |
| Debit Card | $0.01 | $5,000 | COMPLETED |
| PayPal | $0.01 | $25,000 | COMPLETED |
| Bank Transfer | $0.01 | $100,000 | PENDING |
| Crypto | $0.01 | No limit | PENDING |

## JUnit 5 Features Used

- ✅ `@DisplayName` - Clear test descriptions
- ✅ `@Nested` - Logical test grouping
- ✅ `@BeforeEach` - Test setup
- ✅ `@Test` - Test methods
- ✅ `assertAll()` - Multiple assertions
- ✅ `assertTrue/False()` - Boolean assertions
- ✅ `assertEquals()` - Value comparisons
- ✅ `assertNotNull()` - Null checks
- ✅ Test Suite - Grouped execution

## Test Naming Convention

```
should[ExpectedBehavior]When[StateUnderTest]
```

Examples:
- `shouldValidateSuccessfully()` - Happy path
- `shouldFailWhenCardNumberIsNull()` - Edge case
- `shouldCharge1PercentNetworkFee()` - Business logic

## Continuous Integration

Add to your CI/CD pipeline:

```yaml
# GitHub Actions example
- name: Run Tests
  run: mvn test
  
- name: Generate Test Report
  run: mvn surefire-report:report
  
- name: Publish Test Results
  uses: EnricoMi/publish-unit-test-result-action@v2
  if: always()
  with:
    files: target/surefire-reports/*.xml
```

## Test Maintenance

When adding new payment methods:
1. Create `[PaymentMethod]Test.java`
2. Add validation tests
3. Add processing tests
4. Add payment type tests
5. Update `PaymentTestSuite.java`
6. Update this README

## Troubleshooting

### Tests fail with NullPointerException
- Check that constructors properly initialize all fields
- Verify null checks before accessing string methods

### Fee calculation tests fail
- Verify BigDecimal arithmetic (use `multiply`, not `*`)
- Check fee percentages (0.02 = 2%, not 2)
- Ensure flat fees use `BigDecimal.valueOf()`, not `multiply()`

### Status assertion fails
- Verify PENDING status for BankTransfer and Crypto
- Check COMPLETED status for cards and PayPal

## Code Coverage Goal

Target: **95%+ coverage**
- Line coverage: 95%+
- Branch coverage: 90%+
- Method coverage: 100%

Run coverage report:
```bash
mvn jacoco:report
# Report at: target/site/jacoco/index.html
```

---

**Last Updated:** 2026-03-15  
**Total Tests:** 150+  
**Expected Pass Rate:** 100% ✅
