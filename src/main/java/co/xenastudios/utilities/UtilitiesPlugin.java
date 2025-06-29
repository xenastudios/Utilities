package co.xenastudios.utilities;

import co.xenastudios.utilities.commands.CommandManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main class for the Utilities plugin.
 * Handles plugin lifecycle events and initialization.
 */
public class UtilitiesPlugin extends JavaPlugin {

	/**
	 * Called when the plugin is enabled.
	 * Initializes configuration and registers commands.
	 */
	@Override
	public void onEnable() {
		// Save the default config.yml if it does not exist
		this.saveDefaultConfig();

		// Register all plugin commands
		CommandManager.registerCommands(this);

		this.getLogger().info("Plugin enabled!");
	}

	/**
	 * Called when the plugin is disabled.
	 * Handles any necessary cleanup.
	 */
	@Override
	public void onDisable() {
		this.getLogger().info("Plugin disabled!");
	}
}