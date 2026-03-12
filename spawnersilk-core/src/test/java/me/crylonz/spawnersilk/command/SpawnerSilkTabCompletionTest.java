package me.crylonz.spawnersilk.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SpawnerSilkTabCompletionTest {

    @Test
    void spsRootCompletionReturnsMainSubcommands() {
        SpawnerSilkTabCompletion completion = new SpawnerSilkTabCompletion();
        Player player = mock(Player.class);
        Command command = mock(Command.class);

        when(command.getName()).thenReturn("sps");

        List<String> result = completion.onTabComplete(player, command, "sps", new String[]{""});

        assertEquals(Arrays.asList("reload", "editspawner", "givespawner"), result);
    }

    @Test
    void spsGiveSpawnerSecondArgumentListsOnlinePlayers() {
        SpawnerSilkTabCompletion completion = new SpawnerSilkTabCompletion();
        Player sender = mock(Player.class);
        Player steve = mock(Player.class);
        Player alex = mock(Player.class);
        Command command = mock(Command.class);

        when(command.getName()).thenReturn("sps");
        when(sender.hasPermission("spawnersilk.givespawner")).thenReturn(true);
        when(steve.getName()).thenReturn("Steve");
        when(alex.getName()).thenReturn("Alex");

        try (MockedStatic<Bukkit> bukkit = org.mockito.Mockito.mockStatic(Bukkit.class)) {
            bukkit.when(Bukkit::getOnlinePlayers).thenReturn((Collection<? extends Player>) Arrays.asList(steve, alex));

            List<String> result = completion.onTabComplete(sender, command, "sps", new String[]{"givespawner", ""});

            assertEquals(Arrays.asList("Steve", "Alex"), result);
        }
    }

    @Test
    void directGiveSpawnerCommandSecondArgumentContainsKnownEntityTypes() {
        SpawnerSilkTabCompletion completion = new SpawnerSilkTabCompletion();
        Player player = mock(Player.class);
        Command command = mock(Command.class);

        when(command.getName()).thenReturn("givespawner");
        when(player.hasPermission("spawnersilk.givespawner")).thenReturn(true);

        List<String> result = completion.onTabComplete(player, command, "givespawner", new String[]{"Steve", ""});

        assertTrue(result.contains("ZOMBIE"));
        assertTrue(result.contains("CREEPER"));
    }

    @Test
    void nonPlayerSenderGetsNoCompletion() {
        SpawnerSilkTabCompletion completion = new SpawnerSilkTabCompletion();
        CommandSender sender = mock(CommandSender.class);
        Command command = mock(Command.class);

        when(command.getName()).thenReturn("sps");

        List<String> result = completion.onTabComplete(sender, command, "sps", new String[]{""});

        assertTrue(result.isEmpty());
    }
}
