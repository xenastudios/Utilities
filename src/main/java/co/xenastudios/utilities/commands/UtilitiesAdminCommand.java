package co.xenastudios.utilities.commands;

import co.xenastudios.utilities.utilities.MsgUtility;
import co.xenastudios.utilities.UtilitiesPlugin;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;

/**
 * Handles the /utilitiesadmin command for administrative actions on the Utility plugin.
 */
public class UtilitiesAdminCommand {

	/**
	 * Creates the /utilitiesadmin command node for registration.
	 *
	 * @param plugin The main plugin instance.
	 * @return The constructed command node for registration.
	 */
	public static LiteralCommandNode<CommandSourceStack> createCommand(final UtilitiesPlugin plugin) {
		// Build the base /utilitiesadmin command
		LiteralArgumentBuilder<CommandSourceStack> utilitiesAdminCommand =
				Commands.literal("utilitiesadmin");

		// Retrieve the required permission from the config
		String permission = plugin.getConfig().getString("commands.utilitiesadmin.permission");

		// Restrict command usage to those with the permission, if set
		if (permission != null && !permission.isEmpty()) {
			utilitiesAdminCommand.requires(sender -> sender.getSender().hasPermission(permission));
		}

		// Main /utilitiesadmin command execution: show usage message
		utilitiesAdminCommand.executes(ctx -> {
			CommandSender sender = ctx.getSource().getSender();
			String usageMsg = plugin.getConfig().getString(
					"messages.error.usage",
					"<red>Usage: <usage></red>"
			);
			MsgUtility.send(
					sender,
					usageMsg,
					Placeholder.unparsed("usage", "/utilitiesadmin reload")
			);
			return Command.SINGLE_SUCCESS;
		});

		// /utilitiesadmin reload subcommand: reloads the plugin config
		utilitiesAdminCommand.then(
				Commands.literal("reload")
						.executes(ctx -> {
							CommandSender sender = ctx.getSource().getSender();
							plugin.reloadConfig();
							String reloadMsg = plugin.getConfig().getString(
									"commands.utilitiesadmin.messages.config-reloaded",
									"<green>Utilities's config has been reloaded.</green>"
							);
							MsgUtility.send(sender, reloadMsg);
							return Command.SINGLE_SUCCESS;
						})
		);

		// Build and return the complete command node
		return utilitiesAdminCommand.build();
	}
}