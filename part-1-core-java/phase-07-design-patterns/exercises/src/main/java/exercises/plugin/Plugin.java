package exercises.plugin;

/**
 * Plugin lifecycle interface.
 *
 * <p>Every plugin follows a three-phase lifecycle:</p>
 * <ol>
 *   <li>{@link #init()} — allocate resources, register handlers</li>
 *   <li>{@link #execute(String)} — perform work in response to commands</li>
 *   <li>{@link #destroy()} — release resources, deregister handlers</li>
 * </ol>
 *
 * <p>Implementations should be safe to init/destroy multiple times
 * (idempotent lifecycle).</p>
 *
 * @see PluginManager
 */
public interface Plugin {

    /**
     * Return the unique name of this plugin.
     *
     * @return plugin identifier
     */
    String getName();

    /**
     * Initialise the plugin. Called once after instantiation and before
     * any calls to {@link #execute(String)}.
     */
    void init();

    /**
     * Execute a command.
     *
     * @param command the command to process
     * @return textual result of the execution, or {@code null}
     */
    String execute(String command);

    /**
     * Destroy the plugin and release all held resources.
     * Called once when the plugin is being unloaded.
     */
    void destroy();
}
