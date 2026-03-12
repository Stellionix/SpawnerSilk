package me.crylonz.spawnersilk.command;

import me.crylonz.spawnersilk.SpawnerAPI;
import me.crylonz.spawnersilk.SpawnerSilk;
import me.crylonz.spawnersilk.utils.LocalizationManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GiveSpawnerCommandExecutorTest {

    @AfterEach
    void clearPlayers() {
        SpawnerSilk.playersUUID.clear();
    }

    @Test
    void runCommandWithNullArgsUsesSpsUsageMessage() {
        SpawnerSilk plugin = mockPlugin();
        GiveSpawnerCommandExecutor executor = new GiveSpawnerCommandExecutor(plugin);
        Player player = mock(Player.class);

        assertTrue(executor.runCommand(player, null));

        verify(player).sendMessage("command.givespawner.usage.sps");
    }

    @Test
    void runCommandRejectsInvalidAmount() {
        SpawnerSilk plugin = mockPlugin();
        GiveSpawnerCommandExecutor executor = new GiveSpawnerCommandExecutor(plugin);
        Player sender = mock(Player.class);
        Player target = mock(Player.class);

        when(sender.hasPermission("spawnersilk.givespawner")).thenReturn(true);
        when(target.getUniqueId()).thenReturn(UUID.randomUUID());
        SpawnerSilk.playersUUID.put("Alex", target.getUniqueId().toString());

        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(Bukkit::getOnlinePlayers).thenReturn((Collection<? extends Player>) Arrays.asList(target));

            assertTrue(executor.runCommand(sender, new String[]{"Alex", "ZOMBIE", "abc"}));

            verify(sender).sendMessage("command.givespawner.usage.direct");
        }
    }

    @Test
    void runCommandWarnsWhenPlayerDoesNotExist() {
        SpawnerSilk plugin = mockPlugin();
        GiveSpawnerCommandExecutor executor = new GiveSpawnerCommandExecutor(plugin);
        Player sender = mock(Player.class);

        when(sender.hasPermission("spawnersilk.givespawner")).thenReturn(true);

        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(Bukkit::getOnlinePlayers).thenReturn((Collection<? extends Player>) Collections.<Player>emptyList());

            assertTrue(executor.runCommand(sender, new String[]{"Alex", "ZOMBIE"}));

            verify(sender).sendMessage("command.givespawner.invalid_player:Alex");
        }
    }

    @Test
    void runCommandWarnsWhenSpawnerTypeIsInvalid() {
        SpawnerSilk plugin = mockPlugin();
        GiveSpawnerCommandExecutor executor = new GiveSpawnerCommandExecutor(plugin);
        Player sender = mock(Player.class);
        Player target = mock(Player.class);
        ItemStack itemStack = mock(ItemStack.class);

        when(sender.hasPermission("spawnersilk.givespawner")).thenReturn(true);
        when(target.getUniqueId()).thenReturn(UUID.randomUUID());
        when(itemStack.getItemMeta()).thenReturn(mock(org.bukkit.inventory.meta.ItemMeta.class));
        SpawnerSilk.playersUUID.put("Alex", target.getUniqueId().toString());

        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class);
             MockedStatic<SpawnerAPI> spawnerApi = mockStatic(SpawnerAPI.class)) {
            bukkit.when(Bukkit::getOnlinePlayers).thenReturn((Collection<? extends Player>) Arrays.asList(target));
            spawnerApi.when(() -> SpawnerAPI.stringToCustomItemStack("BAD", 1)).thenReturn(itemStack);
            spawnerApi.when(() -> SpawnerAPI.getEntityType(itemStack)).thenReturn(EntityType.UNKNOWN);

            assertTrue(executor.runCommand(sender, new String[]{"Alex", "BAD"}));

            verify(sender).sendMessage("command.givespawner.invalid_type:BAD");
        }
    }

    @Test
    void runCommandGivesSpawnerToTargetPlayer() {
        SpawnerSilk plugin = mockPlugin();
        GiveSpawnerCommandExecutor executor = new GiveSpawnerCommandExecutor(plugin);
        Player sender = mock(Player.class);
        Player target = mock(Player.class);
        PlayerInventory inventory = mock(PlayerInventory.class);
        ItemStack itemStack = mock(ItemStack.class);

        when(sender.hasPermission("spawnersilk.givespawner")).thenReturn(true);
        when(target.getName()).thenReturn("Alex");
        when(target.getUniqueId()).thenReturn(UUID.randomUUID());
        when(target.getInventory()).thenReturn(inventory);
        when(itemStack.getItemMeta()).thenReturn(mock(org.bukkit.inventory.meta.ItemMeta.class));
        SpawnerSilk.playersUUID.put("Alex", target.getUniqueId().toString());

        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class);
             MockedStatic<SpawnerAPI> spawnerApi = mockStatic(SpawnerAPI.class)) {
            bukkit.when(Bukkit::getOnlinePlayers).thenReturn((Collection<? extends Player>) Arrays.asList(target));
            spawnerApi.when(() -> SpawnerAPI.stringToCustomItemStack("ZOMBIE", 2)).thenReturn(itemStack);
            spawnerApi.when(() -> SpawnerAPI.getEntityType(itemStack)).thenReturn(EntityType.ZOMBIE);

            assertTrue(executor.runCommand(sender, new String[]{"Alex", "ZOMBIE", "2"}));

            verify(inventory).addItem(itemStack);
            verify(sender).sendMessage("command.givespawner.success:Alex:ZOMBIE:2");
        }
    }

    private SpawnerSilk mockPlugin() {
        SpawnerSilk plugin = mock(SpawnerSilk.class);
        LocalizationManager localization = mock(LocalizationManager.class);
        when(plugin.getLocalization()).thenReturn(localization);
        when(localization.getMessage(org.mockito.ArgumentMatchers.anyString()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(localization.getMessage(eq("command.givespawner.usage.sps"), anyMap()))
                .thenReturn("command.givespawner.usage.sps");
        when(localization.getMessage(eq("command.givespawner.usage.direct"), anyMap()))
                .thenReturn("command.givespawner.usage.direct");
        when(localization.getMessage(eq("command.givespawner.invalid_player"), anyMap()))
                .thenAnswer(invocation -> "command.givespawner.invalid_player:" + invocation.<java.util.Map<String, String>>getArgument(1).get("player"));
        when(localization.getMessage(eq("command.givespawner.invalid_type"), anyMap()))
                .thenAnswer(invocation -> "command.givespawner.invalid_type:" + invocation.<java.util.Map<String, String>>getArgument(1).get("type"));
        when(localization.getMessage(eq("command.givespawner.success"), anyMap()))
                .thenAnswer(invocation -> {
                    java.util.Map<String, String> args = invocation.getArgument(1);
                    return "command.givespawner.success:" + args.get("player") + ":" + args.get("type") + ":" + args.get("amount");
                });
        when(localization.getMessage(eq("command.givespawner.no_permission"), anyMap()))
                .thenReturn("command.givespawner.no_permission");
        return plugin;
    }
}
