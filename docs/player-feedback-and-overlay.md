# Player Feedback and Overlay

SpawnerSilk can send player-facing messages during gameplay and display an overlay when looking at a spawner.

## Feedback Settings

The `feedback` section in `config.yml` controls which messages are shown:

```yaml
feedback:
  break-errors: true
  place-success: false
  interact-errors: true
  interact-success: false
```

## Break Feedback

When enabled, SpawnerSilk can warn players when:

- Silk Touch is required to break a spawner
- The pickaxe tier is too low
- The spawner type is blacklisted

## Place Feedback

When enabled, SpawnerSilk sends a success message after a spawner is placed.

It can also report a fallback if the stored spawner type is unknown and the item defaults to `PIG`.

## Interaction Feedback

When enabled, SpawnerSilk can notify players when:

- Spawn egg modification is disabled
- A spawner type has been changed successfully with a spawn egg

## Overlay

The overlay is controlled through the `overlay` section:

```yaml
overlay:
  enabled: true
  duration-seconds: 10
```

When enabled, players with the `spawnersilk.overlay` permission can see spawner details while looking at a spawner.

The overlay currently shows:

- Spawner type
- Spawn count
- Spawn range
- Max nearby entities
- Required player range
- Max spawn delay
- Min spawn delay

## Localization

All feedback and overlay lines are localized through the JSON files in `plugins/SpawnerSilk/localization/`.
