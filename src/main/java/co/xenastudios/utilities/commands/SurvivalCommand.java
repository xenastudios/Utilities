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
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handles the /survival command, allowing players to set their own or others' gamemode to Survival.
 */
public class SurvivalCommand {

	/**
	 * Registers the /survival command for the plugin.
	 * Supports both self and target player usage.
	 *
	 * @param plugin The main plugin instance.
	 * @return The constructed command node for registration.
	 */
	public static LiteralCommandNode<CommandSourceStack> createCommand(final UtilitiesPlugin plugin) {
		// Create the base /survival command
		var survivalCommand = Commands.literal("survival");

		// Retrieve permissions from config
		String defaultPermission = plugin.getConfig().getString("commands.survival.permissions.default");
		String othersPermission = plugin.getConfig().getString("commands.survival.permissions.others");

		// Restrict command usage to those with the default permission, if set
		if (defaultPermission != null && !defaultPermission.isEmpty()) {
			survivalCommand.requires(sender -> sender.getSender().hasPermission(defaultPermission));
		}

		// /survival: set own gamemode to Survival
		survivalCommand.executes(ctx -> {
			CommandSender sender = ctx.getSource().getSender();
			if (!(sender instanceof Player player)) {
				// Error: Only players can use this command
				MsgUtility.send(
						sender,
						plugin.getConfig().getString(
								"messages.error.player-only",
								"<red>Only a player can execute this command!</red>"
						)
				);
				return Command.SINGLE_SUCCESS;
			}

			// Set the player's own gamemode to Survival
			setSurvival(player, player, plugin, false);
			return Command.SINGLE_SUCCESS;
		});

		// /survival <target>: set another player's gamemode to Survival
		survivalCommand.then(
				Commands.argument("target", ArgumentTypes.player())
						.executes(ctx -> {
							CommandSender sender = ctx.getSource().getSender();

							// Resolve the target player using the argument resolver
							PlayerSelectorArgumentResolver targetResolver =
									ctx.getArgument("target", PlayerSelectorArgumentResolver.class);
							Player target = targetResolver.resolve(ctx.getSource()).getFirst();

							// Error: Target player not found
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

							// Check permission for setting others' gamemode
							if (othersPermission != null && !othersPermission.isEmpty() &&
									!sender.hasPermission(othersPermission)) {
								sender.sendMessage(plugin.getServer().getPermissionMessage());
								return Command.SINGLE_SUCCESS;
							}

							// Set the target player's gamemode to Survival
							setSurvival(sender, target, plugin, true);
							return Command.SINGLE_SUCCESS;
						})
		);

		// Build and return the complete command node
		return survivalCommand.build();
	}

	/**
	 * Sets the target player's gamemode to Survival and sends appropriate messages.
	 *
	 * @param sender  The command sender (could be self or another player).
	 * @param target  The player whose gamemode is being set.
	 * @param plugin  The main plugin instance.
	 * @param isOther True if setting gamemode for another player, false if self.
	 */
	private static void setSurvival(
			CommandSender sender,
			Player target,
			UtilitiesPlugin plugin,
			boolean isOther
	) {
		// Set the player's gamemode to Survival
		target.setGameMode(GameMode.SURVIVAL);

		// Prepare message paths and defaults
		String basePath = "commands.survival.messages.";
		String selfMsg = plugin.getConfig().getString(
				basePath + "set",
				"<green>Your gamemode has been set to Survival.</green>"
		);
		String othersMsg = plugin.getConfig().getString(
				basePath + "others-set",
				"<green>Set <player>'s gamemode to Survival.</green>"
		);

		// Send messages to the appropriate players
		if (isOther) {
			MsgUtility.send(
					sender,
					othersMsg,
					Placeholder.unparsed("player", target.getName())
			);
			MsgUtility.send(
					target,
					selfMsg
			);
		} else {
			MsgUtility.send(
					target,
					selfMsg
			);
		}
	}
}