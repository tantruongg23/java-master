package exercises.audit;

import exercises.audit.Auditable.AuditLevel;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * AOP aspect that intercepts methods annotated with {@link Auditable}
 * and records audit entries at the configured verbosity level.
 *
 * <p><b>Exercise 2 — Annotation-Driven Audit Logger</b></p>
 *
 * <p>Implementation steps:
 * <ol>
 *   <li>Extract the {@link AuditLevel} from the annotation.</li>
 *   <li>Capture pre-invocation data (method name, arguments, timestamp).</li>
 *   <li>Proceed with the target method invocation.</li>
 *   <li>Capture post-invocation data (return value, execution time).</li>
 *   <li>Build an audit entry and store it.</li>
 *   <li>Handle exceptions — log them in FULL mode, then re-throw.</li>
 * </ol>
 *
 * <p><b>Bonus:</b> Make storage pluggable by defining an {@code AuditStore}
 * interface and injecting it instead of using the embedded list.
 */
@Aspect
@Component
public class AuditAspect {

    private final List<AuditEntry> auditLog = Collections.synchronizedList(new ArrayList<>());

    /**
     * Intercepts any method annotated with {@link Auditable}.
     *
     * @param joinPoint the proceeding join point
     * @param auditable the annotation instance (gives access to the level)
     * @return the original method's return value
     * @throws Throwable if the target method throws
     */
    @Around("@annotation(auditable)")
    public Object audit(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        AuditLevel level = auditable.value();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // TODO: Implement the audit logic
        //
        // 1. Record start time
        //    long start = System.nanoTime();
        //
        // 2. Proceed with method execution (handle success + failure)
        //    Object result = joinPoint.proceed();
        //
        // 3. Calculate elapsed time in milliseconds
        //
        // 4. Build an AuditEntry based on the level:
        //    - BASIC:    method name, elapsed ms
        //    - DETAILED: + arguments (Arrays.toString), return value
        //    - FULL:     + caller info (Thread.currentThread().getStackTrace()),
        //                  timestamp (Instant.now()), thread name
        //
        // 5. Add the entry to auditLog
        //
        // 6. Return the result (or re-throw the exception)

        throw new UnsupportedOperationException("Implement audit advice — see TODO above");
    }

    /** Returns an unmodifiable view of all recorded audit entries. */
    public List<AuditEntry> getAuditLog() {
        return Collections.unmodifiableList(auditLog);
    }

    /**
     * Immutable audit entry.
     *
     * TODO: Add fields as needed for each AuditLevel.
     *       Consider using a record:
     *       {@code record AuditEntry(String methodName, long elapsedMs, ...)}
     */
    public record AuditEntry(
            String methodName,
            AuditLevel level,
            long elapsedMs
            // TODO: Add nullable fields for DETAILED / FULL levels:
            //   String arguments,
            //   String returnValue,
            //   String callerInfo,
            //   Instant timestamp,
            //   String threadName,
            //   String exceptionMessage
    ) {}
}
