# Troubleshooting

This page covers the most common issues you may run into while using SpawnerSilk.

## The Plugin Does Not Start

Check the following first:

- Your server version is supported
- Your Java version matches the plugin requirements
- The server console does not show dependency or API errors

## Configuration Changes Are Not Applied

Run:

```text
/sps reload
```

SpawnerSilk reloads both the main configuration and localization files through this command.

## Commands Do Not Work

Check:

- The player has the required permission
- The command syntax is correct
- The target spawner or player exists

## Spawners Do Not Drop

Review these settings in `config.yml`:

- `mining.require-silk-touch-to-break`
- `mining.require-silk-touch-to-drop`
- `mining.required-pickaxe-tier`
- `drops.spawner-chance`
- `drops.allow-in-creative`
- `restrictions.blacklist`

## Still Need Help?

Ask for help on the project Discord or open an issue on GitHub.
