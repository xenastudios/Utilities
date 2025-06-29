package co.xenastudios.utilities.commands;

import co.xenastudios.utilities.UtilitiesPlugin;
import co.xenastudios.utilities.utilities.MsgUtility;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handles the /speed command, allowing players to set their own or others' walk/fly speed.
 */
public class SpeedCommand {

	/**
	 * Creates the /speed command node for registration.
	 *
	 * @param plugin The main plugin instance.
	 * @return The constructed command node for registration.
	 */
	public static LiteralCommandNode<CommandSourceStack> createCommand(final UtilitiesPlugin plugin) {
		// Build the base /speed command
		var speedCommand = Commands.literal("speed");

		// Retrieve permissions from config
		String defaultPermission = plugin.getConfig().getString("commands.speed.permissions.default");
		String othersPermission = plugin.getConfig().getString("commands.speed.permissions.others");

		// Restrict command usage to those with the default permission, if set
		if (defaultPermission != null && !defaultPermission.isEmpty()) {
			speedCommand.requires(sender -> sender.getSender().hasPermission(defaultPermission));
		}

		// /speed <value>: set own speed
		speedCommand.then(
				Commands.argument("value", IntegerArgumentType.integer(1, 10))
						.executes(ctx -> {
							CommandSender sender = ctx.getSource().getSender();
							int value = ctx.getArgument("value", Integer.class);

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

							setSpeed(player, player, value, plugin, false);
							return Command.SINGLE_SUCCESS;
						})
						// /speed <value> <target>: set another player's speed
						.then(
								Commands.argument("target", ArgumentTypes.player())
										.executes(ctx -> {
											CommandSender sender = ctx.getSource().getSender();
											int value = ctx.getArgument("value", Integer.class);
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

											// Check permission for setting others' speed
											if (othersPermission != null && !othersPermission.isEmpty() &&
													!sender.hasPermission(othersPermission)) {
												sender.sendMessage(plugin.getServer().getPermissionMessage());
												return Command.SINGLE_SUCCESS;
											}

											setSpeed(sender, target, value, plugin, true);
											return Command.SINGLE_SUCCESS;
										})
						)
		);

		// Build and return the complete command node
		return speedCommand.build();
	}

	/**
	 * Sets the walk or fly speed for a player and sends appropriate messages.
	 * If the player is flying, sets fly speed; otherwise, sets walk speed.
	 *
	 * @param sender  The command sender.
	 * @param target  The player whose speed is being set.
	 * @param value   The speed value (1-10).
	 * @param plugin  The main plugin instance.
	 * @param isOther True if setting speed for another player, false if self.
	 */
	private static void setSpeed(
			CommandSender sender,
			Player target,
			int value,
			UtilitiesPlugin plugin,
			boolean isOther
	) {
		// Calculate speed value
		float speed = value / 10.0f;
		boolean isFlying = target.isFlying();

		// Set fly speed if flying, otherwise set walk speed
		try {
			if (isFlying) {
				target.setFlySpeed(speed);
			} else {
				target.setWalkSpeed(speed);
			}
		} catch (IllegalArgumentException exception) {
			MsgUtility.send(
					sender,
					plugin.getConfig().getString(
							"commands.speed.messages.error.invalid",
							"<red>Invalid speed! Please enter a value from 1 to 10.</red>"
					)
			);
			return;
		}

		// Prepare messages
		String basePath = "commands.speed.messages.";
		String selfMsg = plugin.getConfig().getString(
				basePath + "set",
				"<green>Your speed has been set to <speed>.</green>"
		);
		String othersMsg = plugin.getConfig().getString(
				basePath + "others-set",
				"<green>You have set <player>'s speed to <speed>.</green>"
		);

		// Send messages to sender and/or target
		if (isOther) {
			MsgUtility.send(
					sender,
					othersMsg,
					Placeholder.unparsed("player", target.getName()),
					Placeholder.unparsed("speed", String.valueOf(value))
			);
			MsgUtility.send(
					target,
					selfMsg,
					Placeholder.unparsed("speed", String.valueOf(value))
			);
		} else {
			MsgUtility.send(
					target,
					selfMsg,
					Placeholder.unparsed("speed", String.valueOf(value))
			);
		}
	}
}