package co.xenastudios.utilities.commands;

import co.xenastudios.utilities.UtilitiesPlugin;
import co.xenastudios.utilities.utilities.MsgUtility;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

/**
 * Handles the /trash command, allowing players to open a trash bin (hopper inventory).
 */
public class TrashCommand {

	/**
	 * Creates the /trash command node for registration.
	 *
	 * @param plugin The main plugin instance.
	 * @return The constructed command node for registration.
	 */
	public static LiteralCommandNode<CommandSourceStack> createCommand(final UtilitiesPlugin plugin) {
		// Build the base /trash command
		var trashCommand = Commands.literal("trash");

		// Retrieve the required permission from the config
		String permission = plugin.getConfig().getString("commands.trash.permission");

		// Restrict command usage to those with the permission, if set
		if (permission != null && !permission.isEmpty()) {
			trashCommand.requires(sender -> sender.getSender().hasPermission(permission));
		}

		// Main /trash command execution: open trash bin for players
		trashCommand.executes(ctx -> {
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

			// Open a hopper inventory as a trash bin for the player
			Inventory trash = Bukkit.createInventory(player, InventoryType.HOPPER, "Trash");
			player.openInventory(trash);
			return Command.SINGLE_SUCCESS;
		});

		// Build and return the complete command node
		return trashCommand.build();
	}
}