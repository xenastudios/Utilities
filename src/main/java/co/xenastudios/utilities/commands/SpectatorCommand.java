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
 * Handles the /spectator command, allowing players to set their own or others' gamemode to Spectator.
 */
public class SpectatorCommand {

	/**
	 * Registers the /spectator command for the plugin.
	 *
	 * @param plugin The main plugin instance.
	 * @return The constructed command node for registration.
	 */
	public static LiteralCommandNode<CommandSourceStack> createCommand(final UtilitiesPlugin plugin) {
		var spectatorCommand = Commands.literal("spectator");

		// Permissions from config
		String defaultPermission = plugin.getConfig().getString("commands.spectator.permissions.default");
		String othersPermission = plugin.getConfig().getString("commands.spectator.permissions.others");

		if (defaultPermission != null && !defaultPermission.isEmpty()) {
			spectatorCommand.requires(sender -> sender.getSender().hasPermission(defaultPermission));
		}

		// /spectator: set own gamemode
		spectatorCommand.executes(ctx -> {
			CommandSender sender = ctx.getSource().getSender();
			if (!(sender instanceof Player player)) {
				MsgUtility.send(
						sender,
						plugin.getConfig().getString(
								"messages.error.player-only",
								"<red>Only a player can execute this command!</red>"
						)
				);
				return Command.SINGLE_SUCCESS;
			}
			setSpectator(player, player, plugin, false);
			return Command.SINGLE_SUCCESS;
		});

		// /spectator <target>: set another player's gamemode
		spectatorCommand.then(
				Commands.argument("target", ArgumentTypes.player())
						.executes(ctx -> {
							CommandSender sender = ctx.getSource().getSender();
							PlayerSelectorArgumentResolver targetResolver =
									ctx.getArgument("target", PlayerSelectorArgumentResolver.class);
							Player target = targetResolver.resolve(ctx.getSource()).getFirst();

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

							if (othersPermission != null && !othersPermission.isEmpty() &&
									!sender.hasPermission(othersPermission)) {
								sender.sendMessage(plugin.getServer().getPermissionMessage());
								return Command.SINGLE_SUCCESS;
							}

							setSpectator(sender, target, plugin, true);
							return Command.SINGLE_SUCCESS;
						})
		);

		return spectatorCommand.build();
	}

	/**
	 * Sets the target player's gamemode to Spectator and sends appropriate messages.
	 */
	private static void setSpectator(
			CommandSender sender,
			Player target,
			UtilitiesPlugin plugin,
			boolean isOther
	) {
		target.setGameMode(GameMode.SPECTATOR);

		String basePath = "commands.spectator.messages.";
		String selfMsg = plugin.getConfig().getString(
				basePath + "set",
				"<green>Your gamemode has been set to Spectator.</green>"
		);
		String othersMsg = plugin.getConfig().getString(
				basePath + "others-set",
				"<green>Set <player>'s gamemode to Spectator.</green>"
		);

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