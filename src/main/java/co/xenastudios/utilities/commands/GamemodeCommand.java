package co.xenastudios.utilities.commands;

import co.xenastudios.utilities.UtilitiesPlugin;
import co.xenastudios.utilities.utilities.MsgUtility;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;

/**
 * Handles the /gamemode command and its subcommands.
 * Registers /gamemode <survival|creative|adventure|spectator> using the existing command classes,
 * only if they are enabled in the config.
 */
public class GamemodeCommand {

	/**
	 * Creates the /gamemode command and its subcommands for registration.
	 *
	 * @param plugin The main plugin instance.
	 * @return The constructed command node for registration.
	 */
	public static LiteralCommandNode<CommandSourceStack> createCommand(final UtilitiesPlugin plugin) {
		// Build the base /gamemode command
		LiteralArgumentBuilder<CommandSourceStack> gamemodeCommand = Commands.literal("gamemode");

		// Retrieve the required permission from the config (optional)
		String permission = plugin.getConfig().getString("commands.gamemode.permission");
		if (permission != null && !permission.isEmpty()) {
			gamemodeCommand.requires(sender -> sender.getSender().hasPermission(permission));
		}

		// /gamemode by itself: show usage or error
		gamemodeCommand.executes(ctx -> {
			CommandSender sender = ctx.getSource().getSender();
			MsgUtility.send(
					sender,
					plugin.getConfig().getString(
							"messages.error.usage",
							"<red>Usage: <usage></red>"
					),
					Placeholder.unparsed(
							"usage", "/gamemode <survival|creative|adventure|spectator> [player]"
					)
			);
			return com.mojang.brigadier.Command.SINGLE_SUCCESS;
		});

		// Register subcommands for each gamemode only if enabled in config
		if (plugin.getConfig().getBoolean("commands.survival.enabled", false)) {
			gamemodeCommand.then(SurvivalCommand.createCommand(plugin));
		}
		if (plugin.getConfig().getBoolean("commands.creative.enabled", false)) {
			gamemodeCommand.then(CreativeCommand.createCommand(plugin));
		}
		if (plugin.getConfig().getBoolean("commands.adventure.enabled", false)) {
			gamemodeCommand.then(AdventureCommand.createCommand(plugin));
		}
		if (plugin.getConfig().getBoolean("commands.spectator.enabled", false)) {
			gamemodeCommand.then(SpectatorCommand.createCommand(plugin));
		}

		// Build and return the complete command node
		return gamemodeCommand.build();
	}
}