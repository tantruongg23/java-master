package exercises.feature;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

/**
 * Service for querying feature flag states.
 *
 * <p>Feature flags can be defined in {@code application.yml} under
 * {@code app.features} and overridden via environment variables using
 * Spring's relaxed binding (e.g., {@code APP_FEATURES_DARK_MODE=true}).
 *
 * <p><b>TODO:</b>
 * <ol>
 *   <li>Inject {@code AppProperties} (or a dedicated
 *       {@code @ConfigurationProperties} class for features).</li>
 *   <li>Implement {@link #isEnabled(String)} by looking up the flag
 *       in the properties map.</li>
 *   <li>Implement {@link #getAllFlags()} to return all known flags.</li>
 *   <li>(Bonus) Add a {@link #setEnabled(String, boolean)} method for
 *       runtime toggling — note that this only affects the in-memory
 *       state and will not persist across restarts.</li>
 * </ol>
 */
@Service
public class FeatureFlagService {

    // TODO: Inject the feature flag configuration
    //
    // private final AppProperties properties;
    //
    // public FeatureFlagService(AppProperties properties) {
    //     this.properties = properties;
    // }

    /**
     * Checks whether a feature flag is enabled.
     *
     * @param flagName the name of the feature flag (e.g., "dark-mode")
     * @return {@code true} if the flag exists and is enabled, {@code false} otherwise
     */
    public boolean isEnabled(String flagName) {
        // TODO: Look up the flag in properties.getFeatures()
        //       Return false if the flag is not defined (fail-safe)
        //
        // return properties.getFeatures().getOrDefault(flagName, false);

        throw new UnsupportedOperationException("Implement isEnabled — see TODO above");
    }

    /**
     * Returns an unmodifiable view of all feature flags and their states.
     *
     * @return map of flag name → enabled state
     */
    public Map<String, Boolean> getAllFlags() {
        // TODO: return Collections.unmodifiableMap(properties.getFeatures());

        return Collections.emptyMap();
    }

    /**
     * Toggles a feature flag at runtime (in-memory only).
     *
     * <p><b>Bonus:</b> This is used by the custom Actuator endpoint
     * to allow POST-based flag toggling.
     *
     * @param flagName the flag to toggle
     * @param enabled  the desired state
     */
    public void setEnabled(String flagName, boolean enabled) {
        // TODO: properties.getFeatures().put(flagName, enabled);

        throw new UnsupportedOperationException("Implement setEnabled — see TODO above");
    }
}
