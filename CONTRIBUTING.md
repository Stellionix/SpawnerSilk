# Contributing

Thanks for contributing to SpawnerSilk.

## Before You Start

- Open an issue or discussion first if you plan to work on a larger change
- Keep changes focused and easy to review
- Follow the existing coding style and project structure

## Development Notes

- Main plugin code lives in `spawnersilk-core/`
- Documentation lives in `docs/`
- Localization files live in `spawnersilk-core/src/main/resources/localization/`

## Recommended Workflow

1. Create a branch from `main`
2. Make your changes
3. Add or update tests when behavior changes
4. Run the relevant checks locally
5. Open a pull request with a clear description

## Local Checks

Run tests:

```powershell
./gradlew test
```

Build the plugin jar:

```powershell
./gradlew shadowJar
```

Build the documentation:

```powershell
py -m mkdocs build --strict
```

If Gradle does not work with your default Java installation, use JDK 17 for local validation.

## Pull Requests

Please include:

- A short summary of the change
- The reason for the change
- Any relevant screenshots, logs, or config examples when applicable

Small fixes are welcome. Tests and documentation updates are appreciated when they help keep behavior clear.
