package uk.co.arcanegames.AutoUBL;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Added Multi-Threaded configuration IO
 *
 * @author XHawk87
 */
public abstract class MultiThreadedJavaPlugin extends JavaPlugin {

    private File configFile;
    // Keeps track of the current save
    private int saveCounter = 0;

    /**
     * Intended for use within anonymous inner classes in place of 'this'
     *
     * @return this plugin
     */
    public Plugin getPlugin() {
        return this;
    }

    /**
     * @return The configuration file
     */
    public File getConfigFile() {
        if (configFile == null) {
            configFile = new File(getDataFolder(), "config.yml");
        }
        return configFile;
    }

    @Override
    /**
     * Save the plugin configuration file asynchronously
     */
    public void saveConfig() {
        FileConfiguration config = getConfig();

        // Save the configuration data to a string ready for saving
        final String data = config.saveToString();

        // Create a unique identifier for this save
        saveCounter++;
        final int saveId = saveCounter;

        // Schedule the data to be written asynchronously
        new BukkitRunnable() {
            @Override
            public void run() {

                // If the data to be written is already outdated, discard it
                if (saveCounter == saveId) {

                    // Ensure that no two threads are writing to the config file at the same time
                    synchronized (getConfigFile()) {

                        // By the time the lock has been released, the data may already be outdated, if so, discard it
                        if (saveCounter == saveId) {

                            // Save the data to the file
                            try (FileWriter writer = new FileWriter(getConfigFile())) {
                                writer.write(data);
                            } catch (IOException ex) {

                                // Provide contextual information on the stack trace
                                getLogger().log(Level.SEVERE, "Could not save config.yml", ex);
                            }
                        }
                    }
                }
            }
        }.runTaskAsynchronously(this);
    }

    /**
     * Load the configuration file asynchronously, and run a task when it is
     * completed
     *
     * @param notifier The task to be run
     */
    public void reloadConfigAsync(BukkitRunnable notifier) {

        // Schedule the load to be done asynchronously
        new BukkitRunnable() {
            private BukkitRunnable notifier;

            public BukkitRunnable setNotifier(BukkitRunnable notifier) {
                this.notifier = notifier;

                // Allow this method to be chain-called
                return this;
            }

            @Override
            public void run() {
                reloadConfig();
                // Only run the task if the plugin is still enabled.
                if (isEnabled()) notifier.runTask(getPlugin());
            }
        }.setNotifier(notifier).runTaskAsynchronously(this);
    }

    @Override
    /**
     * This is not safe to use in the main thread. Use reloadConfigAsync instead
     */
    public void reloadConfig() {
        if (getServer().isPrimaryThread()) {
            getLogger().warning("Reloading config on main server thread! Use reloadConfigAsync method instead");
        }

        // Ensure the config file is not being written to while reading
        synchronized (getConfigFile()) {
            super.reloadConfig();
        }
    }
}
