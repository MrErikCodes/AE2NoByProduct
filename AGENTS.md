# AGENTS.md

Guidance for AI agents (and humans) working in this repository. Read this before making changes. Keep it up to date when the build or release process changes.

## What this project is

**AE2 No Byproduct** is an Applied Energistics 2 add-on for Minecraft. It strips byproducts (secondary outputs) from AE2 processing patterns via a per-player toggle in the Pattern Encoding Terminal, a server/pack config, and a craftable Byproduct Remover item. Ships for **MC 1.20.1 on Forge and Fabric / AE2 15.4.x**, from one shared codebase via Architectury. Multi-version (**1.21.1 on NeoForge**) is in progress via Stonecutter. Note: 1.21.1 is NeoForge only, because AE2 has no Fabric build for 1.21.1.

## Build & dev commands

- `./gradlew build` builds every module; the per-loader jars land in `forge/build/libs/` and `fabric/build/libs/`, named `ae2nobyproduct-<loader>-<mcversion>-<modversion>.jar`.
- `./gradlew :forge:runClient` / `./gradlew :fabric:runClient` launch a dev client for each loader. AE2 + GuideME are resolved automatically; the Fabric dev runtime also pulls Team Reborn Energy (a nested dependency AE2-fabric needs that loom does not surface on its own).
- Java 17 is required; the Gradle wrapper auto-provisions a JDK 17 toolchain.

## Repository layout

- Multi-loader Architectury layout: `common/` holds all shared logic (stripping mixin, networking, item, toolbar button, config/persistence seams); `forge/` and `fabric/` hold only the loader-specific leaves (entry point, config source, per-player persistence). See `CONTRIBUTING.md` for the package breakdown. **Never share a Java package between `common/` and a loader module** (Forge/NeoForge enforce JPMS and reject split packages).
- One server-side Mixin strips byproducts; client Mixins add the toolbar toggle; the Byproduct Remover item uses AE2's pattern API. All live in `common/` and run on both loaders.
- `.github/workflows/` holds CI and release automation.

## Style rules (enforced)

- **No em-dashes.** Do not use the em-dash character (Unicode U+2014, the long dash) anywhere in docs, comments, or strings. Use commas, colons, parentheses, or separate sentences instead. Check with `grep -rP '\x{2014}' --include='*.md' --include='*.java' --include='*.json' --include='*.yml' --include='*.yaml' --include='*.properties' .` (it must return nothing).
- **Keep `README.md` and `CURSEFORGE.md` aligned.** They describe the same mod for different audiences; any user-facing change (features, recipe, config options, supported versions) must be reflected in BOTH, with no contradictions.
- Match the existing code style (4-space indent). Keep Mixins minimal.

## Versioning

- The version lives in `gradle.properties` as `mod_version` and follows **semver** (`MAJOR.MINOR.PATCH`).
- A release tag is `v<mod_version>` (e.g. `v0.1.0`). The git tag and `gradle.properties` must match.
- Bump `mod_version` for every release: PATCH for fixes, MINOR for new features, MAJOR for breaking changes.

## Changelog

- `CHANGELOG.md` follows [Keep a Changelog](https://keepachangelog.com/). Add an entry under `## [Unreleased]` for every user-facing change as you make it.
- On release, rename `[Unreleased]` to the new version + date and start a fresh `[Unreleased]` section.

## Release process (do NOT skip steps)

See `RELEASE.md` for the full walkthrough. In short:

1. Ensure `main` is green: `./gradlew build` passes and tests pass.
2. Confirm `README.md` and `CURSEFORGE.md` are aligned and em-dash-free.
3. Bump `mod_version` in `gradle.properties`.
4. Move `CHANGELOG.md`'s `[Unreleased]` items into a new `## [x.y.z] - YYYY-MM-DD` section.
5. Commit, then create a GitHub Release with tag `vx.y.z` (use "Generate release notes" for the body).
6. The **`.github/workflows/release.yml`** workflow then builds and publishes BOTH loader jars (forge + fabric) to the GitHub Release, CurseForge, and Modrinth automatically.

### Pre-release checklist
- [ ] `./gradlew build` green, tests pass
- [ ] `mod_version` bumped to match the release tag (the release workflow fails if they differ)
- [ ] `CHANGELOG.md` updated
- [ ] `README.md` + `CURSEFORGE.md` aligned, no em-dashes
- [ ] Supported-versions tables current (`README.md`, `CURSEFORGE.md`)
- [ ] `release.yml` `game-versions` / `loaders` match what this jar actually supports

## When the build or targets change

- If you add a loader or MC version (Architectury/Stonecutter work), **update `.github/workflows/release.yml`**: the build command, the published `files` glob (multiple jars), and `loaders`. The `game-versions` value is read from `gradle.properties` automatically, so update `minecraft_version` there. Also update the supported-versions tables in `README.md` and `CURSEFORGE.md`.
- Each new MC/AE2 target needs the Mixin targets re-verified against that AE2 version (see `CONTRIBUTING.md`).

## Secrets the release needs (already configured)

- `CF_API_TOKEN` (secret): CurseForge API token. Configured.
- `MODRINTH_TOKEN` (secret): Modrinth token. Configured.
- The CurseForge project id (`1590300`) and Modrinth slug (`ae2-no-byproduct`) are set directly in `release.yml`; the published game version is read from `gradle.properties` (`minecraft_version`). Change them in `release.yml` only if the project itself changes.
