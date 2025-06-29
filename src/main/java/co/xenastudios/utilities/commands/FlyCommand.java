package co.xenastudios.utilities.commands;

import co.xenastudios.utilities.UtilitiesPlugin;
import co.xenastudios.utilities.utilities.MsgUtility;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handles the /fly command, allowing players to toggle flight for themselves or others.
 */
public class FlyCommand {

	/**
	 * Creates the /fly command node for registration.
	 *
	 * @param plugin The main plugin instance.
	 * @return The constructed command node for registration.
	 */
	public static LiteralCommandNode<CommandSourceStack> createCommand(final UtilitiesPlugin plugin) {
		// Build the base /fly command
		var flyCommand = Commands.literal("fly");

		// Retrieve the required permissions from the config
		String defaultPermission = plugin.getConfig().getString("commands.fly.permissions.default");
		String othersPermission = plugin.getConfig().getString("commands.fly.permissions.others");

		// Restrict command usage to those with the default permission, if set
		if (defaultPermission != null && !defaultPermission.isEmpty()) {
			flyCommand.requires(sender -> sender.getSender().hasPermission(defaultPermission));
		}

		// /fly: toggles flight for self
		flyCommand.executes(ctx -> {
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

			// Toggle flight for the player
			toggleFlight(player, player, plugin, false);
			return Command.SINGLE_SUCCESS;
		});

		// /fly <target>: toggles flight for another player
		flyCommand.then(
				Commands.argument("target", ArgumentTypes.player())
						.executes(ctx -> {
							CommandSender sender = ctx.getSource().getSender();

							// Resolve the target player using the argument resolver
							PlayerSelectorArgumentResolver targetResolver =
									ctx.getArgument("target", PlayerSelectorArgumentResolver.class);
							Player target = targetResolver.resolve(ctx.getSource()).getFirst();

							// If the target is not found, send an error message
							if (target == null) {
								MsgUtility.send(
										sender,
										plugin.getConfig().getString(
												"messages.error.no-player",
												"<red>That player doesn't exist!</red>"
										)
								);
								return Command.SINGLE_SUCCESS;
							}

							// Check permission for toggling others' flight
							if (othersPermission != null && !othersPermission.isEmpty() &&
									!sender.hasPermission(othersPermission)) {
								sender.sendMessage(plugin.getServer().getPermissionMessage());
								return Command.SINGLE_SUCCESS;
							}

							// Toggle flight for the target player
							toggleFlight(sender, target, plugin, true);
							return Command.SINGLE_SUCCESS;
						})
		);

		// Build and return the complete command node
		return flyCommand.build();
	}

	/**
	 * Toggles flight for the target player and sends appropriate messages.
	 *
	 * @param sender  The command sender.
	 * @param target  The player whose flight is being toggled.
	 * @param plugin  The main plugin instance.
	 * @param isOther True if toggling for another player, false if self.
	 */
	private static void toggleFlight(
			CommandSender sender,
			Player target,
			UtilitiesPlugin plugin,
			boolean isOther
	) {
		// Toggle the player's flight state
		boolean newState = !target.getAllowFlight();
		target.setAllowFlight(newState);
		target.setFlying(newState);

		// Prepare message paths
		String basePath = "commands.fly.messages.";
		String selfMsg = plugin.getConfig().getString(
				basePath + (newState ? "enabled" : "disabled"),
				newState
						? "<green>Flight has been enabled for you.</green>"
						: "<red>Flight has been disabled for you.</red>"
		);
		String othersMsg = plugin.getConfig().getString(
				basePath + (newState ? "others-enabled" : "others-disabled"),
				newState
						? "<green>Flight has been enabled for <player>.</green>"
						: "<red>Flight has been disabled for <player>.</red>"
		);

		// Send messages to the appropriate players
		if (isOther) {
			MsgUtility.send(
					sender,
					othersMsg,
					Placeholder.unparsed("player", target.getName())
			);
			MsgUtility.send(target, selfMsg);
		} else {
			MsgUtility.send(target, selfMsg);
		}
	}
}