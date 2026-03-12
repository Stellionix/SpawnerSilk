package me.crylonz.spawnersilk.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

public class SpawnerSilkConfig {

    public static final String AUTO_UPDATE = "auto-update";
    public static final String NEED_SILK_TOUCH_TO_DESTROY = "need-silk-touch-to-destroy";
    public static final String NEED_SILK_TOUCH = "need-silk-touch";
    public static final String PICKAXE_MODE = "pickaxe-mode";
    public static final String DROP_MODE = "drop-mode";
    public static final String DROP_CHANCE = "drop-chance";
    public static final String DROP_EGG_CHANCE = "drop-egg-chance";
    public static final String EXPLOSION_DROP_CHANCE = "explosion-drop-chance";
    public static final String SPAWNERS_CAN_BE_MODIFIED_BY_EGG = "spawners-can-be-modified-by-egg";
    public static final String DROP_TO_INVENTORY = "drop-to-inventory";
    public static final String USE_EGG = "use-egg";
    public static final String DROP_IN_CREATIVE = "drop-in-creative";
    public static final String SPAWNERS_GENERATE_XP = "spawners-generate-xp";
    public static final String SPAWNER_OVERLAY = "spawner-overlay";
    public static final String SPAWNER_OVERLAY_DELAY = "spawner-overlay-delay";
    public static final String BLACKLIST = "blacklist";
    private static final String LEGACY_BLACKLIST = "black-list";

    private final JavaPlugin plugin;

    public SpawnerSilkConfig(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        plugin.saveDefaultConfig();
        FileConfiguration config = plugin.getConfig();

        applyDefaults(config);
        migrateLegacyKeys(config);
        validateAndNormalize(config);

        config.options().copyDefaults(true);
        plugin.saveConfig();
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

    public FileConfiguration getConfiguration() {
        return plugin.getConfig();
    }

    private void applyDefaults(FileConfiguration config) {
        config.addDefault(AUTO_UPDATE, true);
        config.addDefault(NEED_SILK_TOUCH_TO_DESTROY, false);
        config.addDefault(NEED_SILK_TOUCH, true);
        config.addDefault(PICKAXE_MODE, 5);
        config.addDefault(DROP_MODE, 0);
        config.addDefault(DROP_CHANCE, 100);
        config.addDefault(DROP_EGG_CHANCE, 100);
        config.addDefault(EXPLOSION_DROP_CHANCE, 10);
        config.addDefault(SPAWNERS_CAN_BE_MODIFIED_BY_EGG, true);
        config.addDefault(DROP_TO_INVENTORY, false);
        config.addDefault(USE_EGG, true);
        config.addDefault(DROP_IN_CREATIVE, false);
        config.addDefault(SPAWNERS_GENERATE_XP, false);
        config.addDefault(SPAWNER_OVERLAY, true);
        config.addDefault(SPAWNER_OVERLAY_DELAY, 10);
        config.addDefault(BLACKLIST, Arrays.asList("BOAT_SPAWNER"));
    }

    private void migrateLegacyKeys(FileConfiguration config) {
        Logger logger = plugin.getLogger();

        if (config.contains(LEGACY_BLACKLIST) && !config.isSet(BLACKLIST)) {
            config.set(BLACKLIST, config.getStringList(LEGACY_BLACKLIST));
            logger.info("Migrated legacy config key '" + LEGACY_BLACKLIST + "' to '" + BLACKLIST + "'");
        }

        if (config.contains(LEGACY_BLACKLIST)) {
            config.set(LEGACY_BLACKLIST, null);
        }
    }

    private void validateAndNormalize(FileConfiguration config) {
        normalizeInt(config, PICKAXE_MODE, 5, 0, 6);
        normalizeInt(config, DROP_MODE, 0, 0, 1);
        normalizeInt(config, DROP_CHANCE, 100, 0, 100);
        normalizeInt(config, DROP_EGG_CHANCE, 100, 0, 100);
        normalizeInt(config, EXPLOSION_DROP_CHANCE, 10, 0, 100);
        normalizeInt(config, SPAWNER_OVERLAY_DELAY, 10, 3, Integer.MAX_VALUE);
        normalizeUppercaseList(config, BLACKLIST, Arrays.asList("BOAT_SPAWNER"));
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
        if (LEGACY_BLACKLIST.equals(key)) {
            return BLACKLIST;
        }
        return key;
    }
}
