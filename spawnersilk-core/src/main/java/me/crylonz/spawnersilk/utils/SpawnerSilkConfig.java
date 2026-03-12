package me.crylonz.spawnersilk.utils;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

public class SpawnerSilkConfig {

    public static final String AUTO_UPDATE = "updates.auto-download";
    public static final String NEED_SILK_TOUCH_TO_DESTROY = "mining.require-silk-touch-to-break";
    public static final String NEED_SILK_TOUCH = "mining.require-silk-touch-to-drop";
    public static final String LANGUAGE = "localization.language";
    public static final String PICKAXE_MODE = "mining.required-pickaxe-tier";
    public static final String DROP_MODE = "drops.mode";
    public static final String DROP_CHANCE = "drops.spawner-chance";
    public static final String DROP_EGG_CHANCE = "drops.egg-chance";
    public static final String EXPLOSION_DROP_CHANCE = "drops.explosion-chance";
    public static final String SPAWNERS_CAN_BE_MODIFIED_BY_EGG = "interaction.allow-egg-modification";
    public static final String DROP_TO_INVENTORY = "drops.to-inventory";
    public static final String USE_EGG = "interaction.consume-egg";
    public static final String DROP_IN_CREATIVE = "drops.allow-in-creative";
    public static final String SPAWNERS_GENERATE_XP = "experience.drop-from-spawners";
    public static final String SPAWNER_OVERLAY = "overlay.enabled";
    public static final String SPAWNER_OVERLAY_DELAY = "overlay.duration-seconds";
    public static final String FEEDBACK_BREAK_ERRORS = "feedback.break-errors";
    public static final String FEEDBACK_PLACE_SUCCESS = "feedback.place-success";
    public static final String FEEDBACK_INTERACT_ERRORS = "feedback.interact-errors";
    public static final String FEEDBACK_INTERACT_SUCCESS = "feedback.interact-success";
    public static final String BLACKLIST = "restrictions.blacklist";
    private static final String LEGACY_BLACKLIST = "black-list";

    private static final Map<String, String> LEGACY_KEY_MAPPINGS = new LinkedHashMap<>();

    static {
        LEGACY_KEY_MAPPINGS.put("auto-update", AUTO_UPDATE);
        LEGACY_KEY_MAPPINGS.put("need-silk-touch-to-destroy", NEED_SILK_TOUCH_TO_DESTROY);
        LEGACY_KEY_MAPPINGS.put("need-silk-touch", NEED_SILK_TOUCH);
        LEGACY_KEY_MAPPINGS.put("language", LANGUAGE);
        LEGACY_KEY_MAPPINGS.put("pickaxe-mode", PICKAXE_MODE);
        LEGACY_KEY_MAPPINGS.put("drop-mode", DROP_MODE);
        LEGACY_KEY_MAPPINGS.put("drop-chance", DROP_CHANCE);
        LEGACY_KEY_MAPPINGS.put("drop-egg-chance", DROP_EGG_CHANCE);
        LEGACY_KEY_MAPPINGS.put("explosion-drop-chance", EXPLOSION_DROP_CHANCE);
        LEGACY_KEY_MAPPINGS.put("spawners-can-be-modified-by-egg", SPAWNERS_CAN_BE_MODIFIED_BY_EGG);
        LEGACY_KEY_MAPPINGS.put("drop-to-inventory", DROP_TO_INVENTORY);
        LEGACY_KEY_MAPPINGS.put("use-egg", USE_EGG);
        LEGACY_KEY_MAPPINGS.put("drop-in-creative", DROP_IN_CREATIVE);
        LEGACY_KEY_MAPPINGS.put("spawners-generate-xp", SPAWNERS_GENERATE_XP);
        LEGACY_KEY_MAPPINGS.put("spawner-overlay", SPAWNER_OVERLAY);
        LEGACY_KEY_MAPPINGS.put("spawner-overlay-delay", SPAWNER_OVERLAY_DELAY);
        LEGACY_KEY_MAPPINGS.put("feedback-break-errors", FEEDBACK_BREAK_ERRORS);
        LEGACY_KEY_MAPPINGS.put("feedback-place-success", FEEDBACK_PLACE_SUCCESS);
        LEGACY_KEY_MAPPINGS.put("feedback-interact-errors", FEEDBACK_INTERACT_ERRORS);
        LEGACY_KEY_MAPPINGS.put("feedback-interact-success", FEEDBACK_INTERACT_SUCCESS);
        LEGACY_KEY_MAPPINGS.put("blacklist", BLACKLIST);
        LEGACY_KEY_MAPPINGS.put(LEGACY_BLACKLIST, BLACKLIST);
    }

    private final JavaPlugin plugin;

    public SpawnerSilkConfig(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveDefaultConfig();
        }

        FileConfiguration existingConfig = YamlConfiguration.loadConfiguration(configFile);
        migrateLegacyKeys(existingConfig);

        YamlConfiguration mergedConfig = loadDefaultTemplate();
        mergeKnownValues(mergedConfig, existingConfig);
        validateAndNormalize(mergedConfig);

        try {
            mergedConfig.save(configFile);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to save config.yml", e);
        }

        plugin.reloadConfig();
    }

    public Boolean getBoolean(String key) {
        return getConfiguration().getBoolean(resolveKey(key));
    }

    public ArrayList<String> getList(String key) {
        return new ArrayList<>(getConfiguration().getStringList(resolveKey(key)));
    }

    public double getDouble(String key) {
        return getConfiguration().getDouble(resolveKey(key));
    }

    public int getInt(String key) {
        return getConfiguration().getInt(resolveKey(key));
    }

    public String getString(String key) {
        return getConfiguration().getString(resolveKey(key));
    }

    public FileConfiguration getConfiguration() {
        return plugin.getConfig();
    }

    private YamlConfiguration loadDefaultTemplate() {
        try (InputStream inputStream = plugin.getResource("config.yml")) {
            if (inputStream == null) {
                throw new IOException("Missing embedded config.yml");
            }

            try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                return YamlConfiguration.loadConfiguration(reader);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read embedded config.yml", e);
        }
    }

    private void migrateLegacyKeys(FileConfiguration config) {
        Logger logger = plugin.getLogger();

        for (Map.Entry<String, String> entry : LEGACY_KEY_MAPPINGS.entrySet()) {
            String legacyKey = entry.getKey();
            String newKey = entry.getValue();

            if (!legacyKey.equals(newKey) && config.contains(legacyKey) && !config.isSet(newKey)) {
                config.set(newKey, config.get(legacyKey));
                logger.info("Migrated legacy config key '" + legacyKey + "' to '" + newKey + "'");
            }
        }

        for (String legacyKey : LEGACY_KEY_MAPPINGS.keySet()) {
            String mappedKey = LEGACY_KEY_MAPPINGS.get(legacyKey);
            if (!legacyKey.equals(mappedKey) && config.contains(legacyKey)) {
                config.set(legacyKey, null);
            }
        }
    }

    private void mergeKnownValues(YamlConfiguration targetConfig, FileConfiguration existingConfig) {
        for (String key : targetConfig.getKeys(true)) {
            if (isSection(targetConfig, key)) {
                continue;
            }

            String sourceKey = resolveSourceKey(existingConfig, key);
            if (sourceKey != null && existingConfig.isSet(sourceKey)) {
                targetConfig.set(key, existingConfig.get(sourceKey));
            }
        }
    }

    private void validateAndNormalize(FileConfiguration config) {
        normalizeLocale(config, LANGUAGE, "en_us");
        normalizeInt(config, PICKAXE_MODE, 5, 0, 6);
        normalizeInt(config, DROP_MODE, 0, 0, 1);
        normalizeInt(config, DROP_CHANCE, 100, 0, 100);
        normalizeInt(config, DROP_EGG_CHANCE, 100, 0, 100);
        normalizeInt(config, EXPLOSION_DROP_CHANCE, 10, 0, 100);
        normalizeInt(config, SPAWNER_OVERLAY_DELAY, 10, 3, Integer.MAX_VALUE);
        normalizeUppercaseList(config, BLACKLIST, Arrays.asList("BOAT_SPAWNER"));
        normalizeBoolean(config, FEEDBACK_BREAK_ERRORS, true);
        normalizeBoolean(config, FEEDBACK_PLACE_SUCCESS, false);
        normalizeBoolean(config, FEEDBACK_INTERACT_ERRORS, true);
        normalizeBoolean(config, FEEDBACK_INTERACT_SUCCESS, false);
    }

    private void normalizeLocale(FileConfiguration config, String key, String defaultValue) {
        String value = config.getString(key);
        if (value == null || value.trim().isEmpty()) {
            config.set(key, defaultValue);
            plugin.getLogger().warning("Config key '" + key + "' is invalid. Reset to " + defaultValue);
            return;
        }

        config.set(key, value.trim().toLowerCase(Locale.ROOT));
    }

    private void normalizeInt(FileConfiguration config, String key, int defaultValue, int min, int max) {
        if (!config.isInt(key)) {
            config.set(key, defaultValue);
            plugin.getLogger().warning("Config key '" + key + "' is invalid. Reset to " + defaultValue);
            return;
        }

        int value = config.getInt(key);
        int normalized = Math.max(min, Math.min(max, value));
        if (value != normalized) {
            config.set(key, normalized);
            plugin.getLogger().warning("Config key '" + key + "' out of range. Clamped to " + normalized);
        }
    }

    private void normalizeBoolean(FileConfiguration config, String key, boolean defaultValue) {
        if (!config.isBoolean(key)) {
            config.set(key, defaultValue);
            plugin.getLogger().warning("Config key '" + key + "' is invalid. Reset to " + defaultValue);
        }
    }

    private void normalizeUppercaseList(FileConfiguration config, String key, List<String> defaultValue) {
        List<String> values;
        if (config.isList(key)) {
            values = config.getStringList(key);
        } else {
            values = defaultValue;
            plugin.getLogger().warning("Config key '" + key + "' is invalid. Reset to defaults.");
        }

        List<String> normalized = new ArrayList<>();
        for (String value : values) {
            if (value == null) {
                continue;
            }

            String sanitized = value.trim().toUpperCase(Locale.ROOT).replace(' ', '_');
            if (!sanitized.isEmpty()) {
                normalized.add(sanitized);
            }
        }

        if (normalized.isEmpty()) {
            normalized = new ArrayList<>(defaultValue);
        }

        config.set(key, normalized);
    }

    private String resolveKey(String key) {
        return LEGACY_KEY_MAPPINGS.getOrDefault(key, key);
    }

    private String resolveSourceKey(FileConfiguration config, String key) {
        if (config.isSet(key)) {
            return key;
        }

        for (Map.Entry<String, String> entry : LEGACY_KEY_MAPPINGS.entrySet()) {
            if (entry.getValue().equals(key) && config.isSet(entry.getKey())) {
                return entry.getKey();
            }
        }
        return key;
    }

    private boolean isSection(ConfigurationSection configurationSection, String key) {
        Object value = configurationSection.get(key);
        return value instanceof ConfigurationSection;
    }
}
