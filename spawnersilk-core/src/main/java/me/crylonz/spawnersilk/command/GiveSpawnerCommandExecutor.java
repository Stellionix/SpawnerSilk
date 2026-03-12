package me.crylonz.spawnersilk.command;

import me.crylonz.spawnersilk.SpawnerAPI;
import me.crylonz.spawnersilk.SpawnerSilk;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveSpawnerCommandExecutor implements CommandExecutor {

	private final SpawnerSilk plugin;

	public GiveSpawnerCommandExecutor(SpawnerSilk plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// if command is givespawner
		if (cmd.getName().equalsIgnoreCase("givespawner")) {
			return this.runCommand(sender, args);
		}
		return true;
	}

	private String getUuid(String name) {

		final String[] uuid = new String[1];
		uuid[0] = "error";
		SpawnerSilk.playersUUID.forEach((k, v) -> {
			if (k.equals(name)) {
				uuid[0] = v;
			}
		});
		return uuid[0];
	}

	public boolean runCommand(CommandSender sender, String[] args) {
		Player player = null;
		if ((sender instanceof Player))
			player = (Player) sender;
		String invalidFormatKey = "command.givespawner.usage.direct";
		if (args == null) {
			sendMessage(player, "command.givespawner.usage.sps");
			return true;
		}
		if ((player != null && player.hasPermission("spawnersilk.givespawner")) || (!(sender instanceof Player))) {
			if (args.length >= 2 && args.length < 4) {
				Player destinator = null;

				// get destinator of the command
				for (Player p : Bukkit.getOnlinePlayers()) {
					if (p.getUniqueId().toString().equalsIgnoreCase(getUuid(args[0]))) {
						destinator = p;
						break;
					}
				}

				if (destinator != null) {
					int amount = 1;
					if (args.length >= 3) {
						if (args[2].matches("-?(0|[1-9]\\d*)"))
							amount = Integer.parseInt(args[2]);
						else {
							sendMessage(player, invalidFormatKey);
							return true;
						}
					}

					ItemStack is = SpawnerAPI.stringToCustomItemStack(args[1], amount);

					// if the item is valid
					if (is.getItemMeta() != null && SpawnerAPI.getEntityType(is) != EntityType.UNKNOWN) {
						destinator.getInventory().addItem(is);
						sendMessage(player, "command.givespawner.success");
					}
					// item not valid
					else {
						sendMessage(player, "command.givespawner.invalid_type");
					}

					// destinator not valid
				} else {
					sendMessage(player, "command.givespawner.invalid_player");
				}

				// not good argument structure
			} else if (player != null)
				sendMessage(player, invalidFormatKey);
			else
				sendMessage(null, invalidFormatKey);
		}
		return true;
	}

	private void sendMessage(Player player, String key) {
		String message = plugin.getLocalization().getMessage(key);
		if (player != null) {
			player.sendMessage(message);
		} else {
			SpawnerSilk.log.info(ChatColor.stripColor(message));
		}
	}


}
