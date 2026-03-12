package me.crylonz.spawnersilk.utils;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LocalizationManager {

    private static final String DEFAULT_LOCALE = "en_us";
    private static final String LOCALIZATION_DIR = "localization";

    private final JavaPlugin plugin;
    private Map<String, String> messages = Collections.emptyMap();

    public LocalizationManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        ensureLocalizationExists(DEFAULT_LOCALE);

        String locale = plugin.getConfig().getString(SpawnerSilkConfig.LANGUAGE, DEFAULT_LOCALE);
        if (locale == null || locale.trim().isEmpty()) {
            locale = DEFAULT_LOCALE;
        }
        locale = locale.trim().toLowerCase(Locale.ROOT);
        ensureLocalizationExists(locale);

        Path localizationFile = plugin.getDataFolder().toPath()
                .resolve(LOCALIZATION_DIR)
                .resolve(locale + ".json");

        if (Files.notExists(localizationFile)) {
            plugin.getLogger().warning("Localization file '" + locale + ".json' not found. Falling back to '" + DEFAULT_LOCALE + ".json'");
            localizationFile = plugin.getDataFolder().toPath()
                    .resolve(LOCALIZATION_DIR)
                    .resolve(DEFAULT_LOCALE + ".json");
        }

        try (Reader reader = Files.newBufferedReader(localizationFile, StandardCharsets.UTF_8)) {
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(reader);
            Map<String, String> loadedMessages = new HashMap<>();
            for (Object key : jsonObject.keySet()) {
                Object value = jsonObject.get(key);
                if (key != null && value != null) {
                    loadedMessages.put(String.valueOf(key), String.valueOf(value));
                }
            }
            messages = loadedMessages;
        } catch (IOException | ParseException e) {
            plugin.getLogger().severe("Unable to load localization file '" + locale + ".json'");
            messages = Collections.emptyMap();
        }
    }

    public String getMessage(String key) {
        return colorize(messages.getOrDefault(key, key));
    }

    public String getMessage(String key, Map<String, String> replacements) {
        String message = messages.getOrDefault(key, key);
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return colorize(message);
    }

    private void ensureLocalizationExists(String locale) {
        Path localizationDirectory = plugin.getDataFolder().toPath().resolve(LOCALIZATION_DIR);
        Path localizationFile = localizationDirectory.resolve(locale + ".json");

        try {
            Files.createDirectories(localizationDirectory);
            if (Files.notExists(localizationFile)) {
                try (InputStream inputStream = plugin.getResource(LOCALIZATION_DIR + "/" + locale + ".json")) {
                    if (inputStream != null) {
                        Files.copy(inputStream, localizationFile);
                    }
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to initialize localization files", e);
        }
    }

    private String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
