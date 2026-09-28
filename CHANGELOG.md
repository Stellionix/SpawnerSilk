# Changelog

## 5.9.3

### Fixed
- Consume spawn eggs used from the off hand in Survival mode when egg consumption is enabled.
- Respect denied block interactions when changing spawner types, preserving the egg and spawner when access is blocked.
- Ignore ordinary eggs and other non-spawn-egg items when changing spawner types.

### Maintenance
- Retain the versioned distribution JAR from push CI runs for exact-commit validation.
