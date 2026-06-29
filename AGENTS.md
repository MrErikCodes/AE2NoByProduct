# AGENTS.md

Guidance for AI agents (and humans) working in this repository. Read this before making changes. Keep it up to date when the build or release process changes.

## What this project is

**AE2 No Byproduct** is an Applied Energistics 2 add-on for Minecraft. It strips byproducts (secondary outputs) from AE2 processing patterns via a per-player toggle in the Pattern Encoding Terminal, a server/pack config, a craftable Byproduct Remover item, and an operator `/ae2nobyproduct strip-all` command (network-wide cleanup). Ships for **MC 1.20.1 on Forge and Fabric (AE2 15.4.x)** and **MC 1.21.1 on NeoForge (AE2 19.2.x)**, all from one shared codebase. Note: 1.21.1 is NeoForge only, because AE2 has no Fabric or Forge build for 1.21.1. The build uses the Stonecraft Gradle plugin (Stonecutter + Architectury Loom) over a single flat source tree; each loader/version is a Stonecutter node.

## Build & dev commands

- `./gradlew chiseledBuild` builds every loader and version. `./gradlew chiseledBuildAndCollect` does the same and gathers all jars into `build/libs/`. Jars are named `ae2nobyproduct-<loader>-<modversion>+mc<mcversion>.jar` (for example `ae2nobyproduct-neoforge-0.2.0+mc1.21.1.jar`); each node also keeps its own under `versions/<node>/build/libs/`.
- `./gradlew runActive` launches a dev client for the currently active version. Switch the active version with the Stonecutter task, e.g. `./gradlew "Set active project to 1.21.1-neoforge"`. AE2 + GuideME are resolved automatically; the Fabric dev runtime also pulls Team Reborn Energy (a nested dependency AE2-fabric needs that loom does not surface on its own).
- The Gradle toolchain auto-provisions both Java 17 (1.20.1) and Java 21 (1.21.1).

## Repository layout

- Single flat source tree: one `src/` holds all the logic (stripping mixin, networking, item, toolbar button, config/persistence). There are no more `common/`, `forge/`, or `fabric/` subprojects. Loaders and Minecraft versions are Stonecutter nodes; the per-loader/per-version differences are kept in the same files behind Stonecutter `//?` preprocessor comments. See `CONTRIBUTING.md` for the package breakdown.
- Loader-specific code is minimal: the single `//?`-guarded entry point, plus the AE2 mixin descriptor, networking, and per-player SavedData differences, all guarded inline with Stonecutter `//?` comments.
- One server-side Mixin strips byproducts; client Mixins add the toolbar toggle; the Byproduct Remover item uses AE2's pattern API. All run on every loader.
- `.github/workflows/` holds CI and release automation. Per-version dependency coordinates live in `versions/dependencies/<mc>.properties`.

## Style rules (enforced)

- **No em-dashes.** Do not use the em-dash character (Unicode U+2014, the long dash) anywhere in docs, comments, or strings. Use commas, colons, parentheses, or separate sentences instead. Check with `grep -rP '\x{2014}' --include='*.md' --include='*.java' --include='*.json' --include='*.yml' --include='*.yaml' --include='*.properties' .` (it must return nothing).
- **Keep `README.md` and `CURSEFORGE.md` aligned.** They describe the same mod for different audiences; any user-facing change (features, recipe, config options, supported versions) must be reflected in BOTH, with no contradictions.
- Match the existing code style (4-space indent). Keep Mixins minimal.

## Versioning

- The version lives in `gradle.properties` as `mod.version` and follows **semver** (`MAJOR.MINOR.PATCH`).
- A release tag is `v<mod.version>` (e.g. `v0.1.0`). The git tag and `gradle.properties` must match.
- Bump `mod.version` for every release: PATCH for fixes, MINOR for new features, MAJOR for breaking changes.

## Changelog

- `CHANGELOG.md` follows [Keep a Changelog](https://keepachangelog.com/). Add an entry under `## [Unreleased]` for every user-facing change as you make it.
- On release, rename `[Unreleased]` to the new version + date and start a fresh `[Unreleased]` section.

## Release process (do NOT skip steps)

See `RELEASE.md` for the full walkthrough. In short:

1. Ensure `main` is green: `./gradlew chiseledBuild` passes and tests pass.
2. Confirm `README.md` and `CURSEFORGE.md` are aligned and em-dash-free.
3. Bump `mod.version` in `gradle.properties`.
4. Move `CHANGELOG.md`'s `[Unreleased]` items into a new `## [x.y.z] - YYYY-MM-DD` section.
5. Merge those changes to `main` (via PR or a direct push).
6. The version bump auto-triggers **`.github/workflows/release.yml`**: it tags `vx.y.z`, runs `chiseledBuildAndCollect`, creates the GitHub Release (jars + the `## [x.y.z]` changelog section as the body), and runs `chiseledPublishMods` to upload each loader/version jar to CurseForge and Modrinth. There is no manual "draft a release" step (see "How publishing works" in `RELEASE.md`).

### Pre-release checklist
- [ ] `./gradlew chiseledBuild` green, tests pass
- [ ] `mod.version` (in `gradle.properties`) bumped to the new version (the workflow tags `v<mod.version>` on merge and skips if that tag already exists)
- [ ] `CHANGELOG.md` finalized: `[Unreleased]` moved into a `## [x.y.z]` section (the workflow uses that section as the GitHub Release notes)
- [ ] `README.md` + `CURSEFORGE.md` aligned, no em-dashes
- [ ] Supported-versions tables current (`README.md`, `CURSEFORGE.md`)
- [ ] Every shipped loader/version has a Stonecutter node and a `versions/dependencies/<mc>.properties`

## When the build or targets change

- If you add a loader or MC version, add a **Stonecutter node** for it (in `settings.gradle.kts`) and a **`versions/dependencies/<mc>.properties`** file with that target's coordinates (MC, loader, AE2, GuideME). `chiseledBuild` and `chiseledPublishMods` then pick it up automatically, so no per-loader publish steps need to be added by hand. Also update the supported-versions tables in `README.md` and `CURSEFORGE.md`.
- Each new MC/AE2 target needs the Mixin targets re-verified against that AE2 version (see `CONTRIBUTING.md`).

## Secrets the release needs (already configured)

`chiseledPublishMods` reads its targets from the CI environment:

- `MODRINTH_TOKEN` (secret) and `MODRINTH_ID`: Modrinth token and project. Configured.
- `CURSEFORGE_TOKEN` (from the `CF_API_TOKEN` secret), `CURSEFORGE_ID` (`1590300`), and `CURSEFORGE_SLUG` (`ae2-no-byproduct`): CurseForge token, project id, and slug. Configured.
- `DO_PUBLISH`: Stonecraft uses dry-run unless this is `false`; `release.yml` sets `DO_PUBLISH=false` for real uploads.

Each loader/version's loader, MC version, and AE2 dependency are derived from the Stonecutter nodes and `versions/dependencies/<mc>.properties`, so the published files are always tagged correctly. Change the project id/slug only if the project itself changes.
