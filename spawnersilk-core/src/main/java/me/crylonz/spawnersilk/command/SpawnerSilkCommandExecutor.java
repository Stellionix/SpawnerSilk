package me.crylonz.spawnersilk.command;

import me.crylonz.spawnersilk.SpawnerSilk;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class SpawnerSilkCommandExecutor implements CommandExecutor {

	private final SpawnerSilk plugin;
	private final GiveSpawnerCommandExecutor giveSpawnerCommandExecutor;
	private final EditSpawnerCommandExecutor editSpawnerCommandExecutor;

	public SpawnerSilkCommandExecutor(SpawnerSilk plugin, final GiveSpawnerCommandExecutor giveSpawnerCommandExecutor, final EditSpawnerCommandExecutor editSpawnerCommandExecutor) {
		this.plugin = plugin;
		this.giveSpawnerCommandExecutor = giveSpawnerCommandExecutor;
		this.editSpawnerCommandExecutor = editSpawnerCommandExecutor;
	}

	private final String UNKNOWN_COMMAND = "Unknown command";
	private final String PLUGIN_RELOAD = "Plugin reloaded successfully !";

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
							displayMessage(player, PLUGIN_RELOAD, false);
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
						displayMessage(player, UNKNOWN_COMMAND, true);
						break;
				}
			} else {
				displayMessage(player, UNKNOWN_COMMAND, true);
			}
		}
		return true;
	}

	private void reloadPlugin() {
		plugin.reloadConfig();
		plugin.loadPluginConfig();
	}

	private void displayMessage(Player player, String message, boolean isError) {
		String prefixMessage;

		if (isError) {
			prefixMessage = ChatColor.RED.toString();
		} else {
			prefixMessage = ChatColor.GREEN.toString();
		}


		if (player == null) {
			SpawnerSilk.log.info("[SpawnerSilk] " + message);
		} else {
			player.sendMessage(prefixMessage + "[SpawnerSilk] " + message);
		}
	}

	private String[] reformatArgs(String[] args, String notInclude) {
		return Arrays.stream(args).filter(arg -> !arg.equalsIgnoreCase(notInclude)).toArray(String[]::new);
	}
}

