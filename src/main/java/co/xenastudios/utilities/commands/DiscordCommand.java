package co.xenastudios.utilities.commands;

import co.xenastudios.utilities.UtilitiesPlugin;
import co.xenastudios.utilities.utilities.MsgUtility;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;

/**
 * Handles the /discord command, sending the Discord link to the sender.
 */
public class DiscordCommand {

	/**
	 * Creates the /discord command node for registration.
	 *
	 * @param plugin The main plugin instance.
	 * @return The constructed command node for registration.
	 */
	public static LiteralCommandNode<CommandSourceStack> createCommand(final UtilitiesPlugin plugin) {
		// Build the base /discord command
		var discordCommand = Commands.literal("discord");

		// Retrieve the required permission from the config
		String permission = plugin.getConfig().getString("commands.discord.permission");

		// Restrict command usage to those with the permission, if set
		if (permission != null && !permission.isEmpty()) {
			discordCommand.requires(sender -> sender.getSender().hasPermission(permission));
		}

		// Main /discord command execution: send Discord message
		discordCommand.executes(ctx -> {
			CommandSender sender = ctx.getSource().getSender();
			MsgUtility.send(
					sender,
					plugin.getConfig().getString(
							"commands.discord.message",
							"<white>Discord: <gold><click:open_url:'https://discord.gg/server'>https://discord.gg/server</click></gold></white>"
					)
			);
			return Command.SINGLE_SUCCESS;
		});

		// Build and return the complete command node
		return discordCommand.build();
	}
}