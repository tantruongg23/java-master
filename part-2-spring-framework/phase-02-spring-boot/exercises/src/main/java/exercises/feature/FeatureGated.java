package exercises.feature;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Guards a method behind a feature flag.
 *
 * <p>An AOP aspect checks the specified flag via {@link FeatureFlagService}
 * before allowing the method to execute. If the flag is disabled, a
 * {@code FeatureDisabledException} is thrown.
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * @FeatureGated("export-csv")
 * public byte[] exportTasksAsCsv() { ... }
 * }</pre>
 *
 * <p><b>TODO:</b> Create a {@code FeatureGatedAspect} class with an
 * {@code @Around} or {@code @Before} advice that:
 * <ol>
 *   <li>Reads the {@link #value()} to get the flag name.</li>
 *   <li>Calls {@code featureFlagService.isEnabled(flagName)}.</li>
 *   <li>If disabled, throws {@code FeatureDisabledException}.</li>
 *   <li>If enabled, proceeds with the method invocation.</li>
 * </ol>
 *
 * <p><b>TODO:</b> Create a {@code FeatureDisabledException} (unchecked)
 * with the flag name in the message.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FeatureGated {

    /**
     * The name of the feature flag that must be enabled for
     * the annotated method to execute.
     *
     * @return feature flag name (e.g., "dark-mode", "export-csv")
     */
    String value();
}
