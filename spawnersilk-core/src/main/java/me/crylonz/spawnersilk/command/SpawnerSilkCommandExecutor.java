package me.crylonz.spawnersilk.command;

import me.crylonz.spawnersilk.SpawnerSilk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;

public class SpawnerSilkCommandExecutor implements CommandExecutor {

	private final SpawnerSilk plugin;
	private final GiveSpawnerCommandExecutor giveSpawnerCommandExecutor;
	private final EditSpawnerCommandExecutor editSpawnerCommandExecutor;

	public SpawnerSilkCommandExecutor(SpawnerSilk plugin, final GiveSpawnerCommandExecutor giveSpawnerCommandExecutor, final EditSpawnerCommandExecutor editSpawnerCommandExecutor) {
		this.plugin = plugin;
		this.giveSpawnerCommandExecutor = giveSpawnerCommandExecutor;
		this.editSpawnerCommandExecutor = editSpawnerCommandExecutor;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("sps")) {

			Player player = null;
			if ((sender instanceof Player))
				player = (Player) sender;


			if (args.length >= 1) {
				switch (args[0].toLowerCase()) {
					case "reload":
						if (player == null || player.hasPermission("spawnerSilk.reload")) {
							reloadPlugin();
							displayMessage(player, "command.sps.reload.success");
						} else {
							displayMessage(player, "command.sps.reload.no_permission");
						}
						break;
					case "editspawner":
						if (player != null) {
							return editSpawnerCommandExecutor.runCommand(player, reformatArgs(args, "editspawner"));
						}
					case "givespawner":
						if (player != null) {
							return giveSpawnerCommandExecutor.runCommand(player, reformatArgs(args, "givespawner"));
						}
					default:
						displayMessage(player, "command.sps.unknown");
						break;
				}
			} else {
				displayMessage(player, "command.sps.unknown");
			}
		}
		return true;
	}

	private void reloadPlugin() {
		plugin.reloadConfig();
		plugin.loadPluginConfig();
	}

	private void displayMessage(Player player, String messageKey) {
		String message = plugin.getLocalization().getMessage(messageKey, Collections.emptyMap());
		if (player == null) {
			SpawnerSilk.log.info(org.bukkit.ChatColor.stripColor(message));
		} else {
			player.sendMessage(message);
		}
	}

	private String[] reformatArgs(String[] args, String notInclude) {
		return Arrays.stream(args).filter(arg -> !arg.equalsIgnoreCase(notInclude)).toArray(String[]::new);
	}
}

