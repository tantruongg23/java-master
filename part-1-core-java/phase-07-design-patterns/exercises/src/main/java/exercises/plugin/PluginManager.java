package exercises.plugin;

import java.util.*;

/**
 * Plugin manager implementing the <b>Factory</b> pattern with a registry.
 *
 * <p>Plugins are registered by name. The manager handles the full lifecycle
 * (init → execute → destroy) and wraps each plugin in a {@code PluginProxy}
 * for cross-cutting concerns (logging, permissions, timing).</p>
 *
 * <h3>Design patterns used</h3>
 * <ul>
 *   <li><b>Factory</b> — create / look up plugins by name</li>
 *   <li><b>Proxy</b> — transparent wrapper for logging and access control</li>
 *   <li><b>Command</b> — each plugin operation is a reversible command</li>
 * </ul>
 *
 * @see Plugin
 */
public class PluginManager {

    /**
     * Functional factory for creating plugin instances.
     */
    @FunctionalInterface
    public interface PluginFactory {
        Plugin create();
    }

    private final Map<String, PluginFactory> registry = new HashMap<>();
    private final Map<String, Plugin> activePlugins = new LinkedHashMap<>();

    /**
     * Register a plugin factory under the given name.
     *
     * @param name    unique plugin identifier
     * @param factory supplier that creates a new plugin instance
     */
    public void register(String name, PluginFactory factory) {
        registry.put(name, factory);
    }

    /**
     * Load and initialise a plugin by name.
     *
     * <p>TODO:</p>
     * <ol>
     *   <li>Look up the factory in the registry.</li>
     *   <li>Create the plugin instance.</li>
     *   <li>Wrap it in a {@code PluginProxy} (see below).</li>
     *   <li>Call {@link Plugin#init()} on the proxy.</li>
     *   <li>Store it in {@code activePlugins}.</li>
     * </ol>
     *
     * @param name the registered plugin name
     * @return the loaded (proxied) plugin
     * @throws IllegalArgumentException if no factory is registered for the name
     */
    public Plugin loadPlugin(String name) {
        // TODO: implement plugin loading with proxy wrapping
        throw new UnsupportedOperationException("TODO: implement loadPlugin");
    }

    /**
     * Execute a command on a loaded plugin.
     *
     * <p>TODO: wrap the execution in a {@code PluginCommand} that supports undo.</p>
     *
     * @param pluginName the name of the loaded plugin
     * @param command    the command string to pass to the plugin
     */
    public void execute(String pluginName, String command) {
        // TODO: look up active plugin, execute command, record for undo
        throw new UnsupportedOperationException("TODO: implement execute");
    }

    /**
     * Unload a plugin: call {@link Plugin#destroy()} and remove from active map.
     *
     * @param name the plugin to unload
     */
    public void unloadPlugin(String name) {
        // TODO: destroy and remove the plugin
        throw new UnsupportedOperationException("TODO: implement unloadPlugin");
    }

    /**
     * Unload all active plugins in reverse load order.
     */
    public void shutdown() {
        // TODO: iterate activePlugins in reverse order, destroy each
        throw new UnsupportedOperationException("TODO: implement shutdown");
    }

    // TODO: Implement PluginProxy:
    //
    //   class PluginProxy implements Plugin {
    //       private final Plugin target;
    //
    //       // Add: logging before/after each method call
    //       // Add: permission check before execute()
    //       // Add: timing of execute() calls
    //   }
    //
    // TODO: Implement PluginCommand:
    //
    //   class PluginCommand {
    //       void execute();
    //       void undo();
    //   }
    //
    // Bonus: Implement dependency graph resolution.
    //   - Plugin declares getDependencies() returning List<String>.
    //   - loadPlugin resolves dependencies first (topological sort).
}
