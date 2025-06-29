package co.xenastudios.utilities.commands;

import co.xenastudios.utilities.UtilitiesPlugin;
import co.xenastudios.utilities.utilities.MsgUtility;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;

/**
 * Handles the /store command, sending the store link to the sender.
 */
public class StoreCommand {

	/**
	 * Creates the /store command node for registration.
	 *
	 * @param plugin The main plugin instance.
	 * @return The constructed command node for registration.
	 */
	public static LiteralCommandNode<CommandSourceStack> createCommand(final UtilitiesPlugin plugin) {
		// Build the base /store command
		var storeCommand = Commands.literal("store");

		// Retrieve the required permission from the config
		String permission = plugin.getConfig().getString("commands.store.permission");

		// Restrict command usage to those with the permission, if set
		if (permission != null && !permission.isEmpty()) {
			storeCommand.requires(sender -> sender.getSender().hasPermission(permission));
		}

		// Main /store command execution: send store message
		storeCommand.executes(ctx -> {
			CommandSender sender = ctx.getSource().getSender();
			MsgUtility.send(
					sender,
					plugin.getConfig().getString(
							"commands.store.message",
							"<white>Store: <gold><click:open_url:'https://store.server.com'>https://store.server.com</click></gold></white>"
					)
			);
			return Command.SINGLE_SUCCESS;
		});

		// Build and return the complete command node
		return storeCommand.build();
	}
}