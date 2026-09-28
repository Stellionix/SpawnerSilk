package me.crylonz.spawnersilk;

import me.crylonz.spawnersilk.utils.SpawnerSilkConfig;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class SpawnerInteractionRegressionTest {
    private final SpawnerSilk plugin = mock(SpawnerSilk.class);
    private final SpawnerSilkConfig config = mock(SpawnerSilkConfig.class);
    private final Player player = mock(Player.class);
    private final Block block = mock(Block.class);
    private final CreatureSpawner spawner = mock(CreatureSpawner.class);

    private PlayerInteractEvent interaction(Material material, EquipmentSlot hand) {
        when(plugin.getDataConfig()).thenReturn(config);
        when(config.getBoolean(SpawnerSilkConfig.SPAWNERS_CAN_BE_MODIFIED_BY_EGG)).thenReturn(true);
        when(config.getBoolean(SpawnerSilkConfig.USE_EGG)).thenReturn(true);
        when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
        when(block.getType()).thenReturn(Material.SPAWNER);
        when(block.getState()).thenReturn(spawner);
        return new PlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK,
                new ItemStack(material, 3), block, org.bukkit.block.BlockFace.UP, hand);
    }

    private void interact(PlayerInteractEvent event) {
        try (MockedStatic<SpawnerSilk> statics = mockStatic(SpawnerSilk.class)) {
            statics.when(SpawnerSilk::getSpawnerMaterial).thenReturn(Material.SPAWNER);
            new SpawnerSilkListener(plugin).onPlayerInteractEvent(event);
        }
    }

    @Test
    void ordinaryEggDoesNotThrowOrChangeSpawner() {
        PlayerInteractEvent event = interaction(Material.EGG, EquipmentSlot.HAND);
        assertDoesNotThrow(() -> interact(event));
        verifyNoInteractions(spawner);
    }

    @Test
    void offhandSpawnEggIsConsumedInSurvival() {
        PlayerInteractEvent event = interaction(Material.ZOMBIE_SPAWN_EGG, EquipmentSlot.OFF_HAND);
        interact(event);
        verify(spawner).setSpawnedType(EntityType.ZOMBIE);
        org.junit.jupiter.api.Assertions.assertEquals(2, event.getItem().getAmount());
    }

    @Test
    void protectedBlockIsNotModifiedAndEggIsNotConsumed() {
        PlayerInteractEvent event = interaction(Material.ZOMBIE_SPAWN_EGG, EquipmentSlot.HAND);
        event.setUseInteractedBlock(Event.Result.DENY);
        interact(event);
        verifyNoInteractions(spawner);
        org.junit.jupiter.api.Assertions.assertEquals(3, event.getItem().getAmount());
    }

    @Test
    void creativeOffhandEggIsPreserved() {
        PlayerInteractEvent event = interaction(Material.ZOMBIE_SPAWN_EGG, EquipmentSlot.OFF_HAND);
        when(player.getGameMode()).thenReturn(GameMode.CREATIVE);
        interact(event);
        verify(spawner).setSpawnedType(EntityType.ZOMBIE);
        org.junit.jupiter.api.Assertions.assertEquals(3, event.getItem().getAmount());
    }
}
