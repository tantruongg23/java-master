# Payment Processing System - Quick Start Guide

## 🚀 Running the Tests

### Prerequisites
- Java 21+
- Maven 3.8+

### Step 1: Install Dependencies
```bash
cd exercises
mvn clean install
```

### Step 2: Run All Tests
```bash
mvn test
```

### Step 3: View Results
```bash
# Console output shows:
# Tests run: 150+, Failures: 0, Errors: 0, Skipped: 0
```

## 📊 Expected Output

```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running exercises.payment.CreditCardPaymentTest
[INFO] Tests run: 29, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running exercises.payment.DebitCardPaymentTest
[INFO] Tests run: 25, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running exercises.payment.PaypalPaymentTest
[INFO] Tests run: 20, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running exercises.payment.BankTransferPaymentTest
[INFO] Tests run: 28, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running exercises.payment.CryptoPaymentTest
[INFO] Tests run: 30, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running exercises.payment.PaymentProcessorTest
[INFO] Tests run: 18, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 150, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] BUILD SUCCESS
```

## 🧪 Running Specific Tests

### Run only CreditCard tests
```bash
mvn test -Dtest=CreditCardPaymentTest
```

### Run only validation tests
```bash
mvn test -Dtest="*Test#*Validation*"
```

### Run only processing tests
```bash
mvn test -Dtest="*Test#*Processing*"
```

### Run only SOLID principles tests
```bash
mvn test -Dtest=PaymentProcessorTest#SolidPrinciplesTests
```

## 🔍 Test Categories

### 1. Validation Tests (60+ tests)
Tests input validation for all payment methods:
- Null checks
- Format validation
- Length constraints
- Regex patterns

### 2. Processing Tests (50+ tests)
Tests payment processing logic:
- Fee calculations
- Transaction limits
- Status returns
- Amount handling

### 3. SOLID Principles Tests (10+ tests)
Tests architectural principles:
- Dependency Inversion
- Open/Closed
- Liskov Substitution

### 4. Integration Tests (20+ tests)
Tests complete payment flows:
- End-to-end scenarios
- Multiple payment methods
- Error handling
- Edge cases

### 5. Edge Case Tests (10+ tests)
Tests boundary conditions:
- Amounts at exact limits
- Very small/large amounts
- Invalid inputs
- Null safety

## 📝 Test Report

Generate HTML test report:
```bash
mvn surefire-report:report
```

View report at:
```
target/site/surefire-report.html
```

## 🎯 Quick Verification

Run this to verify all tests pass:
```bash
mvn clean test -q
```

Expected output:
```
Tests run: 150, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## 🐛 Troubleshooting

### Issue: Tests not found
**Solution:** Ensure test classes end with `Test.java` and are in `src/test/java/`

### Issue: Dependencies not found
**Solution:** Run `mvn clean install` to download dependencies

### Issue: Compilation errors
**Solution:** Check Java version with `java -version` (requires Java 21+)

### Issue: Specific test failing
**Solution:** Run single test with `-Dtest=TestClassName#methodName` for details

## 💡 Tips

1. **Run tests before committing:**
   ```bash
   mvn clean test
   ```

2. **Watch mode (requires Maven plugin):**
   ```bash
   mvn test -Dsurefire.rerunFailingTestsCount=2
   ```

3. **Generate coverage report:**
   ```bash
   mvn jacoco:prepare-agent test jacoco:report
   ```

4. **Parallel test execution:**
   ```bash
   mvn test -DforkCount=4
   ```

## ✅ Success Criteria

All 150+ tests should pass:
- ✅ Validation tests
- ✅ Processing tests  
- ✅ SOLID principles tests
- ✅ Integration tests
- ✅ Edge case tests

## 📚 Next Steps

1. ✅ Run all tests
2. ✅ Fix any failures
3. ✅ Generate coverage report
4. ✅ Review test documentation
5. ✅ Add more edge case tests (optional)

---

**Need help?** Check the detailed README in `src/test/java/exercises/payment/README.md`
