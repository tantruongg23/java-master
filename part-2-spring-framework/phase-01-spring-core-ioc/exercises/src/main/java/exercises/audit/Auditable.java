package exercises.audit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method for automatic audit logging via AOP.
 *
 * <p>The {@link AuditAspect} intercepts calls to methods annotated with
 * {@code @Auditable} and records an audit entry whose verbosity is
 * controlled by the {@link AuditLevel}.
 *
 * <h3>Audit levels</h3>
 * <ul>
 *   <li>{@code BASIC}    — method name, execution time</li>
 *   <li>{@code DETAILED} — method name, arguments, return value, execution time</li>
 *   <li>{@code FULL}     — all of DETAILED + caller info, timestamp, thread name</li>
 * </ul>
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * @Auditable(AuditLevel.DETAILED)
 * public Order placeOrder(OrderRequest request) { ... }
 * }</pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {

    /**
     * The verbosity level for the audit entry.
     * Defaults to {@link AuditLevel#BASIC}.
     */
    AuditLevel value() default AuditLevel.BASIC;

    /**
     * Controls how much information the audit logger captures.
     */
    enum AuditLevel {
        /** Method name and execution time only. */
        BASIC,
        /** Method name, arguments, return value, and execution time. */
        DETAILED,
        /** Everything in DETAILED plus caller class/method, timestamp, and thread name. */
        FULL
    }
}
