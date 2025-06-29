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
 * Handles the /anvil command, allowing players to open an anvil menu.
 */
public class AnvilCommand {

	/**
	 * Creates the /anvil command node for registration.
	 *
	 * @param plugin The main plugin instance.
	 * @return The constructed command node for registration.
	 */
	public static LiteralCommandNode<CommandSourceStack> createCommand(final UtilitiesPlugin plugin) {
		// Build the base /anvil command
		var anvilCommand = Commands.literal("anvil");

		// Retrieve the required permission from the config
		String permission = plugin.getConfig().getString("commands.anvil.permission");

		// Restrict command usage to those with the permission, if set
		if (permission != null && !permission.isEmpty()) {
			anvilCommand.requires(sender -> sender.getSender().hasPermission(permission));
		}

		// Main /anvil command execution: open anvil menu for players
		anvilCommand.executes(ctx -> {
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

			// Open the anvil menu for the player
			player.openAnvil(null, true);
			return Command.SINGLE_SUCCESS;
		});

		// Build and return the complete command node
		return anvilCommand.build();
	}
}