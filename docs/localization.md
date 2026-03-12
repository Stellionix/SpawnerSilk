# Localization

SpawnerSilk loads player-facing messages from JSON files stored in the plugin data folder.

## Language Selection

The active language is configured in `config.yml`:

```yaml
localization:
  language: en_us
```

SpawnerSilk then loads:

```text
plugins/SpawnerSilk/localization/<language>.json
```

## Default Behavior

- The plugin ships with `en_us.json`.
- On startup, SpawnerSilk ensures the default localization file exists in the data folder.
- If the configured language file is missing, the plugin falls back to `en_us.json`.
- Localization files are reloaded with `/sps reload`.

## File Format

Localization files use a flat JSON structure:

```json
{
  "command.sps.reload.success": "&a[SpawnerSilk] Plugin reloaded successfully!",
  "event.interact.changed": "&a[SpawnerSilk] Spawner changed to {type}"
}
```

## Color Codes and Placeholders

- Color codes use the Bukkit `&` format.
- Placeholders are written as flat tokens such as `{player}`, `{type}`, `{amount}`, or `{value}`.

Example:

```json
{
  "command.givespawner.success": "&a[SpawnerSilk] Gave {amount} {type} spawner(s) to {player}"
}
```

## Current Scope

Localization currently covers:

- Command messages
- Permission error messages
- Overlay text
- Player feedback for break, place, and interaction events

## Notes

- JSON keys should remain flat.
- Missing keys fall back to the key name itself, which helps surface mistakes during editing.
