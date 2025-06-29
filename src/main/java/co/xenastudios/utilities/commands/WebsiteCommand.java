package co.xenastudios.utilities.commands;

import co.xenastudios.utilities.UtilitiesPlugin;
import co.xenastudios.utilities.utilities.MsgUtility;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;

/**
 * Handles the /website command, sending the website link to the sender.
 */
public class WebsiteCommand {

	/**
	 * Creates the /website command node for registration.
	 *
	 * @param plugin The main plugin instance.
	 * @return The constructed command node for registration.
	 */
	public static LiteralCommandNode<CommandSourceStack> createCommand(final UtilitiesPlugin plugin) {
		// Build the base /website command
		var websiteCommand = Commands.literal("website");

		// Retrieve the required permission from the config
		String permission = plugin.getConfig().getString("commands.website.permission");

		// Restrict command usage to those with the permission, if set
		if (permission != null && !permission.isEmpty()) {
			websiteCommand.requires(sender -> sender.getSender().hasPermission(permission));
		}

		// Main /website command execution: send website message
		websiteCommand.executes(ctx -> {
			CommandSender sender = ctx.getSource().getSender();
			MsgUtility.send(
					sender,
					plugin.getConfig().getString(
							"commands.website.message",
							"<white>Website: <gold><click:open_url:'https://server.com'>https://server.com</click></gold></white>"
					)
			);
			return Command.SINGLE_SUCCESS;
		});

		// Build and return the complete command node
		return websiteCommand.build();
	}
}