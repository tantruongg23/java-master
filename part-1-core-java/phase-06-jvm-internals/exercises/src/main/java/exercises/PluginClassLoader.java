package exercises;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Exercise 4 — Custom ClassLoader (Plugin System)
 *
 * <p>A classloader that loads {@code .class} files from a configurable directory,
 * simulating a plugin system with classloader isolation.</p>
 *
 * <h3>Concepts demonstrated</h3>
 * <ul>
 *   <li>Parent-delegation model override.</li>
 *   <li>Classloader isolation — two loaders can load different versions of the same class.</li>
 *   <li>Classloader leaks — when strong references prevent GC of the loader and all its classes.</li>
 *   <li>Hot-reload simulation — discard old loader, create a new one.</li>
 * </ul>
 *
 * <h3>Setup</h3>
 * <p>Create a directory (e.g., {@code plugins/}) with compiled {@code .class} files.
 * Each plugin must implement a known interface (e.g., {@code Plugin}).</p>
 *
 * <pre>
 * // Plugin.java (in main project)
 * public interface Plugin {
 *     String getName();
 *     void execute();
 * }
 *
 * // GreeterPlugin.java (compiled separately, placed in plugins/)
 * public class GreeterPlugin implements Plugin {
 *     public String getName() { return "Greeter v1"; }
 *     public void execute() { System.out.println("Hello from Greeter!"); }
 * }
 * </pre>
 */
public class PluginClassLoader extends ClassLoader {

    // ──────────────────────────────────────────────────────────────
    // Plugin interface
    // ──────────────────────────────────────────────────────────────

    /**
     * Interface that all plugins must implement.
     * Defined here so both the main app and plugin classes share the same type.
     */
    public interface Plugin {
        String getName();
        void execute();
    }

    // ──────────────────────────────────────────────────────────────
    // Fields
    // ──────────────────────────────────────────────────────────────

    private final Path pluginDirectory;

    /**
     * @param pluginDirectory directory containing {@code .class} files
     * @param parent          parent classloader (typically the application classloader)
     */
    public PluginClassLoader(Path pluginDirectory, ClassLoader parent) {
        super(parent);
        this.pluginDirectory = pluginDirectory;
    }

    // ──────────────────────────────────────────────────────────────
    // ClassLoader overrides
    // ──────────────────────────────────────────────────────────────

    /**
     * Find a class by reading its {@code .class} file from the plugin directory.
     *
     * <p>Implementation tips:
     * <ul>
     *   <li>Convert the fully-qualified class name to a file path
     *       (e.g., {@code com.example.Foo} → {@code com/example/Foo.class}).</li>
     *   <li>Read the bytes with {@link Files#readAllBytes(Path)}.</li>
     *   <li>Call {@link #defineClass(String, byte[], int, int)} to create the Class object.</li>
     * </ul>
     *
     * @param name fully-qualified class name
     * @return the loaded class
     * @throws ClassNotFoundException if the file doesn't exist
     */
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        // TODO: Convert class name to file path.
        // TODO: Check if the .class file exists in pluginDirectory.
        // TODO: Read bytes, call defineClass.
        // TODO: Throw ClassNotFoundException if file not found.
        throw new UnsupportedOperationException("TODO — implement findClass");
    }

    // ──────────────────────────────────────────────────────────────
    // Plugin loading API
    // ──────────────────────────────────────────────────────────────

    /**
     * Load a plugin class by name and return an instance.
     *
     * @param className fully-qualified class name
     * @return a Plugin instance
     * @throws Exception if loading or instantiation fails
     */
    public Plugin loadPlugin(String className) throws Exception {
        // TODO: Load the class with loadClass(className).
        // TODO: Verify it implements Plugin.
        // TODO: Instantiate via getConstructor().newInstance().
        throw new UnsupportedOperationException("TODO — implement loadPlugin");
    }

    // ──────────────────────────────────────────────────────────────
    // Demonstration methods
    // ──────────────────────────────────────────────────────────────

    /**
     * Demonstrate classloader isolation: load the same class name from two different
     * directories and show that they are distinct classes.
     *
     * @param dir1 first plugin directory
     * @param dir2 second plugin directory
     * @param className class to load from both
     */
    public static void demonstrateIsolation(Path dir1, Path dir2, String className) {
        // TODO: Create two PluginClassLoader instances (one per directory).
        // TODO: Load the same className from each.
        // TODO: Show that class1 != class2 (different Class objects).
        // TODO: Show that instanceof checks across loaders fail.
        throw new UnsupportedOperationException("TODO — implement demonstrateIsolation");
    }

    /**
     * Demonstrate classloader leak: keep a strong reference to a loaded class
     * (or its instance) and show that the classloader cannot be GC'd.
     */
    public static void demonstrateClassLoaderLeak() {
        // TODO: Create a PluginClassLoader, load a class, store a reference.
        // TODO: Set the loader to null.
        // TODO: Call System.gc() and show that the loader (and all its classes) are NOT collected
        //       because the stored reference indirectly retains the loader.
        // TODO: Then clear the reference and show it CAN be collected.
        throw new UnsupportedOperationException("TODO — implement demonstrateClassLoaderLeak");
    }

    /**
     * Simulate hot-reloading: load a plugin, execute it, then reload a modified version.
     *
     * @param pluginDir plugin directory
     * @param className class to reload
     */
    public static void simulateHotReload(Path pluginDir, String className) {
        // TODO: Load and execute the plugin.
        // TODO: Discard the old classloader (set to null, clear references).
        // TODO: Prompt user to replace the .class file.
        // TODO: Create a NEW PluginClassLoader and reload the class.
        // TODO: Execute again — should reflect the new implementation.
        throw new UnsupportedOperationException("TODO — implement simulateHotReload");
    }

    // ──────────────────────────────────────────────────────────────
    // Main
    // ──────────────────────────────────────────────────────────────

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: java exercises.PluginClassLoader <plugin-dir> <class-name>");
            System.err.println("Example: java exercises.PluginClassLoader ./plugins GreeterPlugin");
            System.exit(1);
        }

        Path pluginDir = Path.of(args[0]);
        String className = args[1];

        PluginClassLoader loader = new PluginClassLoader(pluginDir, PluginClassLoader.class.getClassLoader());
        Plugin plugin = loader.loadPlugin(className);

        System.out.println("Loaded plugin: " + plugin.getName());
        plugin.execute();
    }
}
