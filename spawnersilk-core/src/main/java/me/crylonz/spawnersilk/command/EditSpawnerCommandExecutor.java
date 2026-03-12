package me.crylonz.spawnersilk.command;

import me.crylonz.spawnersilk.SpawnerSilk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public class EditSpawnerCommandExecutor implements CommandExecutor {

	private final SpawnerSilk plugin;

	public EditSpawnerCommandExecutor(SpawnerSilk plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (sender instanceof Player) {
			Player player = (Player) sender;

			if (args.length >= 1 && args.length <= 2) {
				if (cmd.getName().equalsIgnoreCase("editspawner")) {
					return this.runCommand(player, args);
				}
			}
		}
		return false;
	}

	public boolean runCommand(Player player, String[] args) {
		Block block = player.getTargetBlockExact(10);
		if (!player.hasPermission("spawnersilk.editspawner")) {
			player.sendMessage(plugin.getLocalization().getMessage("command.editspawner.no_permission", Collections.emptyMap()));
			return false;
		}
		if (args == null) return false;

		if (block != null && block.getType() == Material.SPAWNER) {
			CreatureSpawner cs = (CreatureSpawner) block.getState();

			/// SPAWNRANGE
			if (args[0].equalsIgnoreCase("spawnrange")) {
				if (args.length == 2) {
					cs.setSpawnRange(Integer.parseInt(args[1]));
					cs.update();
					block.setBlockData(cs.getBlockData());
				}
				sendValueMessage(player, "command.editspawner.spawnrange", cs.getSpawnRange());
				return true;
			}

			/// SPAWNCOUNT
			else if (args[0].equalsIgnoreCase("spawncount")) {
				if (args.length == 2) {
					cs.setSpawnCount(Integer.parseInt(args[1]));
					cs.update();
					block.setBlockData(cs.getBlockData());

				}
				sendValueMessage(player, "command.editspawner.spawncount", cs.getSpawnCount());
				return true;
			}

			/// MaxNearbyEntities
			else if (args[0].equalsIgnoreCase("MaxNearbyEntities")) {
				if (args.length == 2) {
					cs.setMaxNearbyEntities(Integer.parseInt(args[1]));
					cs.update();
					block.setBlockData(cs.getBlockData());
				}

				sendValueMessage(player, "command.editspawner.max_nearby_entities", cs.getMaxNearbyEntities());
				return true;
			}

			/// RequiredPlayerRange
			else if (args[0].equalsIgnoreCase("RequiredPlayerRange")) {
				if (args.length == 2) {
					cs.setRequiredPlayerRange(Integer.parseInt(args[1]));
					cs.update();
					block.setBlockData(cs.getBlockData());
				}

				sendValueMessage(player, "command.editspawner.required_player_range", cs.getRequiredPlayerRange());
				return true;
			}

			/// Delay
			else if (args[0].equalsIgnoreCase("Delay")) {
				if (args.length == 2) {
					cs.setDelay(Integer.parseInt(args[1]));
					cs.update();
					block.setBlockData(cs.getBlockData());
				}

				sendValueMessage(player, "command.editspawner.delay", cs.getDelay());
				return true;
			}

			/// MaxSpawnDelay
			else if (args[0].equalsIgnoreCase("MaxSpawnDelay")) {
				if (args.length == 2) {
					cs.setMaxSpawnDelay(Integer.parseInt(args[1]));
					cs.update();
					block.setBlockData(cs.getBlockData());
				}

				sendValueMessage(player, "command.editspawner.max_spawn_delay", cs.getMaxSpawnDelay());
				return true;
			}
			/// MinSpawnDelay
			else if (args[0].equalsIgnoreCase("MinSpawnDelay")) {
				if (args.length == 2) {
					cs.setMinSpawnDelay(Integer.parseInt(args[1]));
					cs.update();
					block.setBlockData(cs.getBlockData());
				}

				sendValueMessage(player, "command.editspawner.min_spawn_delay", cs.getMinSpawnDelay());
				return true;
			}

		} else {
			player.sendMessage(plugin.getLocalization().getMessage("command.editspawner.must_target_spawner"));
			return true;
		}
		return false;
	}

	private void sendValueMessage(Player player, String key, int value) {
		player.sendMessage(plugin.getLocalization().getMessage(key, Collections.singletonMap("value", String.valueOf(value))));
	}

}
