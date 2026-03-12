# Integrations

SpawnerSilk supports optional integrations through soft dependencies.

## ShopGUI+

SpawnerSilk can hook into ShopGUI+ when the plugin is installed on the server.

Behavior:

- ShopGUI+ is optional
- If ShopGUI+ is present, SpawnerSilk enables its integration during startup
- If the hook fails, SpawnerSilk logs that ShopGUI+ support has been disabled

This means the plugin still runs without ShopGUI+, but the related integration features are only available when the dependency is present and loads correctly.

## Notes for Server Owners

- No extra setup is required for basic SpawnerSilk usage
- Optional integrations should be treated as enhancements, not hard requirements
- Startup logs are the best place to confirm whether an integration was enabled successfully
