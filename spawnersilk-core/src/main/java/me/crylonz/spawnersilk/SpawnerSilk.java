package me.crylonz.spawnersilk;

import me.crylonz.spawnersilk.command.EditSpawnerCommandExecutor;
import me.crylonz.spawnersilk.command.GiveSpawnerCommandExecutor;
import me.crylonz.spawnersilk.command.SpawnerSilkCommandExecutor;
import me.crylonz.spawnersilk.command.SpawnerSilkTabCompletion;
import me.crylonz.spawnersilk.external.ShopGuiPlus;
import me.crylonz.spawnersilk.utils.LocalizationManager;
import me.crylonz.spawnersilk.utils.SpawnerSilkConfig;
import me.crylonz.spawnersilk.utils.SpawnerSilkUpdater;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Logger;


public class SpawnerSilk extends JavaPlugin implements Listener {

    public static final Logger log = Logger.getLogger("Minecraft");
    public static HashMap<String, String> playersUUID = new HashMap<>();
    private SpawnerSilkProvider spawnerProvider;
    public SpawnerSilkConfig config = new SpawnerSilkConfig(this);
    private final LocalizationManager localization = new LocalizationManager(this);

    @Override
    public void onEnable() {

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new SpawnerSilkListener(this), this);

        Metrics metrics = new Metrics(this, 5536);

        GiveSpawnerCommandExecutor giveSpawnerCommandExecutor = new GiveSpawnerCommandExecutor(this);
        EditSpawnerCommandExecutor editSpawnerCommandExecutor = new EditSpawnerCommandExecutor(this);

        this.getCommand("givespawner").setExecutor(giveSpawnerCommandExecutor);
        this.getCommand("editspawner").setExecutor(editSpawnerCommandExecutor);
        this.getCommand("sps").setExecutor(new SpawnerSilkCommandExecutor(this, giveSpawnerCommandExecutor, editSpawnerCommandExecutor));

        Objects.requireNonNull(getCommand("givespawner")).setTabCompleter(new SpawnerSilkTabCompletion());
        Objects.requireNonNull(getCommand("editspawner")).setTabCompleter(new SpawnerSilkTabCompletion());
        Objects.requireNonNull(getCommand("sps")).setTabCompleter(new SpawnerSilkTabCompletion());

        loadPluginConfig();

        if (Bukkit.getPluginManager().getPlugin("ShopGUIPlus") != null) {
            this.spawnerProvider = new SpawnerSilkProvider();
            ShopGuiPlus.hookIntoShopGui(spawnerProvider, this.getLogger());
            this.getLogger().info(localization.getMessage("plugin.shopgui.enabled"));
        }  else {
            this.getLogger().info(localization.getMessage("plugin.shopgui.disabled"));
        }

        for (Player play : Bukkit.getOnlinePlayers()) {
            playersUUID.put(play.getName(), play.getUniqueId().toString());
        }

        if (config.getBoolean(SpawnerSilkConfig.AUTO_UPDATE)) {
            SpawnerSilkUpdater updater = new SpawnerSilkUpdater(this, 322295, this.getFile(), SpawnerSilkUpdater.UpdateType.DEFAULT, true);
        }
    }

    @Override
    public void onDisable() {

    }

    public SpawnerSilkConfig getDataConfig() {
        return config;
    }

    public void loadPluginConfig() {
        config.load();
        localization.load();
    }

    public LocalizationManager getLocalization() {
        return localization;
    }

    public static Material getSpawnerMaterial() {
        if (Bukkit.getVersion().contains("1.12")) {
            return Material.getMaterial("MOB_SPAWNER");
        }
        return Material.SPAWNER;
    }
}
