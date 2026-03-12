package me.crylonz.spawnersilk.utils;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Collections;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LocalizationManagerTest {

    @TempDir
    Path tempDir;

    @Test
    void loadUsesConfiguredLocaleWhenAvailable() {
        JavaPlugin plugin = mock(JavaPlugin.class);
        Logger logger = mock(Logger.class);
        YamlConfiguration config = new YamlConfiguration();
        LocalizationManager manager = new LocalizationManager(plugin);

        config.set(SpawnerSilkConfig.LANGUAGE, "fr_fr");
        when(plugin.getDataFolder()).thenReturn(tempDir.toFile());
        when(plugin.getLogger()).thenReturn(logger);
        when(plugin.getConfig()).thenReturn(config);
        when(plugin.getResource("localization/en_us.json")).thenReturn(stream("{\"message\":\"english\"}"));
        when(plugin.getResource("localization/fr_fr.json")).thenReturn(stream("{\"message\":\"bonjour {name}\"}"));

        manager.load();

        assertEquals("bonjour Alex", manager.getMessage("message", Collections.singletonMap("name", "Alex")));
    }

    @Test
    void loadFallsBackToDefaultLocaleWhenConfiguredLocaleIsMissing() {
        JavaPlugin plugin = mock(JavaPlugin.class);
        Logger logger = mock(Logger.class);
        YamlConfiguration config = new YamlConfiguration();
        LocalizationManager manager = new LocalizationManager(plugin);

        config.set(SpawnerSilkConfig.LANGUAGE, "de_de");
        when(plugin.getDataFolder()).thenReturn(tempDir.toFile());
        when(plugin.getLogger()).thenReturn(logger);
        when(plugin.getConfig()).thenReturn(config);
        when(plugin.getResource("localization/en_us.json")).thenReturn(stream("{\"message\":\"english\"}"));
        when(plugin.getResource("localization/de_de.json")).thenReturn(null);

        manager.load();

        assertEquals("english", manager.getMessage("message"));
        verify(logger).warning("Localization file 'de_de.json' not found. Falling back to 'en_us.json'");
    }

    private ByteArrayInputStream stream(String json) {
        return new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
    }
}
