# Changelog

All notable changes to AE2 No Byproduct are documented here.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project adheres to [Semantic Versioning](https://semver.org/).

## [Unreleased]

## [0.3.0] - 2026-06-29

### Added
- **Minecraft 1.21.1 support on NeoForge** (against Applied Energistics 2 19.2.x), built from the same single codebase. 1.21.1 is NeoForge only, because AE2 has no Fabric or Forge build for 1.21.1.
- **`/ae2nobyproduct strip-all` command** (operators, permission level 2): strips byproducts from every Pattern Provider in the AE2 network of the block you are looking at, covering both block and cable-part Pattern Providers. Two-step for safety: the first run previews how many patterns would change (no changes), and a second run on that same network within 30 seconds applies it.

### Changed
- Migrated the build to the **Stonecraft** Gradle plugin (Stonecutter + Architectury Loom): the old `common` / `forge` / `fabric` Architectury subprojects are replaced by a single flat `src/` tree, where each loader and Minecraft version is a Stonecutter node. Build everything with `./gradlew chiseledBuild` (or `chiseledBuildAndCollect`) and run the active version with `./gradlew runActive`.
- **Unified the config to a single JSON file on every loader** (`config/ae2nobyproduct.json`). On Forge the config moved from `.toml` to `.json`; the options, defaults, and keys are unchanged. If you customized the Forge `.toml` config, re-apply your values in the new `.json` file.
- The per-player toggle now persists via vanilla `SavedData` (keyed by player UUID) on every loader, replacing the loader-specific persistence used before. It still survives relog, server restart, and death.
- Release jars are now named `ae2nobyproduct-<loader>-<modversion>+mc<mcversion>.jar` (for example `ae2nobyproduct-neoforge-0.3.0+mc1.21.1.jar`), and publishing is handled by Stonecraft's mod-publish-plugin in a single `./gradlew chiseledPublishMods` step.

## [0.2.0] - 2026-06-28

### Added
- **Fabric support (MC 1.20.1).** The full feature set now runs on Fabric as well as Forge, from a single shared codebase (Architectury): the per-player toggle, byproduct stripping, the toolbar button, the Byproduct Remover item, and the server config. Per-player toggle state persists across relog, server restart, and death on both loaders.

### Changed
- Internal restructure to a multi-loader layout (`common` / `forge` / `fabric`). All gameplay logic now lives in `common` and is shared by both loaders; only the config source and per-player persistence are loader-specific. No change to in-game behavior on Forge.
- The Forge config moved from the per-world `serverconfig/` folder to the global `config/` folder (`config/ae2nobyproduct.toml`), matching Fabric (`config/ae2nobyproduct.json`) and most other mods. If you customized the config on 0.1.0, re-apply your values in the new file.
- Release jars now include the loader and Minecraft version in their filename (for example `ae2nobyproduct-fabric-1.20.1-0.2.0.jar`) so the per-loader downloads are unambiguous.

## [0.1.0] - 2026-06-27

### Added
- Per-player toggle button in the AE2 Pattern Encoding Terminal that strips byproducts from processing patterns at encode time, keeping only the first output. Server-authoritative, and the choice is persisted across relog, server restart, and death.
- Server config: `enableFeature`, `allowPlayerToggle`, `defaultStrip`, `consumeOnUse`, `showMessages`. Pack makers can force byproduct stripping for everyone with no per-player UI.
- Byproduct Remover item: shift + right-click a Pattern Provider to strip byproducts from every processing pattern stored inside it. Works on base AE2 providers and add-on providers (tested with Extended AE and MEGA Cells).
- Crafting recipe for the Byproduct Remover: 16k Storage Component + Blank Pattern + Crafting Unit (shapeless).

[Unreleased]: https://github.com/AE2NoByproduct/AE2NoByproduct/compare/v0.3.0...HEAD
[0.3.0]: https://github.com/AE2NoByproduct/AE2NoByproduct/compare/v0.2.0...v0.3.0
[0.2.0]: https://github.com/AE2NoByproduct/AE2NoByproduct/compare/v0.1.0...v0.2.0
[0.1.0]: https://github.com/AE2NoByproduct/AE2NoByproduct/releases/tag/v0.1.0
