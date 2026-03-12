package me.crylonz.spawnersilk.utils;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;

import java.util.ArrayList;

public class ArmorStandCleaner implements Runnable {
    ArrayList<ArmorStand> armorStands;

    public ArmorStandCleaner(ArrayList<ArmorStand> armorStands) {
        this.armorStands = armorStands;
    }

    @Override
    public void run() {
        Location armorStandLocation = armorStands.get(0).getLocation();

        armorStands.forEach(armorStand -> armorStands.forEach(Entity::remove));

        armorStandLocation.getChunk().setForceLoaded(false);

    }

    public boolean isChunkForceLoaded(Location location) {
        return location.getWorld() == null ||
                location.getWorld().isChunkForceLoaded(
                        location.getBlockX() >> 4,
                        location.getBlockZ() >> 4
                );
    }
}
