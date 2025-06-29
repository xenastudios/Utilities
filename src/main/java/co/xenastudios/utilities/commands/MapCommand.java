package co.xenastudios.utilities.commands;

import co.xenastudios.utilities.UtilitiesPlugin;
import co.xenastudios.utilities.utilities.MsgUtility;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;

/**
 * Handles the /map command, sending the map link to the sender.
 */
public class MapCommand {

	/**
	 * Creates the /map command node for registration.
	 *
	 * @param plugin The main plugin instance.
	 * @return The constructed command node for registration.
	 */
	public static LiteralCommandNode<CommandSourceStack> createCommand(final UtilitiesPlugin plugin) {
		// Build the base /map command
		var mapCommand = Commands.literal("map");

		// Retrieve the required permission from the config
		String permission = plugin.getConfig().getString("commands.map.permission");

		// Restrict command usage to those with the permission, if set
		if (permission != null && !permission.isEmpty()) {
			mapCommand.requires(sender -> sender.getSender().hasPermission(permission));
		}

		// Main /map command execution: send map message
		mapCommand.executes(ctx -> {
			CommandSender sender = ctx.getSource().getSender();
			MsgUtility.send(
					sender,
					plugin.getConfig().getString(
							"commands.map.message",
							"<white>Map: <gold><click:open_url:'https://map.server.com'>https://map.server.com</click></gold></white>"
					)
			);
			return Command.SINGLE_SUCCESS;
		});

		// Build and return the complete command node
		return mapCommand.build();
	}
}