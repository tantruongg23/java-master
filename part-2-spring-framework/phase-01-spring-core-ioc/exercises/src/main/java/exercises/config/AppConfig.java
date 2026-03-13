package exercises.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 * Root configuration for Phase 01 exercises.
 *
 * <p>Demonstrates:
 * <ul>
 *   <li>Java-based configuration with {@code @Configuration}</li>
 *   <li>Component scanning across exercise packages</li>
 *   <li>Profile-specific bean definitions</li>
 *   <li>Externalized properties via {@code @PropertySource}</li>
 *   <li>AspectJ auto-proxy for AOP support</li>
 * </ul>
 */
@Configuration
@ComponentScan(basePackages = "exercises")
@EnableAspectJAutoProxy
@PropertySource("classpath:application.properties")
public class AppConfig {

    // ── Dev Profile ──────────────────────────────────────────────────

    /**
     * Simulated DataSource for the {@code dev} profile.
     *
     * TODO: Create a simple DataSourceConfig POJO (url, username, poolSize)
     *       and return an instance populated from dev-specific properties.
     */
    @Bean
    @Profile("dev")
    public Object devDataSource(
            @Value("${datasource.url:jdbc:h2:mem:devdb}") String url,
            @Value("${datasource.username:sa}") String username,
            @Value("${datasource.pool-size:5}") int poolSize) {

        // TODO: Replace Object with your DataSourceConfig class
        //   return new DataSourceConfig(url, username, poolSize);
        throw new UnsupportedOperationException("Implement devDataSource bean");
    }

    // ── Staging Profile ──────────────────────────────────────────────

    /**
     * Simulated DataSource for the {@code staging} profile.
     */
    @Bean
    @Profile("staging")
    public Object stagingDataSource(
            @Value("${datasource.url:jdbc:postgresql://staging-host:5432/stagingdb}") String url,
            @Value("${datasource.username:staging_user}") String username,
            @Value("${datasource.pool-size:10}") int poolSize) {

        // TODO: return new DataSourceConfig(url, username, poolSize);
        throw new UnsupportedOperationException("Implement stagingDataSource bean");
    }

    // ── Prod Profile ─────────────────────────────────────────────────

    /**
     * Simulated DataSource for the {@code prod} profile.
     */
    @Bean
    @Profile("prod")
    public Object prodDataSource(
            @Value("${datasource.url:jdbc:postgresql://prod-cluster:5432/proddb}") String url,
            @Value("${datasource.username:prod_admin}") String username,
            @Value("${datasource.pool-size:20}") int poolSize) {

        // TODO: return new DataSourceConfig(url, username, poolSize);
        throw new UnsupportedOperationException("Implement prodDataSource bean");
    }

    // ── Feature Flags (common, overridden per profile) ───────────────

    /**
     * TODO: Define beans or @Value-based flags for:
     *   - cacheEnabled (dev=false, staging=true, prod=true)
     *   - emailNotifications (dev=false, staging=false, prod=true)
     *
     * Consider creating a FeatureFlags record/class and
     * providing profile-specific @Bean methods.
     */

    // ── Cache Manager (profile-specific override demo) ───────────────

    /**
     * Stub cache manager for development — does nothing.
     *
     * TODO: Create a CacheManager interface and NoOpCacheManager implementation.
     */
    @Bean
    @Profile("dev")
    public Object devCacheManager() {
        // TODO: return new NoOpCacheManager();
        throw new UnsupportedOperationException("Implement devCacheManager");
    }

    /**
     * Real cache manager for production.
     *
     * TODO: Create a SimpleCacheManager implementation that uses a ConcurrentHashMap.
     */
    @Bean
    @Profile("prod")
    public Object prodCacheManager() {
        // TODO: return new SimpleCacheManager();
        throw new UnsupportedOperationException("Implement prodCacheManager");
    }
}
