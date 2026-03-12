package me.crylonz.spawnersilk;

import me.crylonz.spawnersilk.utils.SpawnerSilkConfig;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SpawnerSilkListenerTest {

    @Test
    void canGetSpawnerAcceptsConfiguredPickaxeTier() {
        SpawnerSilk plugin = mock(SpawnerSilk.class);
        SpawnerSilkConfig config = mock(SpawnerSilkConfig.class);
        Player player = mock(Player.class);
        PlayerInventory inventory = mock(PlayerInventory.class);
        SpawnerSilkListener listener = new SpawnerSilkListener(plugin);

        when(plugin.getDataConfig()).thenReturn(config);
        when(config.getInt(SpawnerSilkConfig.PICKAXE_MODE)).thenReturn(3);
        when(player.getInventory()).thenReturn(inventory);
        when(inventory.getItemInMainHand()).thenReturn(new ItemStack(Material.IRON_PICKAXE));

        assertTrue(listener.canGetSpawner(player));
    }

    @Test
    void canGetSpawnerRejectsLowerPickaxeTier() {
        SpawnerSilk plugin = mock(SpawnerSilk.class);
        SpawnerSilkConfig config = mock(SpawnerSilkConfig.class);
        Player player = mock(Player.class);
        PlayerInventory inventory = mock(PlayerInventory.class);
        SpawnerSilkListener listener = new SpawnerSilkListener(plugin);

        when(plugin.getDataConfig()).thenReturn(config);
        when(config.getInt(SpawnerSilkConfig.PICKAXE_MODE)).thenReturn(6);
        when(player.getInventory()).thenReturn(inventory);
        when(inventory.getItemInMainHand()).thenReturn(new ItemStack(Material.DIAMOND_PICKAXE));

        assertFalse(listener.canGetSpawner(player));
    }

    @Test
    void dropToPlayerStopsWhenEntityIsBlacklisted() {
        SpawnerSilk plugin = mock(SpawnerSilk.class);
        SpawnerSilkConfig config = mock(SpawnerSilkConfig.class);
        BlockBreakEvent event = mock(BlockBreakEvent.class);
        Block block = mock(Block.class);
        CreatureSpawner spawner = mock(CreatureSpawner.class);
        Player player = mock(Player.class);
        SpawnerSilkListener listener = new SpawnerSilkListener(plugin);

        when(plugin.getDataConfig()).thenReturn(config);
        when(config.getList(SpawnerSilkConfig.BLACKLIST)).thenReturn(new java.util.ArrayList<>(Arrays.asList("ZOMBIE")));
        when(event.getBlock()).thenReturn(block);
        when(block.getState()).thenReturn(spawner);
        when(spawner.getSpawnedType()).thenReturn(EntityType.ZOMBIE);
        when(event.getPlayer()).thenReturn(player);
        try (MockedStatic<SpawnerAPI> spawnerApi = mockStatic(SpawnerAPI.class)) {
            spawnerApi.when(() -> SpawnerAPI.getSpawner(EntityType.ZOMBIE)).thenReturn(new ItemStack(Material.SPAWNER));

            listener.dropToPlayer(event, true, true);

            verify(event, never()).setExpToDrop(any(Integer.class));
            verify(player, never()).getInventory();
        }
    }

    @Test
    void dropToPlayerAddsSpawnerToInventoryInDropModeZero() {
        SpawnerSilk plugin = mock(SpawnerSilk.class);
        SpawnerSilkConfig config = mock(SpawnerSilkConfig.class);
        BlockBreakEvent event = mock(BlockBreakEvent.class);
        Block block = mock(Block.class);
        CreatureSpawner spawner = mock(CreatureSpawner.class);
        Player player = mock(Player.class);
        PlayerInventory inventory = mock(PlayerInventory.class);
        SpawnerSilkListener listener = new SpawnerSilkListener(plugin);
        ItemStack spawnerItem = new ItemStack(Material.SPAWNER);

        when(plugin.getDataConfig()).thenReturn(config);
        when(config.getList(SpawnerSilkConfig.BLACKLIST)).thenReturn(new java.util.ArrayList<>());
        when(config.getBoolean(SpawnerSilkConfig.SPAWNERS_GENERATE_XP)).thenReturn(false);
        when(config.getInt(SpawnerSilkConfig.DROP_MODE)).thenReturn(0);
        when(config.getBoolean(SpawnerSilkConfig.DROP_IN_CREATIVE)).thenReturn(false);
        when(config.getBoolean(SpawnerSilkConfig.DROP_TO_INVENTORY)).thenReturn(true);
        when(event.getBlock()).thenReturn(block);
        when(block.getState()).thenReturn(spawner);
        when(spawner.getSpawnedType()).thenReturn(EntityType.ZOMBIE);
        when(event.getPlayer()).thenReturn(player);
        when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
        when(player.getInventory()).thenReturn(inventory);
        when(inventory.firstEmpty()).thenReturn(0);

        try (MockedStatic<SpawnerAPI> spawnerApi = mockStatic(SpawnerAPI.class)) {
            spawnerApi.when(() -> SpawnerAPI.getSpawner(EntityType.ZOMBIE)).thenReturn(spawnerItem);

            listener.dropToPlayer(event, true, false);

            verify(event).setExpToDrop(0);
            verify(inventory).addItem(eq(spawnerItem));
        }
    }

    @Test
    void dropToPlayerDropsNaturallyWhenInventoryIsFull() {
        SpawnerSilk plugin = mock(SpawnerSilk.class);
        SpawnerSilkConfig config = mock(SpawnerSilkConfig.class);
        BlockBreakEvent event = mock(BlockBreakEvent.class);
        Block block = mock(Block.class);
        CreatureSpawner spawner = mock(CreatureSpawner.class);
        Player player = mock(Player.class);
        PlayerInventory inventory = mock(PlayerInventory.class);
        World world = mock(World.class);
        Location location = mock(Location.class);
        SpawnerSilkListener listener = new SpawnerSilkListener(plugin);
        ItemStack spawnerItem = new ItemStack(Material.SPAWNER);

        when(plugin.getDataConfig()).thenReturn(config);
        when(config.getList(SpawnerSilkConfig.BLACKLIST)).thenReturn(new java.util.ArrayList<>());
        when(config.getBoolean(SpawnerSilkConfig.SPAWNERS_GENERATE_XP)).thenReturn(true);
        when(config.getInt(SpawnerSilkConfig.DROP_MODE)).thenReturn(0);
        when(config.getBoolean(SpawnerSilkConfig.DROP_IN_CREATIVE)).thenReturn(false);
        when(config.getBoolean(SpawnerSilkConfig.DROP_TO_INVENTORY)).thenReturn(true);
        when(event.getBlock()).thenReturn(block);
        when(block.getState()).thenReturn(spawner);
        when(block.getLocation()).thenReturn(location);
        when(spawner.getSpawnedType()).thenReturn(EntityType.ZOMBIE);
        when(event.getPlayer()).thenReturn(player);
        when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
        when(player.getInventory()).thenReturn(inventory);
        when(player.getWorld()).thenReturn(world);
        when(inventory.firstEmpty()).thenReturn(-1);

        try (MockedStatic<SpawnerAPI> spawnerApi = mockStatic(SpawnerAPI.class)) {
            spawnerApi.when(() -> SpawnerAPI.getSpawner(EntityType.ZOMBIE)).thenReturn(spawnerItem);

            listener.dropToPlayer(event, true, false);

            verify(world).dropItemNaturally(location, spawnerItem);
        }
    }
}
