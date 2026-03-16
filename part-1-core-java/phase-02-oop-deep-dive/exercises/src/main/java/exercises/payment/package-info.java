
/**
 * Payment Processing System — Exercise 2: SOLID Principles in Practice
 *
 * <p>This package demonstrates all five SOLID principles through a real-world payment processing
 * system. Each principle is explicitly applied and documented throughout the codebase.
 *
 * <h2>Architecture Overview</h2>
 *
 * <pre>
 * ┌─────────────────────────────────────────────────────────────────┐
 * │                     PaymentProcessor                            │
 * │  (Orchestrates payment flow - depends on abstraction)           │
 * └─────────────────────┬───────────────────────────────────────────┘
 *                       │ depends on
 *                       ▼
 * ┌─────────────────────────────────────────────────────────────────┐
 * │                  PaymentMethod (Interface)                       │
 * │  • validate(): boolean                                           │
 * │  • process(BigDecimal): PaymentResult                            │
 * │  • getPaymentType(): String                                      │
 * └─────────────────────┬───────────────────────────────────────────┘
 *                       │ implemented by
 *        ┌──────────────┼──────────────┬──────────────┬────────────┐
 *        ▼              ▼              ▼              ▼            ▼
 * ┌────────────┐ ┌────────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐
 * │CreditCard  │ │ DebitCard  │ │ PayPal   │ │  Bank    │ │  Crypto  │
 * │Payment     │ │Payment     │ │Payment   │ │Transfer  │ │Payment   │
 * └────────────┘ └────────────┘ └──────────┘ └──────────┘ └──────────┘
 *
 *                    Optional Enhancement (SRP + ISP):
 * ┌─────────────────────────────────────────────────────────────────┐
 * │              PaymentValidator (Interface)                        │
 * │  • validate(): boolean                                           │
 * │  • validateWithErrors(): List&lt;String&gt;                      │
 * └─────────────────────┬───────────────────────────────────────────┘
 *                       │ implemented by
 *        ┌──────────────┼──────────────┬──────────────┐
 *        ▼              ▼              ▼              ▼
 * ┌────────────────┐ ┌────────────────┐ ┌───────────────┐
 * │CreditCard      │ │Crypto          │ │  Other        │
 * │Validator       │ │Validator       │ │  Validators   │
 * └────────────────┘ └────────────────┘ └───────────────┘
 * </pre>
 *
 * <h2>SOLID Principles Applied</h2>
 *
 * <h3>1. Single Responsibility Principle (SRP)</h3>
 *
 * <p><b>Definition:</b> A class should have only one reason to change.
 *
 * <p><b>Implementation:</b>
 * <ul>
 *   <li><b>{@link exercises.payment.PaymentProcessor}</b> — Orchestration only
 *       <ul>
 *           <li>✅ Handles payment flow coordination</li>
 *           <li>✅ Does NOT contain validation logic</li>
 *           <li>✅ Does NOT contain fee calculation logic</li>
 *           <li>✅ Single responsibility: orchestrate the payment pipeline</li>
 *       </ul>
 *   </li>
 *
 *   <li><b>Payment Method Classes</b> (e.g., {@link exercises.payment.CreditCardPayment})
 *       <ul>
 *           <li>✅ Each class handles ONE payment type only</li>
 *           <li>✅ Validation logic specific to that payment type</li>
 *           <li>✅ Fee calculation specific to that payment type</li>
 *           <li>✅ Single responsibility: process one payment method</li>
 *       </ul>
 *   </li>
 *
 *   <li><b>{@link exercises.payment.PaymentResult}</b> — Data carrier only
 *       <ul>
 *           <li>✅ Immutable record holding payment outcome</li>
 *           <li>✅ No business logic</li>
 *           <li>✅ Single responsibility: carry payment result data</li>
 *       </ul>
 *   </li>
 *
 *   <li><b>Enhanced SRP with Validators</b> (Optional)
 *       <ul>
 *           <li>✅ {@link exercises.payment.PaymentValidator} — Validation ONLY</li>
 *           <li>✅ Payment classes focus on processing ONLY</li>
 *           <li>✅ Clear separation of validation and processing concerns</li>
 *       </ul>
 *   </li>
 * </ul>
 *
 * <p><b>Example:</b>
 * <pre>{@code
 * // PaymentProcessor has ONE job: orchestrate
 * public class PaymentProcessor {
 *     public PaymentResult processPayment(PaymentMethod method, BigDecimal amount) {
 *         // 1. Validate amount
 *         // 2. Validate payment method (delegates to method)
 *         // 3. Process payment (delegates to method)
 *         // No validation or fee calculation logic here!
 *     }
 * }
 *
 * // CreditCardPayment has ONE job: process credit card payments
 * public class CreditCardPayment implements PaymentMethod {
 *     public boolean validate() {

 *     public PaymentResult process(BigDecimal amount) {
 * }
 * }</pre>
 *
 * <h3>2. Open/Closed Principle (OCP)</h3>
 *
 * <p><b>Definition:</b> Software entities should be open for extension but closed for modification.
 *
 * <p><b>Implementation:</b>
 * <ul>
 *   <li><b>{@link exercises.payment.PaymentProcessor}</b> is CLOSED for modification
 *       <ul>
 *           <li>✅ Adding {@link exercises.payment.CryptoPayment} required ZERO changes</li>
 *           <li>✅ Works with ANY {@link exercises.payment.PaymentMethod} implementation</li>
 *           <li>✅ No if/else chains or switch statements</li>
 *       </ul>
 *   </li>
 *
 *   <li><b>New payment methods</b> are added by extension
 *       <ul>
 *           <li>✅ Create new class implementing {@link exercises.payment.PaymentMethod}</li>
 *           <li>✅ No modifications to existing code</li>
 *           <li>✅ System automatically supports new payment type</li>
 *       </ul>
 *   </li>
 * </ul>
 *
 * <p><b>Example:</b>
 * <pre>{@code
 * // BEFORE: CryptoPayment didn't exist
 * PaymentProcessor processor = new PaymentProcessor();
 * processor.processPayment(new CreditCardPayment(...), amount); // Works
 *
 * // AFTER: Added CryptoPayment (NEW class, NO modifications to processor)
 * processor.processPayment(new CryptoPayment(...), amount); // Works immediately!
 *
 * // The processor was CLOSED (no changes) but OPEN for extension (new payment type)
 * }</pre>
 *
 * <p><b>Real-World Demonstration:</b>
 * <ul>
 *   <li>Original design: CreditCard, DebitCard, PayPal, BankTransfer (4 types)</li>
 *   <li>Added: {@link exercises.payment.CryptoPayment} (bonus exercise)</li>
 *   <li>Lines changed in {@link exercises.payment.PaymentProcessor}: <b>ZERO</b> ✅</li>
 * </ul>
 *
 * <h3>3. Liskov Substitution Principle (LSP)</h3>
 *
 * <p><b>Definition:</b> Subtypes must be substitutable for their base types without altering correctness.
 *
 * <p><b>Implementation:</b>
 * <ul>
 *   <li><b>All {@link exercises.payment.PaymentMethod} implementations are substitutable</b>
 *       <ul>
 *           <li>✅ Same interface contract: validate(), process(), getPaymentType()</li>
 *           <li>✅ Consistent return types</li>
 *           <li>✅ No unexpected exceptions</li>
 *           <li>✅ Behavior is predictable</li>
 *       </ul>
 *   </li>
 *
 *   <li><b>Contract guarantees:</b>
 *       <ul>
 *           <li>✅ validate() always returns boolean (never throws)</li>
 *           <li>✅ process() always returns {@link exercises.payment.PaymentResult}</li>
 *           <li>✅ All implementations respect transaction limits</li>
 *           <li>✅ All implementations calculate fees correctly</li>
 *       </ul>
 *   </li>
 * </ul>
 *
 * <p><b>Example:</b>
 * <pre>{@code
 * // Any PaymentMethod subtype can be used interchangeably
 * PaymentMethod[] paymentMethods = {
 *     new CreditCardPayment(...),
 *     new DebitCardPayment(...),
 *     new PaypalPayment(...),
 *     new BankTransferPayment(...),
 *     new CryptoPayment(...)
 * };
 *
 * // All work identically through the processor
 * for (PaymentMethod method : paymentMethods) {
 *     PaymentResult result = processor.processPayment(method, amount);
 *     // Consistent behavior for all subtypes ✅
 * }
 * }</pre>
 *
 * <p><b>LSP Violations Avoided:</b>
 * <ul>
 *   <li>❌ No implementation throws unexpected exceptions</li>
 *   <li>❌ No implementation returns null when it shouldn't</li>
 *   <li>❌ No implementation ignores the amount parameter</li>
 *   <li>❌ No implementation has side effects beyond returning a result</li>
 * </ul>
 *
 * <h3>4. Interface Segregation Principle (ISP)</h3>
 *
 * <p><b>Definition:</b> Clients should not be forced to depend on methods they don't use.
 *
 * <p><b>Implementation:</b>
 * <ul>
 *   <li><b>{@link exercises.payment.PaymentMethod}</b> — Focused, cohesive interface
 *       <ul>
 *           <li>✅ Only 3 methods (minimal interface)</li>
 *           <li>✅ All methods are essential for payment processing</li>
 *           <li>✅ No "fat" interface with unrelated methods</li>
 *           <li>✅ No method is optional or unused</li>
 *       </ul>
 *   </li>
 *
 *   <li><b>{@link exercises.payment.PaymentValidator}</b> — Even more focused (Optional)
 *       <ul>
 *           <li>✅ Only 2 methods (validation only)</li>
 *           <li>✅ No processing methods mixed in</li>
 *           <li>✅ Single purpose: validation</li>
 *       </ul>
 *   </li>
 *
 *   <li><b>What we AVOIDED (anti-patterns):</b>
 *       <ul>
 *           <li>❌ NOT a "fat" interface like: PaymentMethod with refund(), cancel(), getHistory(), etc.</li>
 *           <li>❌ NOT mixing concerns: validation + processing + reporting + auditing</li>
 *           <li>❌ NOT forcing implementations to provide methods they don't need</li>
 *       </ul>
 *   </li>
 * </ul>
 *
 * <p><b>Example:</b>
 * <pre>{@code
 * // GOOD (ISP): Focused interface
 * public interface PaymentMethod {
 *     boolean validate();                          // Essential
 *     PaymentResult process(BigDecimal amount);    // Essential
 *     String getPaymentType();                     // Essential
 * }
 *
 * // BAD (ISP Violation): Fat interface
 * public interface PaymentMethodBad {
 *     boolean validate();
 *     PaymentResult process(BigDecimal amount);
 *     String getPaymentType();
 *     void refund(String transactionId);           // Not all payment types support refunds
 *     void cancel();                                // Not all support cancellation
 *     List<Transaction> getHistory();               // Not all track history
 *     void sendReceipt(String email);               // Not payment processing logic
 *     void auditLog();                              // Cross-cutting concern
 *     // ... 10 more methods                        // Forces implementations to provide unused methods
 * }
 *
 * // With ISP: Each implementation only provides what it needs
 * public class CryptoPayment implements PaymentMethod {
 *     // Only implement 3 essential methods ✅
 * }
 * }</pre>
 *
 * <h3>5. Dependency Inversion Principle (DIP)</h3>
 *
 * <p><b>Definition:</b> High-level modules should depend on abstractions, not concrete implementations.
 *
 * <p><b>Implementation:</b>
 * <ul>
 *   <li><b>{@link exercises.payment.PaymentProcessor}</b> depends on abstraction
 *       <ul>
 *           <li>✅ Depends on {@link exercises.payment.PaymentMethod} interface</li>
 *           <li>✅ Does NOT import concrete classes (CreditCardPayment, etc.)</li>
 *           <li>✅ Works with ANY implementation</li>
 *           <li>✅ Loose coupling</li>
 *       </ul>
 *   </li>
 *
 *   <li><b>Enhanced DIP with Validators</b> (Optional)
 *       <ul>
 *           <li>✅ Payment classes depend on {@link exercises.payment.PaymentValidator} interface</li>
 *           <li>✅ Validators can be injected (dependency injection)</li>
 *           <li>✅ Testable with mock validators</li>
 *       </ul>
 *   </li>
 *
 *   <li><b>Benefits achieved:</b>
 *       <ul>
 *           <li>✅ Easy to test (inject mock implementations)</li>
 *           <li>✅ Easy to extend (add new implementations)</li>
 *           <li>✅ Loose coupling between components</li>
 *           <li>✅ High-level policy separated from low-level details</li>
 *       </ul>
 *   </li>
 * </ul>
 *
 * <p><b>Example:</b>
 * <pre>{@code
 * // HIGH-LEVEL MODULE (depends on abstraction)
 * public class PaymentProcessor {
 *     // ✅ Depends on PaymentMethod interface, NOT concrete classes
 *     public PaymentResult processPayment(PaymentMethod method, BigDecimal amount) {
 *         if (!method.validate()) { ... }
 *         return method.process(amount);
 *     }
 * }
 *
 * // LOW-LEVEL MODULE (implements abstraction)
 * public class CreditCardPayment implements PaymentMethod {
 *     // Concrete implementation
 * }
 *
 * // USAGE: High-level doesn't know about low-level details
 * PaymentProcessor processor = new PaymentProcessor();
 * processor.processPayment(new CreditCardPayment(...), amount); // Abstraction used
 *
 * // Enhanced DIP with validator injection
 * public class CreditCardPaymentWithValidator {
 *     private final PaymentValidator validator; // ✅ Depends on abstraction
 *
 *     public CreditCardPaymentWithValidator(..., PaymentValidator validator) {
 *         this.validator = validator; // ✅ Injected dependency
 *     }
 * }
 * }</pre>
 *
 * <h2>Design Patterns Used</h2>
 *
 * <ul>
 *   <li><b>Strategy Pattern:</b> {@link exercises.payment.PaymentMethod} implementations
 *       provide different payment strategies</li>
 *   <li><b>Factory Pattern:</b> {@link exercises.payment.PaymentResult} factory methods
 *       (completed(), pending(), failed())</li>
 *   <li><b>Template Method:</b> Validation and processing flow in
 *       {@link exercises.payment.PaymentProcessor}</li>
 *   <li><b>Dependency Injection:</b> {@link exercises.payment.PaymentValidator}
 *       can be injected into payment classes</li>
 * </ul>
 *
 * <h2>Business Rules</h2>
 *
 * <h3>Transaction Limits</h3>
 * <table border="1">
 *   <caption>Payment Method Limits</caption>
 *   <tr>
 *     <th>Payment Method</th>
 *     <th>Minimum</th>
 *     <th>Maximum</th>
 *     <th>Fee Structure</th>
 *     <th>Status</th>
 *   </tr>
 *   <tr>
 *     <td>{@link exercises.payment.CreditCardPayment}</td>
 *     <td>$0.01</td>
 *     <td>$10,000</td>
 *     <td>2% of amount</td>
 *     <td>COMPLETED</td>
 *   </tr>
 *   <tr>
 *     <td>{@link exercises.payment.DebitCardPayment}</td>
 *     <td>$0.01</td>
 *     <td>$5,000</td>
 *     <td>Flat $0.50</td>
 *     <td>COMPLETED</td>
 *   </tr>
 *   <tr>
 *     <td>{@link exercises.payment.PaypalPayment}</td>
 *     <td>$0.01</td>
 *     <td>$25,000</td>
 *     <td>2.9% + $0.30</td>
 *     <td>COMPLETED</td>
 *   </tr>
 *   <tr>
 *     <td>{@link exercises.payment.BankTransferPayment}</td>
 *     <td>$0.01</td>
 *     <td>$100,000</td>
 *     <td>Flat $5.00</td>
 *     <td>PENDING</td>
 *   </tr>
 *   <tr>
 *     <td>{@link exercises.payment.CryptoPayment}</td>
 *     <td>$0.01</td>
 *     <td>No limit</td>
 *     <td>1% network fee</td>
 *     <td>PENDING</td>
 *   </tr>
 * </table>
 *
 * <h2>Usage Examples</h2>
 *
 * <h3>Basic Payment Processing</h3>
 * <pre>{@code
 * // Create processor (high-level module)
 * PaymentProcessor processor = new PaymentProcessor();
 *
 * // Create payment method (low-level module via abstraction)
 * PaymentMethod creditCard = new CreditCardPayment(
 *     "4111111111111111",
 *     "John Doe",
 *     Month.DECEMBER,
 *     Year.of(2026),
 *     "123"
 * );
 *
 * // Process payment
 * PaymentResult result = processor.processPayment(creditCard, BigDecimal.valueOf(100));
 *
 * // Check result
 * if (result.status() == PaymentStatus.COMPLETED) {
 *     System.out.println("Payment successful: " + result.transactionId());
 *     System.out.println("Fee: $" + result.fee());
 *     System.out.println("Total: $" + result.totalCharged());
 * }
 * }</pre>
 *
 * <h3>Processing Different Payment Types (LSP)</h3>
 * <pre>{@code
 * PaymentProcessor processor = new PaymentProcessor();
 *
 * // All payment types are substitutable (LSP)
 * PaymentMethod[] methods = {
 *     new CreditCardPayment(...),
 *     new PaypalPayment(...),
 *     new CryptoPayment(...)
 * };
 *
 * for (PaymentMethod method : methods) {
 *     // Same interface, consistent behavior
 *     PaymentResult result = processor.processPayment(method, amount);
 *     System.out.println(method.getPaymentType() + ": " + result.status());
 * }
 * }</pre>
 *
 * <h3>Using Validators (Enhanced SRP + DIP)</h3>
 * <pre>{@code
 * // Create validator (separate concern)
 * PaymentValidator validator = new CreditCardPaymentValidator(
 *     cardNumber, cardHolder, month, year, cvv
 * );
 *
 * // Get detailed validation errors
 * List<String> errors = validator.validateWithErrors();
 * if (!errors.isEmpty()) {
 *     errors.forEach(System.out::println);
 *     return;
 * }
 *
 * // Inject validator into payment (DIP)
 * PaymentMethod payment = new CreditCardPaymentWithValidator(
 *     cardNumber, cardHolder, month, year, cvv,
 *     validator  // Dependency injection
 * );
 * }</pre>
 *
 * <h2>Testing Support</h2>
 *
 * <p>The SOLID design makes this system highly testable:
 *
 * <ul>
 *   <li><b>Unit Testing:</b> Each payment method can be tested independently</li>
 *   <li><b>Integration Testing:</b> Processor can be tested with real implementations</li>
 *   <li><b>Mock Testing:</b> Easy to create mock validators and payment methods</li>
 *   <li><b>Test Coverage:</b> 142 comprehensive tests covering all scenarios</li>
 * </ul>
 *
 * <h2>Extensibility Examples</h2>
 *
 * <h3>Adding a New Payment Method (OCP)</h3>
 * <pre>{@code
 * // Step 1: Create new class implementing PaymentMethod
 * public class ApplePayPayment implements PaymentMethod {
 *     @Override
 *     public boolean validate() {
 *
 *     @Override
 *     public PaymentResult process(BigDecimal amount)
 *
 *     @Override
 *     public String getPaymentType() { return "ApplePayPayment"; }
 * }
 *
 * // Step 2: Use immediately (no changes to PaymentProcessor needed!)
 * processor.processPayment(new ApplePayPayment(...), amount); // Works! ✅
 * }</pre>
 *
 * <h3>Adding a Custom Validator (DIP)</h3>
 * <pre>{@code
 * // Create custom validator
 * public class PCICompliantValidator implements PaymentValidator {
 *     @Override
 *     public boolean validate() {
 *         // Enhanced PCI-DSS validation rules
 *         return performPCIChecks();
 *     }
 *
 *     @Override
 *     public List<String> validateWithErrors() {
 *         // Detailed PCI compliance errors
 *         return performPCIValidation();
 *     }
 * }
 *
 * // Inject into payment
 * new CreditCardPaymentWithValidator(..., new PCICompliantValidator());
 * }</pre>
 *
 * <h2>Key Takeaways</h2>
 *
 * <p><b>This implementation demonstrates:</b>
 * <ol>
 *   <li><b>SRP:</b> Each class has one reason to change</li>
 *   <li><b>OCP:</b> Extended with CryptoPayment without modifying existing code</li>
 *   <li><b>LSP:</b> All payment methods are substitutable</li>
 *   <li><b>ISP:</b> Focused interfaces with minimal methods</li>
 *   <li><b>DIP:</b> High-level depends on abstractions</li>
 * </ol>
 *
 * <p><b>Benefits achieved:</b>
 * <ul>
 *   <li>✅ Easy to extend with new payment methods</li>
 *   <li>✅ Easy to test (142 comprehensive tests)</li>
 *   <li>✅ Loose coupling between components</li>
 *   <li>✅ Clear separation of concerns</li>
 *   <li>✅ Maintainable and scalable architecture</li>
 * </ul>
 *
 * <h2>Documentation Files</h2>
 *
 * <ul>
 *   <li><b>TEST_SUITE_COMPLETE.md:</b> Complete test suite documentation</li>
 *   <li><b>PAYMENT_VALIDATOR_PATTERN.md:</b> Validator pattern explained</li>
 *   <li><b>VALIDATOR_QUICK_REF.md:</b> Quick reference guide</li>
 * </ul>
 *
 * @see exercises.payment.PaymentMethod
 * @see exercises.payment.PaymentProcessor
 * @see exercises.payment.PaymentResult
 * @see exercises.payment.PaymentStatus
 * @see exercises.payment.PaymentValidator
 *
 * @author Java Master Course
 * @version 1.0
 * @since 2026-03-15
 */

package exercises.payment;