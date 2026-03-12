package me.crylonz.spawnersilk;

import me.crylonz.spawnersilk.utils.LocalizationManager;
import me.crylonz.spawnersilk.utils.SpawnerSilkConfig;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

class SpawnerSilkTest {

    @Test
    void loadPluginConfigLoadsConfigAndLocalization() throws Exception {
        SpawnerSilk plugin = mock(SpawnerSilk.class);
        SpawnerSilkConfig config = mock(SpawnerSilkConfig.class);
        LocalizationManager localization = mock(LocalizationManager.class);

        setField(plugin, "config", config);
        setField(plugin, "localization", localization);
        doCallRealMethod().when(plugin).loadPluginConfig();

        plugin.loadPluginConfig();

        verify(config).load();
        verify(localization).load();
    }

    @Test
    void getSpawnerMaterialReturnsLegacyMaterialFor112() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(Bukkit::getVersion).thenReturn("git-Spigot-1.12.2");

            assertEquals(Material.getMaterial("MOB_SPAWNER"), SpawnerSilk.getSpawnerMaterial());
        }
    }

    @Test
    void getSpawnerMaterialReturnsSpawnerForModernVersions() {
        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(Bukkit::getVersion).thenReturn("1.21.1");

            assertEquals(Material.SPAWNER, SpawnerSilk.getSpawnerMaterial());
        }
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = SpawnerSilk.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
