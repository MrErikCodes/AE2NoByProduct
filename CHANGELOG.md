# Changelog

All notable changes to AE2 No Byproduct are documented here.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project adheres to [Semantic Versioning](https://semver.org/).

## [Unreleased]

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

[Unreleased]: https://github.com/AE2NoByproduct/AE2NoByproduct/compare/v0.2.0...HEAD
[0.2.0]: https://github.com/AE2NoByproduct/AE2NoByproduct/compare/v0.1.0...v0.2.0
[0.1.0]: https://github.com/AE2NoByproduct/AE2NoByproduct/releases/tag/v0.1.0
