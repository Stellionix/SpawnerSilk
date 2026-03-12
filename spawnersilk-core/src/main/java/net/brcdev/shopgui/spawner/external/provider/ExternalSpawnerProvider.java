package net.brcdev.shopgui.spawner.external.provider;

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public interface ExternalSpawnerProvider {

    String getName();

    ItemStack getSpawnerItem(EntityType entityType);

    EntityType getSpawnerEntityType(ItemStack itemStack);
}
