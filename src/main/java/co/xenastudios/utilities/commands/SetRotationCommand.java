package co.xenastudios.utilities.commands;

import co.xenastudios.utilities.UtilitiesPlugin;
import co.xenastudios.utilities.utilities.MsgUtility;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handles the /setrotation command, allowing players to set their own or others' yaw and pitch.
 */
public class SetRotationCommand {

	/**
	 * Creates the /setrotation command node for registration.
	 *
	 * @param plugin The main plugin instance.
	 * @return The constructed command node for registration.
	 */
	public static LiteralCommandNode<CommandSourceStack> createCommand(final UtilitiesPlugin plugin) {
		var setRotationCommand = Commands.literal("setrotation");

		// Permissions from config
		String defaultPermission = plugin.getConfig().getString("commands.setrotation.permissions.default");
		String othersPermission = plugin.getConfig().getString("commands.setrotation.permissions.others");

		// Restrict command usage to those with the default permission, if set
		if (defaultPermission != null && !defaultPermission.isEmpty()) {
			setRotationCommand.requires(sender -> sender.getSender().hasPermission(defaultPermission));
		}

		// /setrotation <yaw> <pitch>
		setRotationCommand
				.then(Commands.argument("yaw", FloatArgumentType.floatArg(-180f, 180f))
						.then(Commands.argument("pitch", FloatArgumentType.floatArg(-90f, 90f))
								.executes(ctx -> {
									CommandSender sender = ctx.getSource().getSender();
									float yaw = ctx.getArgument("yaw", Float.class);
									float pitch = ctx.getArgument("pitch", Float.class);

									if (!(sender instanceof Player player)) {
										MsgUtility.send(sender, "<red>Only a player can execute this command!</red>");
										return Command.SINGLE_SUCCESS;
									}

									setRotation(sender, player, yaw, pitch, plugin, false);
									return Command.SINGLE_SUCCESS;
								})
								// /setrotation <yaw> <pitch> <target>
								.then(Commands.argument("target", ArgumentTypes.player())
										.executes(ctx -> {
											CommandSender sender = ctx.getSource().getSender();
											float yaw = ctx.getArgument("yaw", Float.class);
											float pitch = ctx.getArgument("pitch", Float.class);

											PlayerSelectorArgumentResolver targetResolver =
													ctx.getArgument("target", PlayerSelectorArgumentResolver.class);
											Player target = targetResolver.resolve(ctx.getSource()).getFirst();

											if (target == null) {
												MsgUtility.send(sender, "<red>That player doesn't exist!</red>");
												return Command.SINGLE_SUCCESS;
											}

											// Check permission for setting others' rotation
											if (othersPermission != null && !othersPermission.isEmpty() &&
													!sender.hasPermission(othersPermission)) {
												sender.sendMessage(plugin.getServer().getPermissionMessage());
												return Command.SINGLE_SUCCESS;
											}

											setRotation(sender, target, yaw, pitch, plugin, true);
											return Command.SINGLE_SUCCESS;
										})
								)));

		return setRotationCommand.build();
	}

	/**
	 * Sets the target player's yaw and pitch and sends appropriate messages.
	 */
	private static void setRotation(
			CommandSender sender,
			Player target,
			float yaw,
			float pitch,
			UtilitiesPlugin plugin,
			boolean isOther
	) {
		// Clamp values just in case
		yaw = Math.max(-180f, Math.min(180f, yaw));
		pitch = Math.max(-90f, Math.min(90f, pitch));

		Location loc = target.getLocation();
		loc.setYaw(yaw);
		loc.setPitch(pitch);
		target.teleportAsync(loc);

		String basePath = "commands.setrotation.messages.";
		String selfMsg = plugin.getConfig().getString(
				basePath + "self",
				"<green>Your rotation has been set to yaw=<yaw>, pitch=<pitch>.</green>"
		);
		String othersMsg = plugin.getConfig().getString(
				basePath + "others",
				"<green>Set <player>'s rotation to yaw=<yaw>, pitch=<pitch>.</green>"
		);

		if (isOther) {
			MsgUtility.send(
					sender,
					othersMsg,
					Placeholder.unparsed("player", target.getName()),
					Placeholder.unparsed("yaw", String.valueOf(yaw)),
					Placeholder.unparsed("pitch", String.valueOf(pitch))
			);
			MsgUtility.send(
					target,
					selfMsg,
					Placeholder.unparsed("yaw", String.valueOf(yaw)),
					Placeholder.unparsed("pitch", String.valueOf(pitch))
			);
		} else {
			MsgUtility.send(
					target,
					selfMsg,
					Placeholder.unparsed("yaw", String.valueOf(yaw)),
					Placeholder.unparsed("pitch", String.valueOf(pitch))
			);
		}
	}
}