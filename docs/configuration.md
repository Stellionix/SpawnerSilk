# Configuration

Make sure SpawnerSilk is already [installed](installation.md) before editing the configuration.

SpawnerSilk uses a structured `config.yml` with sections grouped by feature. After any change, run `/sps reload`.

## Main Sections

| Section | Purpose |
| --- | --- |
| `localization` | Selects the language file loaded from the `localization/` folder |
| `updates` | Controls automatic update downloads |
| `mining` | Defines Silk Touch and pickaxe requirements |
| `drops` | Controls drop mode, drop chances, and inventory behavior |
| `interaction` | Controls spawn egg interaction with spawners |
| `experience` | Enables or disables XP drops from mined spawners |
| `overlay` | Configures the spawner overlay display |
| `feedback` | Toggles player-facing messages for break, place, and interact actions |
| `restrictions` | Defines restricted spawner types, such as the blacklist |

## Example

```yaml
localization:
  language: en_us

mining:
  require-silk-touch-to-break: false
  require-silk-touch-to-drop: true
  required-pickaxe-tier: 5

drops:
  mode: 0
  spawner-chance: 100
  to-inventory: false
```

## Important Notes

- Missing keys are restored from the default config during updates.
- The plugin preserves user values while rebuilding the file from the bundled template.

## Common Settings

| Key | Type | Default | Description |
| --- | --- | --- | --- |
| `localization.language` | string | `en_us` | Language file to load |
| `updates.auto-download` | boolean | `true` | Automatically download supported updates |
| `mining.require-silk-touch-to-break` | boolean | `false` | Requires Silk Touch to break a spawner |
| `mining.require-silk-touch-to-drop` | boolean | `true` | Requires Silk Touch for a spawner item to drop |
| `mining.required-pickaxe-tier` | integer | `5` | Minimum pickaxe tier required to collect a spawner |
| `drops.mode` | integer | `0` | Drop a typed spawner, or a base spawner plus egg |
| `drops.spawner-chance` | integer | `100` | Chance for a spawner to drop when broken |
| `drops.egg-chance` | integer | `100` | Chance for a spawn egg to drop in mode `1` |
| `drops.explosion-chance` | integer | `10` | Chance for a spawner to drop after an explosion |
| `drops.to-inventory` | boolean | `false` | Sends drops directly to inventory |
| `drops.allow-in-creative` | boolean | `false` | Allows spawner drops in Creative mode |
| `interaction.allow-egg-modification` | boolean | `true` | Allows spawn eggs to retune spawners |
| `interaction.consume-egg` | boolean | `true` | Consumes the spawn egg after use |
| `experience.drop-from-spawners` | boolean | `false` | Drops XP when a spawner is mined |
| `overlay.enabled` | boolean | `true` | Enables the spawner overlay |
| `overlay.duration-seconds` | integer | `10` | Overlay display duration |
| `feedback.break-errors` | boolean | `true` | Shows break-related error messages |
| `feedback.place-success` | boolean | `false` | Shows a message after placing a spawner |
| `feedback.interact-errors` | boolean | `true` | Shows interaction-related error messages |
| `feedback.interact-success` | boolean | `false` | Shows a message after changing a spawner |
| `restrictions.blacklist` | list | `["BOAT_SPAWNER"]` | Spawner types that can never be collected |
