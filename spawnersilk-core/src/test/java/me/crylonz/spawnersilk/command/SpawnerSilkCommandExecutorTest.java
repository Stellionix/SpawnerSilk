package me.crylonz.spawnersilk.command;

import me.crylonz.spawnersilk.SpawnerSilk;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class SpawnerSilkCommandExecutorTest {

    @Test
    void reloadSubcommandReloadsPluginAndNotifiesPlayer() {
        SpawnerSilk plugin = mock(SpawnerSilk.class);
        GiveSpawnerCommandExecutor giveSpawner = mock(GiveSpawnerCommandExecutor.class);
        EditSpawnerCommandExecutor editSpawner = mock(EditSpawnerCommandExecutor.class);
        SpawnerSilkCommandExecutor executor = new SpawnerSilkCommandExecutor(plugin, giveSpawner, editSpawner);
        Player player = mock(Player.class);
        Command command = mock(Command.class);

        when(command.getName()).thenReturn("sps");
        when(player.hasPermission("spawnerSilk.reload")).thenReturn(true);

        boolean result = executor.onCommand(player, command, "sps", new String[]{"reload"});

        assertTrue(result);
        verify(plugin).reloadConfig();
        verify(plugin).registerConfig();
        verify(player).sendMessage(argThat((String message) -> message.contains("Plugin reloaded successfully")));
        verifyNoSubcommandsWereCalled(giveSpawner, editSpawner);
    }

    @Test
    void editSpawnerSubcommandDelegatesWithTrimmedArgs() {
        SpawnerSilk plugin = mock(SpawnerSilk.class);
        GiveSpawnerCommandExecutor giveSpawner = mock(GiveSpawnerCommandExecutor.class);
        EditSpawnerCommandExecutor editSpawner = mock(EditSpawnerCommandExecutor.class);
        SpawnerSilkCommandExecutor executor = new SpawnerSilkCommandExecutor(plugin, giveSpawner, editSpawner);
        Player player = mock(Player.class);
        Command command = mock(Command.class);

        when(command.getName()).thenReturn("sps");
        when(editSpawner.runCommand(player, new String[]{"delay", "20"})).thenReturn(true);

        boolean result = executor.onCommand(player, command, "sps", new String[]{"editspawner", "delay", "20"});

        assertTrue(result);
        verify(editSpawner).runCommand(player, new String[]{"delay", "20"});
        verify(giveSpawner, never()).runCommand(any(), any());
    }

    @Test
    void unknownSubcommandShowsErrorToPlayer() {
        SpawnerSilk plugin = mock(SpawnerSilk.class);
        GiveSpawnerCommandExecutor giveSpawner = mock(GiveSpawnerCommandExecutor.class);
        EditSpawnerCommandExecutor editSpawner = mock(EditSpawnerCommandExecutor.class);
        SpawnerSilkCommandExecutor executor = new SpawnerSilkCommandExecutor(plugin, giveSpawner, editSpawner);
        Player player = mock(Player.class);
        Command command = mock(Command.class);

        when(command.getName()).thenReturn("sps");

        boolean result = executor.onCommand(player, command, "sps", new String[]{"unknown"});

        assertTrue(result);
        verify(player).sendMessage(argThat((String message) -> message.contains("Unknown command")));
        verifyNoSubcommandsWereCalled(giveSpawner, editSpawner);
    }

    @Test
    void nonPlayerCannotExecutePlayerOnlySubcommands() {
        SpawnerSilk plugin = mock(SpawnerSilk.class);
        GiveSpawnerCommandExecutor giveSpawner = mock(GiveSpawnerCommandExecutor.class);
        EditSpawnerCommandExecutor editSpawner = mock(EditSpawnerCommandExecutor.class);
        SpawnerSilkCommandExecutor executor = new SpawnerSilkCommandExecutor(plugin, giveSpawner, editSpawner);
        Command command = mock(Command.class);

        when(command.getName()).thenReturn("sps");

        boolean result = executor.onCommand(mock(org.bukkit.command.CommandSender.class), command, "sps", new String[]{"givespawner", "Alex"});

        assertTrue(result);
        verifyNoSubcommandsWereCalled(giveSpawner, editSpawner);
    }

    private void verifyNoSubcommandsWereCalled(GiveSpawnerCommandExecutor giveSpawner,
                                               EditSpawnerCommandExecutor editSpawner) {
        verify(giveSpawner, never()).runCommand(any(), any());
        verify(editSpawner, never()).runCommand(any(), any());
    }
}
