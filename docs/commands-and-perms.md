# Commands and Permissions

Make sure SpawnerSilk is already [installed](installation.md).

## Commands

| Command | Permission | Description |
| --- | --- | --- |
| `/givespawner <player> <type> [amount]` | `spawnersilk.givespawner` | Gives a spawner to a target player |
| `/editspawner <property> [value]` | `spawnersilk.editspawner` | Edits the spawner block you are looking at |
| `/sps reload` | `spawnersilk.reload` | Reloads the plugin configuration and localization |

## General Permission

| Permission | Description |
| --- | --- |
| `spawnersilk.minespawner` | Allows players to mine spawners |
| `spawnersilk.overlay` | Allows players to view spawner overlay information |

## `/editspawner` Properties

To use `/editspawner`, you must be looking directly at a spawner block.

Supported properties:

- `spawnRange`
- `spawnCount`
- `maxNearbyEntities`
- `requiredPlayerRange`
- `delay`
- `maxSpawnDelay`
- `minSpawnDelay`

Example:

```text
/editspawner requiredPlayerRange 5
```

This makes the spawner active when a player is within five blocks.

## Notes

- Command feedback is localized through the files in `localization/`.