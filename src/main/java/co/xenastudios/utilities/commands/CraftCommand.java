package co.xenastudios.utilities.commands;

import co.xenastudios.utilities.UtilitiesPlugin;
import co.xenastudios.utilities.utilities.MsgUtility;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handles the /craft command, allowing players to open a crafting table menu.
 */
public class CraftCommand {

	/**
	 * Creates the /craft command node for registration.
	 *
	 * @param plugin The main plugin instance.
	 * @return The constructed command node for registration.
	 */
	public static LiteralCommandNode<CommandSourceStack> createCommand(final UtilitiesPlugin plugin) {
		// Build the base /craft command
		var craftCommand = Commands.literal("craft");

		// Retrieve the required permission from the config
		String permission = plugin.getConfig().getString("commands.craft.permission");

		// Restrict command usage to those with the permission, if set
		if (permission != null && !permission.isEmpty()) {
			craftCommand.requires(sender -> sender.getSender().hasPermission(permission));
		}

		// Main /craft command execution: open crafting table for players
		craftCommand.executes(ctx -> {
			CommandSender sender = ctx.getSource().getSender();
			if (!(sender instanceof Player player)) {
				// Send error if not a player
				MsgUtility.send(
						sender,
						plugin.getConfig().getString(
								"messages.error.player-only",
								"<red>Only a player can execute this command!</red>"
						)
				);
				return Command.SINGLE_SUCCESS;
			}

			// Open the crafting table menu for the player
			player.openWorkbench(null, true);
			return Command.SINGLE_SUCCESS;
		});

		// Build and return the complete command node
		return craftCommand.build();
	}
}