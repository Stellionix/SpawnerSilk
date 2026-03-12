<h1 align="center">
  <img src="spawnersilk-logo.png" alt="SpawnerSilk" width="800" /><br>
</h1>

<h2 align="center">
  <img src="http://cf.way2muchnoise.eu/full_322295_downloads.svg" alt="Downloads"/>
  <img src="https://img.shields.io/github/last-commit/Stellionix/SpawnerSilk" alt="Last commit"/>
  <a href="https://github.com/Stellionix/SpawnerSilk/actions/workflows/ci.yml"><img src="https://github.com/Stellionix/SpawnerSilk/actions/workflows/ci.yml/badge.svg" alt="CI"/></a>
  <a href="https://stellionix.github.io/SpawnerSilk/"><img src="https://img.shields.io/badge/docs-online-blue" alt="Docs"/></a>
  <img src="https://img.shields.io/github/license/Stellionix/SpawnerSilk" alt="License"/>
</h2>

SpawnerSilk lets players mine and manage mob spawners on Bukkit-based servers.

It is designed for Minecraft Java Edition servers and primarily targets Bukkit, Spigot, Paper, and compatible forks.

## Features

- Mine spawners with configurable Silk Touch and pickaxe requirements
- Choose how spawners drop, including direct spawner drops or spawner plus spawn egg mode
- Configure explosion drops, inventory delivery, Creative mode behavior, and XP rewards
- Change spawner types with spawn eggs
- Show an in-game overlay with spawner details
- Localize plugin messages through JSON language files
- Use admin commands to give and edit spawners
- Enable optional ShopGUI+ integration

## Compatibility

- Minecraft Java Edition
- Bukkit API `1.13+`
- Tested against modern server software such as Bukkit, Spigot, Paper, Purpur, and similar forks

## Download

- [CurseForge](https://www.curseforge.com/minecraft/bukkit-plugins/spawnersilk)
- [BukkitDev](https://dev.bukkit.org/projects/spawnersilk)
- [GitHub](https://github.com/Stellionix/SpawnerSilk)

## Quick Start

1. Download the latest SpawnerSilk jar.
2. Place it in your server's `plugins/` directory.
3. Start the server.
4. Review the generated configuration and localization files if needed.

Expected startup log:

```text
[SpawnerSilk] Enabling SpawnerSilk vX.X.X
```

## Commands

Main commands:

- `/givespawner <player> <type> [amount]`
- `/editspawner <property> [value]`
- `/sps reload`

The full command list and permission nodes are documented here:

- [Commands and Permissions](https://stellionix.github.io/SpawnerSilk/commands-and-perms/)

## Documentation

The official documentation is available at:

- [stellionix.github.io/SpawnerSilk](https://stellionix.github.io/SpawnerSilk/)

Useful pages:

- [Installation](https://stellionix.github.io/SpawnerSilk/installation/)
- [Configuration](https://stellionix.github.io/SpawnerSilk/configuration/)
- [Localization](https://stellionix.github.io/SpawnerSilk/localization/)
- [Player Feedback and Overlay](https://stellionix.github.io/SpawnerSilk/player-feedback-and-overlay/)
- [Integrations](https://stellionix.github.io/SpawnerSilk/integrations/)

## Statistics

<img align="center" src="https://bstats.org/signatures/bukkit/Spawnersilk.svg" alt="bStats"/>

More statistics are available on [bStats](https://bstats.org/plugin/bukkit/SpawnerSilk/5536).

## Contributing

Contribution guidelines are available in [CONTRIBUTING.md](CONTRIBUTING.md).
