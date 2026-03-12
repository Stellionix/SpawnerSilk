package me.crylonz.spawnersilk.utils;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SpawnerSilkConfigTest {

    @Test
    void loadAddsMissingDefaultsWithoutOverwritingExistingValues() {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.set(SpawnerSilkConfig.AUTO_UPDATE, false);
        configuration.set(SpawnerSilkConfig.DROP_CHANCE, 42);

        JavaPlugin plugin = mockPlugin(configuration);
        SpawnerSilkConfig config = new SpawnerSilkConfig(plugin);

        config.load();

        assertFalse(config.getBoolean(SpawnerSilkConfig.AUTO_UPDATE));
        assertEquals(42, config.getInt(SpawnerSilkConfig.DROP_CHANCE));
        assertEquals(10, config.getInt(SpawnerSilkConfig.SPAWNER_OVERLAY_DELAY));
        assertEquals("en_us", config.getString(SpawnerSilkConfig.LANGUAGE));
        assertIterableEquals(Arrays.asList("BOAT_SPAWNER"), config.getList(SpawnerSilkConfig.BLACKLIST));
        verify(plugin).reloadConfig();
    }

    @Test
    void loadMigratesLegacyBlacklistKey() {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.set("black-list", Arrays.asList("zombie horse", "bee_spawner"));

        JavaPlugin plugin = mockPlugin(configuration);
        SpawnerSilkConfig config = new SpawnerSilkConfig(plugin);

        config.load();

        assertIterableEquals(Arrays.asList("ZOMBIE_HORSE", "BEE_SPAWNER"), config.getList(SpawnerSilkConfig.BLACKLIST));
        assertIterableEquals(config.getList("black-list"), config.getList(SpawnerSilkConfig.BLACKLIST));
    }

    @Test
    void loadClampsOutOfRangeNumericValues() {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.set(SpawnerSilkConfig.PICKAXE_MODE, 999);
        configuration.set(SpawnerSilkConfig.DROP_MODE, -10);
        configuration.set(SpawnerSilkConfig.DROP_CHANCE, 101);
        configuration.set(SpawnerSilkConfig.DROP_EGG_CHANCE, -1);
        configuration.set(SpawnerSilkConfig.EXPLOSION_DROP_CHANCE, 1000);
        configuration.set(SpawnerSilkConfig.SPAWNER_OVERLAY_DELAY, 1);

        JavaPlugin plugin = mockPlugin(configuration);
        SpawnerSilkConfig config = new SpawnerSilkConfig(plugin);

        config.load();

        assertEquals(6, config.getInt(SpawnerSilkConfig.PICKAXE_MODE));
        assertEquals(0, config.getInt(SpawnerSilkConfig.DROP_MODE));
        assertEquals(100, config.getInt(SpawnerSilkConfig.DROP_CHANCE));
        assertEquals(0, config.getInt(SpawnerSilkConfig.DROP_EGG_CHANCE));
        assertEquals(100, config.getInt(SpawnerSilkConfig.EXPLOSION_DROP_CHANCE));
        assertEquals(3, config.getInt(SpawnerSilkConfig.SPAWNER_OVERLAY_DELAY));
    }

    @Test
    void loadResetsInvalidTypesToSafeDefaults() {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.set(SpawnerSilkConfig.DROP_CHANCE, "invalid");
        configuration.set(SpawnerSilkConfig.BLACKLIST, "boat_spawner");
        configuration.set(SpawnerSilkConfig.LANGUAGE, "");

        JavaPlugin plugin = mockPlugin(configuration);
        SpawnerSilkConfig config = new SpawnerSilkConfig(plugin);

        config.load();

        assertEquals(100, config.getInt(SpawnerSilkConfig.DROP_CHANCE));
        assertIterableEquals(Arrays.asList("BOAT_SPAWNER"), config.getList(SpawnerSilkConfig.BLACKLIST));
        assertEquals("en_us", config.getString(SpawnerSilkConfig.LANGUAGE));
        assertTrue(config.getBoolean(SpawnerSilkConfig.SPAWNER_OVERLAY));
    }

    private JavaPlugin mockPlugin(YamlConfiguration initialConfig) {
        JavaPlugin plugin = mock(JavaPlugin.class);
        Logger logger = mock(Logger.class);
        File dataFolder = new File("build/tmp/test-config-" + System.nanoTime());
        File configFile = new File(dataFolder, "config.yml");

        dataFolder.mkdirs();
        try {
            initialConfig.save(configFile);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        when(plugin.getDataFolder()).thenReturn(dataFolder);
        when(plugin.getLogger()).thenReturn(logger);
        when(plugin.getResource("config.yml")).thenReturn(new ByteArrayInputStream(defaultConfig().getBytes(StandardCharsets.UTF_8)));
        when(plugin.getConfig()).thenReturn(YamlConfiguration.loadConfiguration(configFile));

        doAnswer(invocation -> {
            when(plugin.getConfig()).thenReturn(YamlConfiguration.loadConfiguration(configFile));
            return null;
        }).when(plugin).reloadConfig();

        return plugin;
    }

    private String defaultConfig() {
        return ""
                + "localization:\n"
                + "  language: en_us\n"
                + "updates:\n"
                + "  auto-download: true\n"
                + "mining:\n"
                + "  require-silk-touch-to-break: false\n"
                + "  require-silk-touch-to-drop: true\n"
                + "  required-pickaxe-tier: 5\n"
                + "drops:\n"
                + "  mode: 0\n"
                + "  spawner-chance: 100\n"
                + "  egg-chance: 100\n"
                + "  explosion-chance: 10\n"
                + "  to-inventory: false\n"
                + "  allow-in-creative: false\n"
                + "interaction:\n"
                + "  allow-egg-modification: true\n"
                + "  consume-egg: true\n"
                + "experience:\n"
                + "  drop-from-spawners: false\n"
                + "overlay:\n"
                + "  enabled: true\n"
                + "  duration-seconds: 10\n"
                + "feedback:\n"
                + "  break-errors: true\n"
                + "  place-success: false\n"
                + "  interact-errors: true\n"
                + "  interact-success: false\n"
                + "restrictions:\n"
                + "  blacklist:\n"
                + "    - BOAT_SPAWNER\n";
    }
}
