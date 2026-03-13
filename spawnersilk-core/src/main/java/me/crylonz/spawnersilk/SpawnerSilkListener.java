package me.crylonz.spawnersilk;

import me.crylonz.spawnersilk.utils.ArmorStandCleaner;
import me.crylonz.spawnersilk.utils.SpawnerSilkHologram;
import me.crylonz.spawnersilk.utils.SpawnerSilkConfig;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static me.crylonz.spawnersilk.SpawnerSilk.getSpawnerMaterial;
import static me.crylonz.spawnersilk.SpawnerSilk.playersUUID;
import static org.bukkit.Bukkit.getServer;

public class SpawnerSilkListener implements Listener {

    private SpawnerSilk plugin;

    public SpawnerSilkListener(SpawnerSilk plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        playersUUID.put(e.getPlayer().getName(), e.getPlayer().getUniqueId().toString());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        playersUUID.remove(e.getPlayer().getName(), e.getPlayer().getUniqueId().toString());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreakEvent(BlockBreakEvent e) {

        if (!e.isCancelled()) {

            if (e.getBlock().getType() == getSpawnerMaterial() && e.getPlayer().hasPermission("spawnersilk.minespawner")) {
                Player p = e.getPlayer();

                if (!p.getInventory().getItemInMainHand().getEnchantments().containsKey(Enchantment.SILK_TOUCH)
                        && plugin.getDataConfig().getBoolean(SpawnerSilkConfig.NEED_SILK_TOUCH_TO_DESTROY)) {
                    e.setCancelled(true);
                    sendFeedback(p, SpawnerSilkConfig.FEEDBACK_BREAK_ERRORS, "event.break.need_silk_touch");
                }

                boolean canDropWithSilkRule = p.getInventory().getItemInMainHand().getEnchantments().containsKey(Enchantment.SILK_TOUCH)
                        || !plugin.getDataConfig().getBoolean(SpawnerSilkConfig.NEED_SILK_TOUCH);
                boolean canUsePickaxe = canGetSpawner(p);

                if (canDropWithSilkRule && canUsePickaxe) {

                    int randomSpawnerDrop = new Random().nextInt(100);
                    int randomEggDrop = new Random().nextInt(100);

                    dropToPlayer(e,
                            plugin.getDataConfig().getInt(SpawnerSilkConfig.DROP_CHANCE) >= randomSpawnerDrop,
                            plugin.getDataConfig().getInt(SpawnerSilkConfig.DROP_EGG_CHANCE) >= randomEggDrop);
                } else if (canDropWithSilkRule && !canUsePickaxe) {
                    sendFeedback(p, SpawnerSilkConfig.FEEDBACK_BREAK_ERRORS, "event.break.pickaxe_too_weak", pickaxeArgs());
                }
            }
        }
    }

    public void dropToPlayer(BlockBreakEvent e, boolean dropSpawner, boolean dropEgg) {
        CreatureSpawner spawner = (CreatureSpawner) e.getBlock().getState();
        EntityType entity = spawner.getSpawnedType();
        ItemStack spawnerItem = SpawnerAPI.getSpawner(entity);

        if (plugin.getDataConfig()
                .getList(SpawnerSilkConfig.BLACKLIST)
                .stream()
                .anyMatch(bannedEntity -> bannedEntity.toUpperCase().contains(entity.name().toUpperCase()))) {
            sendFeedback(e.getPlayer(), SpawnerSilkConfig.FEEDBACK_BREAK_ERRORS, "event.break.blacklisted", typeArgs(entity));
            return;
        }

        if (!plugin.getDataConfig().getBoolean(SpawnerSilkConfig.SPAWNERS_GENERATE_XP)) {
            e.setExpToDrop(0);
        }

        int dropMode = plugin.getDataConfig().getInt(SpawnerSilkConfig.DROP_MODE);
        boolean dropInCreative = plugin.getDataConfig().getBoolean(SpawnerSilkConfig.DROP_IN_CREATIVE);

        if (e.getPlayer().getGameMode() == GameMode.CREATIVE && !dropInCreative) {
            return;
        }

        if (dropMode == 1) {

            if (plugin.getDataConfig().getBoolean(SpawnerSilkConfig.DROP_TO_INVENTORY) && e.getPlayer().getInventory().firstEmpty() != -1) {

                if (dropSpawner) {
                    e.getPlayer().getInventory().addItem(new ItemStack(Material.SPAWNER));
                }

                if (dropEgg) {
                    e.getPlayer().getInventory().addItem(new ItemStack(Material.valueOf(entity.name().toUpperCase().replace(" ", "_") + "_SPAWN_EGG")));
                }

            } else {
                if (dropSpawner) {
                    e.getPlayer().getWorld().dropItemNaturally(e.getBlock().getLocation(), new ItemStack(Material.SPAWNER));
                }
                if (dropEgg) {
                    e.getPlayer().getWorld().dropItemNaturally(e.getBlock().getLocation(),
                            new ItemStack(Material.valueOf(entity.name().toUpperCase().replace(" ", "_") + "_SPAWN_EGG")));
                }
            }
        }
        // Drop Mode 0
        else {
            if (plugin.getDataConfig().getBoolean(SpawnerSilkConfig.DROP_TO_INVENTORY) && e.getPlayer().getInventory().firstEmpty() != -1) {
                if (dropSpawner) {
                    e.getPlayer().getInventory().addItem(spawnerItem);
                }
            } else {
                if (dropSpawner) {
                    e.getPlayer().getWorld().dropItemNaturally(e.getBlock().getLocation(), spawnerItem);
                }
            }
        }
    }

    public boolean canGetSpawner(Player p) {
        int mode = plugin.getDataConfig().getInt(SpawnerSilkConfig.PICKAXE_MODE);
        boolean valid = mode == 0;
        if (mode <= 1 && !valid) {
            valid = p.getInventory().getItemInMainHand().getType() == Material.WOODEN_PICKAXE;
        }
        if (mode <= 2 && !valid) {
            valid = p.getInventory().getItemInMainHand().getType() == Material.STONE_PICKAXE;
        }
        if (mode <= 3 && !valid) {
            valid = p.getInventory().getItemInMainHand().getType() == Material.IRON_PICKAXE;
        }
        if (mode <= 4 && !valid) {
            valid = p.getInventory().getItemInMainHand().getType() == Material.GOLDEN_PICKAXE;
        }
        if (mode <= 5 && !valid) {
            valid = p.getInventory().getItemInMainHand().getType() == Material.DIAMOND_PICKAXE;
        }
        if (mode <= 6 && !valid) {
            valid = p.getInventory().getItemInMainHand().getType() == Material.NETHERITE_PICKAXE;
        }
        return valid;
    }

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent e) {

        if (e.getBlockPlaced().getType() == getSpawnerMaterial()) {
            CreatureSpawner cs = (CreatureSpawner) e.getBlockPlaced().getState();
            EntityType entityType = SpawnerAPI.getEntityType(e.getItemInHand());

            if (entityType != EntityType.UNKNOWN) {
                cs.setSpawnedType(SpawnerAPI.getEntityType(e.getItemInHand()));
                sendFeedback(e.getPlayer(), SpawnerSilkConfig.FEEDBACK_PLACE_SUCCESS, "event.place.success", typeArgs(entityType));
            } else {
                cs.setSpawnedType(EntityType.PIG);
                sendFeedback(e.getPlayer(), SpawnerSilkConfig.FEEDBACK_PLACE_SUCCESS, "event.place.unknown_type_fallback", typeArgs(EntityType.PIG));
            }
            cs.update();
        }
    }

    @EventHandler
    public void playerRenameItem(InventoryClickEvent event) {
        if (event.getInventory().getType().equals(InventoryType.ANVIL)) {
            if (event.getCurrentItem() != null && event.getCurrentItem().getType() == getSpawnerMaterial()) {
                event.getWhoClicked().sendMessage(plugin.getLocalization().getMessage("listener.anvil.spawner_denied"));
                event.setResult(Event.Result.DENY);
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK || e.getClickedBlock() == null || e.getClickedBlock().getType() != getSpawnerMaterial()) {
            return;
        }

        if (!plugin.getDataConfig().getBoolean(SpawnerSilkConfig.SPAWNERS_CAN_BE_MODIFIED_BY_EGG)) {
            e.setCancelled(true);
            sendFeedback(e.getPlayer(), SpawnerSilkConfig.FEEDBACK_INTERACT_ERRORS, "event.interact.modification_disabled");
            return;
        }

        if (e.getItem() == null || !e.getItem().getType().name().toUpperCase().contains("EGG")) {
            return;
        }

        e.setCancelled(true);

        CreatureSpawner cs = (CreatureSpawner) e.getClickedBlock().getState();
        EntityType newType = EntityType.valueOf(e.getItem().getType().name().replace("_SPAWN_EGG", ""));
        cs.setSpawnedType(newType);
        cs.update();

        if (plugin.getDataConfig().getBoolean(SpawnerSilkConfig.USE_EGG)
                && e.getPlayer().getGameMode() != GameMode.CREATIVE
                && e.getHand() == EquipmentSlot.HAND) {
            ItemStack item = e.getItem();
            item.setAmount(item.getAmount() - 1);
        }

        sendFeedback(e.getPlayer(), SpawnerSilkConfig.FEEDBACK_INTERACT_SUCCESS, "event.interact.changed", typeArgs(newType));
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (plugin.getDataConfig().getBoolean(SpawnerSilkConfig.SPAWNER_OVERLAY) && e.getPlayer().hasPermission("spawnersilk.overlay")) {
            Block block = e.getPlayer().getTargetBlockExact(10);

            // Player look at a spawner
            if (block != null && block.getType() == Material.SPAWNER) {
                CreatureSpawner cs = (CreatureSpawner) block.getState();

                // if holo is now already display
                if (e.getPlayer().getWorld()
                        .getNearbyEntities(block.getLocation(), 1, 3, 1)
                        .stream().noneMatch(entity -> entity.getType() == EntityType.ARMOR_STAND)) {

                    ArrayList<ArmorStand> armorStands = new ArrayList<>();

                    cs.getChunk().setForceLoaded(true);

                    armorStands.add(SpawnerSilkHologram.generateHologram(cs.getLocation(), plugin.getLocalization().getMessage("overlay.title", entityArgs(cs.getSpawnedType())), 0.5f, 0.40f, 0.5f, this.plugin, e.getPlayer().getUniqueId()));
                    armorStands.add(SpawnerSilkHologram.generateHologram(cs.getLocation(), plugin.getLocalization().getMessage("overlay.spawn_count", valueArgs(cs.getSpawnCount())), 0.5f, 0.05f, 0.5f, this.plugin, e.getPlayer().getUniqueId()));
                    armorStands.add(SpawnerSilkHologram.generateHologram(cs.getLocation(), plugin.getLocalization().getMessage("overlay.spawn_range", valueArgs(cs.getSpawnRange())), 0.5f, -0.20f, 0.5f, this.plugin, e.getPlayer().getUniqueId()));
                    armorStands.add(SpawnerSilkHologram.generateHologram(cs.getLocation(), plugin.getLocalization().getMessage("overlay.max_entities", valueArgs(cs.getMaxNearbyEntities())), 0.5f, -0.45f, 0.5f, this.plugin, e.getPlayer().getUniqueId()));
                    armorStands.add(SpawnerSilkHologram.generateHologram(cs.getLocation(), plugin.getLocalization().getMessage("overlay.player_range", valueArgs(cs.getRequiredPlayerRange())), 0.5f, -0.70f, 0.5f, this.plugin, e.getPlayer().getUniqueId()));
                    armorStands.add(SpawnerSilkHologram.generateHologram(cs.getLocation(), plugin.getLocalization().getMessage("overlay.max_spawn_delay", valueArgs(cs.getMaxSpawnDelay())), 0.5f, -0.95f, 0.5f, this.plugin, e.getPlayer().getUniqueId()));
                    armorStands.add(SpawnerSilkHologram.generateHologram(cs.getLocation(), plugin.getLocalization().getMessage("overlay.min_spawn_delay", valueArgs(cs.getMinSpawnDelay())), 0.5f, -1.2f, 0.5f, this.plugin, e.getPlayer().getUniqueId()));

                    getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new ArmorStandCleaner(armorStands), 20L * plugin.getDataConfig().getInt(SpawnerSilkConfig.SPAWNER_OVERLAY_DELAY));

                }
            }
        }
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent e) {
        Random r = new Random();
        int randomInt = r.nextInt(100);
        if (e.blockList().size() > 0) {
            for (int i = 0; i < e.blockList().size(); i++) {
                Block block = e.blockList().get(i);
                if (block.getType() == getSpawnerMaterial() && randomInt <= plugin.getDataConfig().getInt(SpawnerSilkConfig.EXPLOSION_DROP_CHANCE)) {
                    CreatureSpawner s = (CreatureSpawner) block.getState();
                    block.getWorld().dropItemNaturally(block.getLocation(), SpawnerAPI.getSpawner(s.getSpawnedType()));
                }
            }
        }
    }

    private Map<String, String> valueArgs(int value) {
        return Collections.singletonMap("value", String.valueOf(value));
    }

    private Map<String, String> entityArgs(EntityType entityType) {
        Map<String, String> replacements = new HashMap<>();
        replacements.put("entity", entityType.name());
        return replacements;
    }

    private Map<String, String> typeArgs(EntityType entityType) {
        Map<String, String> replacements = new HashMap<>();
        replacements.put("type", entityType.name());
        return replacements;
    }

    private Map<String, String> pickaxeArgs() {
        Map<String, String> replacements = new HashMap<>();
        replacements.put("required_pickaxe", requiredPickaxeName(plugin.getDataConfig().getInt(SpawnerSilkConfig.PICKAXE_MODE)));
        return replacements;
    }

    private String requiredPickaxeName(int mode) {
        switch (mode) {
            case 1:
                return "WOODEN_PICKAXE";
            case 2:
                return "STONE_PICKAXE";
            case 3:
                return "IRON_PICKAXE";
            case 4:
                return "GOLDEN_PICKAXE";
            case 5:
                return "DIAMOND_PICKAXE";
            case 6:
                return "NETHERITE_PICKAXE";
            default:
                return "PICKAXE";
        }
    }

    private void sendFeedback(Player player, String configKey, String messageKey) {
        sendFeedback(player, configKey, messageKey, Collections.emptyMap());
    }

    private void sendFeedback(Player player, String configKey, String messageKey, Map<String, String> replacements) {
        if (player != null && plugin.getDataConfig().getBoolean(configKey)) {
            player.sendMessage(plugin.getLocalization().getMessage(messageKey, replacements));
        }
    }
}
