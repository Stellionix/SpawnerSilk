package me.crylonz.spawnersilk.command;

import me.crylonz.spawnersilk.SpawnerSilk;
import me.crylonz.spawnersilk.utils.LocalizationManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EditSpawnerCommandExecutorTest {

    @Test
    void runCommandReturnsFalseWithoutPermission() {
        SpawnerSilk plugin = mock(SpawnerSilk.class);
        EditSpawnerCommandExecutor executor = new EditSpawnerCommandExecutor(plugin);
        Player player = mock(Player.class);

        when(player.hasPermission("spawnersilk.editspawner")).thenReturn(false);

        assertFalse(executor.runCommand(player, new String[]{"delay", "10"}));
    }

    @Test
    void runCommandWarnsWhenPlayerIsNotLookingAtSpawner() {
        SpawnerSilk plugin = mock(SpawnerSilk.class);
        LocalizationManager localization = mock(LocalizationManager.class);
        EditSpawnerCommandExecutor executor = new EditSpawnerCommandExecutor(plugin);
        Player player = mock(Player.class);
        Block block = mock(Block.class);

        when(plugin.getLocalization()).thenReturn(localization);
        when(localization.getMessage("command.editspawner.must_target_spawner")).thenReturn("must_target");
        when(player.hasPermission("spawnersilk.editspawner")).thenReturn(true);
        when(player.getTargetBlockExact(10)).thenReturn(block);
        when(block.getType()).thenReturn(Material.DIRT);

        assertTrue(executor.runCommand(player, new String[]{"delay"}));
        verify(player).sendMessage("must_target");
    }

    @Test
    void runCommandUpdatesSpawnerDelayAndSendsLocalizedValue() {
        SpawnerSilk plugin = mock(SpawnerSilk.class);
        LocalizationManager localization = mock(LocalizationManager.class);
        EditSpawnerCommandExecutor executor = new EditSpawnerCommandExecutor(plugin);
        Player player = mock(Player.class);
        Block block = mock(Block.class);
        CreatureSpawner spawner = mock(CreatureSpawner.class);

        when(plugin.getLocalization()).thenReturn(localization);
        when(localization.getMessage(eq("command.editspawner.delay"), anyMap())).thenReturn("delay=20");
        when(player.hasPermission("spawnersilk.editspawner")).thenReturn(true);
        when(player.getTargetBlockExact(10)).thenReturn(block);
        when(block.getType()).thenReturn(Material.SPAWNER);
        when(block.getState()).thenReturn(spawner);
        when(spawner.getDelay()).thenReturn(20);

        assertTrue(executor.runCommand(player, new String[]{"delay", "20"}));
        verify(spawner).setDelay(20);
        verify(spawner).update();
        verify(block).setBlockData(spawner.getBlockData());
        verify(player).sendMessage("delay=20");
    }

    @Test
    void onCommandRejectsNonMatchingInvocationShape() {
        SpawnerSilk plugin = mock(SpawnerSilk.class);
        EditSpawnerCommandExecutor executor = new EditSpawnerCommandExecutor(plugin);

        assertFalse(executor.onCommand(mock(org.bukkit.command.CommandSender.class), mock(Command.class), "editspawner", new String[]{"delay"}));
    }
}
