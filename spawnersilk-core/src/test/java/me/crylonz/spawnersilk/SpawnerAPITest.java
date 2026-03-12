package me.crylonz.spawnersilk;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SpawnerAPITest {

    @Test
    void stringToCustomItemStackBuildsFormattedSpawnerItem() {
        ItemMeta itemMeta = mock(ItemMeta.class);

        try (MockedStatic<SpawnerSilk> spawnerSilk = mockStatic(SpawnerSilk.class);
             MockedConstruction<ItemStack> itemStacks = mockConstruction(ItemStack.class, (mock, context) -> {
                 when(mock.getItemMeta()).thenReturn(itemMeta);
             })) {
            spawnerSilk.when(SpawnerSilk::getSpawnerMaterial).thenReturn(Material.SPAWNER);

            ItemStack itemStack = SpawnerAPI.stringToCustomItemStack("zombie_horse", 3);

            assertSame(itemStacks.constructed().get(0), itemStack);
            ArgumentCaptor<String> displayName = ArgumentCaptor.forClass(String.class);
            verify(itemMeta).setDisplayName(displayName.capture());
            assertTrue(displayName.getValue().contains("Zombie Horse Spawner"));
            verify(itemStack).setItemMeta(itemMeta);
        }
    }

    @Test
    void getEntityTypeReturnsEntityFromSpawnerDisplayName() {
        ItemStack itemStack = mock(ItemStack.class);
        ItemMeta itemMeta = mock(ItemMeta.class);

        when(itemStack.getType()).thenReturn(Material.SPAWNER);
        when(itemStack.getItemMeta()).thenReturn(itemMeta);
        when(itemMeta.getDisplayName()).thenReturn("Zombie Spawner");

        try (MockedStatic<SpawnerSilk> spawnerSilk = mockStatic(SpawnerSilk.class)) {
            spawnerSilk.when(SpawnerSilk::getSpawnerMaterial).thenReturn(Material.SPAWNER);

            assertSame(EntityType.ZOMBIE, SpawnerAPI.getEntityType(itemStack));
        }
    }

    @Test
    void getEntityTypeReturnsUnknownForInvalidItem() {
        ItemStack itemStack = mock(ItemStack.class);

        when(itemStack.getType()).thenReturn(Material.DIRT);

        try (MockedStatic<SpawnerSilk> spawnerSilk = mockStatic(SpawnerSilk.class)) {
            spawnerSilk.when(SpawnerSilk::getSpawnerMaterial).thenReturn(Material.SPAWNER);

            assertSame(EntityType.UNKNOWN, SpawnerAPI.getEntityType(itemStack));
        }
    }
}
