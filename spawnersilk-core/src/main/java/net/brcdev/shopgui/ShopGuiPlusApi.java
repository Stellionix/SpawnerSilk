package net.brcdev.shopgui;

import me.crylonz.spawnersilk.SpawnerSilkProvider;
import net.brcdev.shopgui.exception.api.ExternalSpawnerProviderNameConflictException;

public final class ShopGuiPlusApi {

    private ShopGuiPlusApi() {
    }

    public static void registerSpawnerProvider(SpawnerSilkProvider provider)
            throws ExternalSpawnerProviderNameConflictException {
        // Local stub kept to make the project buildable when the external ShopGUI+ API artifact is unavailable.
    }
}
