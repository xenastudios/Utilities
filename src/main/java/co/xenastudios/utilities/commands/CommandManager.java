package co.xenastudios.utilities.commands;

import co.xenastudios.utilities.UtilitiesPlugin;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Handles registration of all plugin commands based on configuration.
 */
public class CommandManager {

	/**
	 * Registers commands with the server, based on the plugin's configuration.
	 * Only registers commands if their 'enabled' field is true.
	 *
	 * @param plugin The main plugin instance.
	 */
	public static void registerCommands(UtilitiesPlugin plugin) {
		FileConfiguration config = plugin.getConfig();

		plugin.getLifecycleManager().registerEventHandler(
				LifecycleEvents.COMMANDS,
				commands -> {
					// Register each command if enabled in config (default: false)
					if (config.getBoolean("commands.adventure.enabled", false)) {
						commands.registrar().register(
								AdventureCommand.createCommand(plugin),
								"Set your gamemode to Adventure.",
								config.getStringList("commands.adventure.aliases")
						);
					}
					if (config.getBoolean("commands.anvil.enabled", false)) {
						commands.registrar().register(
								AnvilCommand.createCommand(plugin),
								"Open an anvil menu.",
								config.getStringList("commands.anvil.aliases")
						);
					}
					if (config.getBoolean("commands.craft.enabled", false)) {
						commands.registrar().register(
								CraftCommand.createCommand(plugin),
								"Open a crafting table menu.",
								config.getStringList("commands.craft.aliases")
						);
					}
					if (config.getBoolean("commands.creative.enabled", false)) {
						commands.registrar().register(
								CreativeCommand.createCommand(plugin),
								"Set your gamemode to Creative.",
								config.getStringList("commands.creative.aliases")
						);
					}
					if (config.getBoolean("commands.discord.enabled", false)) {
						commands.registrar().register(
								DiscordCommand.createCommand(plugin),
								"Get our Discord's link.",
								config.getStringList("commands.discord.aliases")
						);
					}
					if (config.getBoolean("commands.fly.enabled", false)) {
						commands.registrar().register(
								FlyCommand.createCommand(plugin),
								"Set your fly mode.",
								config.getStringList("commands.fly.aliases")
						);
					}
					if (config.getBoolean("commands.gamemode.enabled", false)) {
						commands.registrar().register(
								GamemodeCommand.createCommand(plugin),
								"Change your gamemode.",
								config.getStringList("commands.gamemode.aliases")
						);
					}
					if (config.getBoolean("commands.grinder.enabled", false)) {
						commands.registrar().register(
								GrinderCommand.createCommand(plugin),
								"Open a grindstone menu.",
								config.getStringList("commands.grinder.aliases")
						);
					}
					if (config.getBoolean("commands.loom.enabled", false)) {
						commands.registrar().register(
								LoomCommand.createCommand(plugin),
								"Open a loom menu.",
								config.getStringList("commands.loom.aliases")
						);
					}
					if (config.getBoolean("commands.map.enabled", false)) {
						commands.registrar().register(
								MapCommand.createCommand(plugin),
								"Get our map's link.",
								config.getStringList("commands.map.aliases")
						);
					}
					if (config.getBoolean("commands.setrotation.enabled", false)) {
						commands.registrar().register(
								SetRotationCommand.createCommand(plugin),
								"Set your rotation.",
								config.getStringList("commands.setrotation.aliases")
						);
					}
					if (config.getBoolean("commands.smith.enabled", false)) {
						commands.registrar().register(
								SmithCommand.createCommand(plugin),
								"Open a smithing table menu.",
								config.getStringList("commands.smith.aliases")
						);
					}
					if (config.getBoolean("commands.spectator.enabled", false)) {
						commands.registrar().register(
								SpectatorCommand.createCommand(plugin),
								"Set your gamemode to Spectator.",
								config.getStringList("commands.spectator.aliases")
						);
					}
					if (config.getBoolean("commands.speed.enabled", false)) {
						commands.registrar().register(
								SpeedCommand.createCommand(plugin),
								"Manage your speed.",
								config.getStringList("commands.speed.aliases")
						);
					}
					if (config.getBoolean("commands.store.enabled", false)) {
						commands.registrar().register(
								StoreCommand.createCommand(plugin),
								"Get our store's link.",
								config.getStringList("commands.store.aliases")
						);
					}
					if (config.getBoolean("commands.survival.enabled", false)) {
						commands.registrar().register(
								SurvivalCommand.createCommand(plugin),
								"Set your gamemode to Survival.",
								config.getStringList("commands.survival.aliases")
						);
					}
					if (config.getBoolean("commands.top.enabled", false)) {
						commands.registrar().register(
								TopCommand.createCommand(plugin),
								"Teleport to the highest block at your location.",
								config.getStringList("commands.top.aliases")
						);
					}
					if (config.getBoolean("commands.trash.enabled", false)) {
						commands.registrar().register(
								TrashCommand.createCommand(plugin),
								"Open a trash bin to delete items.",
								config.getStringList("commands.trash.aliases")
						);
					}
					if (config.getBoolean("commands.utilitiesadmin.enabled", false)) {
						commands.registrar().register(
								UtilitiesAdminCommand.createCommand(plugin),
								"Manage your Utilities plugin.",
								config.getStringList("commands.utilitiesadmin.aliases")
						);
					}
					if (config.getBoolean("commands.website.enabled", false)) {
						commands.registrar().register(
								WebsiteCommand.createCommand(plugin),
								"Get our website's link.",
								config.getStringList("commands.website.aliases")
						);
					}
				}
		);

		// Log command registration using the plugin's logger
		plugin.getLogger().info("Commands registered successfully.");
	}
}