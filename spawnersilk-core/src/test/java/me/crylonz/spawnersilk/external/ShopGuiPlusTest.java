package me.crylonz.spawnersilk.external;

import me.crylonz.spawnersilk.SpawnerSilkProvider;
import net.brcdev.shopgui.ShopGuiPlusApi;
import net.brcdev.shopgui.exception.api.ExternalSpawnerProviderNameConflictException;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.logging.Logger;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

class ShopGuiPlusTest {

    @Test
    void hookIntoShopGuiRegistersProvider() throws Exception {
        SpawnerSilkProvider provider = mock(SpawnerSilkProvider.class);
        Logger logger = mock(Logger.class);

        try (MockedStatic<ShopGuiPlusApi> api = mockStatic(ShopGuiPlusApi.class)) {
            ShopGuiPlus.hookIntoShopGui(provider, logger);

            api.verify(() -> ShopGuiPlusApi.registerSpawnerProvider(provider));
        }
    }

    @Test
    void hookIntoShopGuiLogsWhenRegistrationFails() throws Exception {
        SpawnerSilkProvider provider = mock(SpawnerSilkProvider.class);
        Logger logger = mock(Logger.class);

        try (MockedStatic<ShopGuiPlusApi> api = mockStatic(ShopGuiPlusApi.class)) {
            api.when(() -> ShopGuiPlusApi.registerSpawnerProvider(provider))
                    .thenThrow(new ExternalSpawnerProviderNameConflictException("boom"));

            ShopGuiPlus.hookIntoShopGui(provider, logger);

            verify(logger).severe("ShopGUI+ support disabled because it can't be load");
        }
    }
}
