package exercises;

import java.util.*;
import java.util.function.Predicate;

/**
 * A composable, functional validation framework.
 *
 * <p>{@code Validator<T>} wraps a {@link Predicate} with a human-readable
 * error message. Validators are combined with {@link #and} and {@link #or}
 * to build complex rules from simple parts.</p>
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * Validator<String> username = Validator
 *     .of(s -> s != null,                      "must not be null")
 *     .and(s -> s.length() >= 3,               "must be at least 3 chars")
 *     .and(s -> s.length() <= 50,              "must be at most 50 chars")
 *     .and(s -> s.matches("[a-zA-Z0-9_]+"),    "only alphanumeric and underscore");
 *
 * ValidationResult result = username.validate("ab");
 * // result.isValid() == false
 * // result.errors()  == ["must be at least 3 chars"]
 * }</pre>
 *
 * @param <T> the type being validated
 */
public class Validator<T> {

    /**
     * The result of a validation — either valid or a list of error messages.
     */
    public record ValidationResult(List<String> errors) {

        public static final ValidationResult VALID = new ValidationResult(List.of());

        public boolean isValid() {
            return errors.isEmpty();
        }

        /**
         * Merge two results, combining their error lists.
         */
        public ValidationResult merge(ValidationResult other) {
            if (this.isValid()) return other;
            if (other.isValid()) return this;
            var combined = new ArrayList<>(this.errors);
            combined.addAll(other.errors);
            return new ValidationResult(List.copyOf(combined));
        }
    }

    private final List<Rule<T>> rules = new ArrayList<>();

    private record Rule<T>(Predicate<T> predicate, String errorMessage) {}

    private Validator() {}

    /**
     * Create a new validator with a single rule.
     *
     * @param predicate    condition that must hold
     * @param errorMessage message if the predicate fails
     * @param <T>          type being validated
     * @return a new Validator with one rule
     */
    public static <T> Validator<T> of(Predicate<T> predicate, String errorMessage) {
        // TODO: create a Validator, add the rule, return it
        throw new UnsupportedOperationException("TODO: implement of");
    }

    /**
     * Add an additional rule (logical AND — all rules must pass).
     *
     * @param predicate    condition that must hold
     * @param errorMessage message if the predicate fails
     * @return this validator (for chaining)
     */
    public Validator<T> and(Predicate<T> predicate, String errorMessage) {
        // TODO: add the rule to this.rules, return this
        throw new UnsupportedOperationException("TODO: implement and");
    }

    /**
     * Combine with another validator using logical OR:
     * the value is valid if <em>either</em> validator passes.
     *
     * @param other the alternative validator
     * @return a new combined validator
     */
    public Validator<T> or(Validator<T> other) {
        // TODO: create a new Validator that passes if either
        //       this.validate(value) or other.validate(value) is valid.
        throw new UnsupportedOperationException("TODO: implement or");
    }

    /**
     * Run all rules against the value and collect errors.
     *
     * @param value the object to validate
     * @return a {@link ValidationResult} — valid or with a list of error messages
     */
    public ValidationResult validate(T value) {
        // TODO: test each rule's predicate against value,
        //       collect error messages for failed predicates,
        //       return VALID if all pass or a new ValidationResult with errors.
        throw new UnsupportedOperationException("TODO: implement validate");
    }

    // TODO: Support nested validation for complex objects.
    //   Example:
    //     Validator<Address> addressValidator = ...;
    //     Validator<User> userValidator = Validator
    //         .of(u -> u.name() != null, "name required")
    //         .andNested(User::address, addressValidator, "address");
    //
    //   public <U> Validator<T> andNested(Function<T, U> extractor,
    //                                      Validator<U> nestedValidator,
    //                                      String fieldName) { ... }

    // Bonus: Async validation returning CompletableFuture<ValidationResult>.
}
