package co.xenastudios.utilities.commands;

import co.xenastudios.utilities.UtilitiesPlugin;
import co.xenastudios.utilities.utilities.MsgUtility;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handles the /top command, teleporting players to the highest block at their location.
 */
public class TopCommand {

	/**
	 * Creates the /top command node for registration.
	 *
	 * @param plugin The main plugin instance.
	 * @return The constructed command node for registration.
	 */
	public static LiteralCommandNode<CommandSourceStack> createCommand(final UtilitiesPlugin plugin) {
		// Build the base /top command
		var topCommand = Commands.literal("top");

		// Retrieve the required permission from the config
		String permission = plugin.getConfig().getString("commands.top.permission");

		// Restrict command usage to those with the permission, if set
		if (permission != null && !permission.isEmpty()) {
			topCommand.requires(sender -> sender.getSender().hasPermission(permission));
		}

		// Main /top command execution: teleport player to the highest block
		topCommand.executes(ctx -> {
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

			// Get player's current location and world
			Location loc = player.getLocation();
			World world = player.getWorld();
			int x = loc.getBlockX();
			int z = loc.getBlockZ();
			int y = world.getHighestBlockYAt(x, z);

			// Teleport player to the top block at their current X/Z
			Location topLocation = new Location(
					world,
					x + 0.5,
					y + 1,
					z + 0.5,
					loc.getYaw(),
					loc.getPitch()
			);
			player.teleportAsync(topLocation);

			// Send confirmation message
			MsgUtility.send(
					player,
					plugin.getConfig().getString(
							"commands.top.messages.teleported",
							"<green>Successfully teleported to the top!</green>"
					)
			);
			return Command.SINGLE_SUCCESS;
		});

		// Build and return the complete command node
		return topCommand.build();
	}
}