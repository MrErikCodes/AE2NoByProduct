# AGENTS.md

Guidance for AI agents (and humans) working in this repository. Read this before making changes. Keep it up to date when the build or release process changes.

## What this project is

**AE2 No Byproduct** is an Applied Energistics 2 add-on for Minecraft. It strips byproducts (secondary outputs) from AE2 processing patterns via a per-player toggle in the Pattern Encoding Terminal, a server/pack config, and a craftable Byproduct Remover item. Currently ships for **MC 1.20.1 / Forge / AE2 15.4.x**. Multi-loader and multi-version (Fabric, NeoForge, 1.21.1) is in progress via Architectury + Stonecutter.

## Build & dev commands

- `./gradlew build` builds the mod; the jar lands in `build/libs/`.
- `./gradlew runClient` launches a dev client (Forge 1.20.1). AE2 + GuideME are pulled automatically via CurseMaven, no manual mod drop needed.
- Java 17 is required; the Gradle wrapper auto-provisions a JDK 17 toolchain.

## Repository layout

- Source: `src/main/java/dev/erikcodes/ae2nobyproduct/` (see `CONTRIBUTING.md` for the package breakdown), resources in `src/main/resources/`.
- One server-side Mixin strips byproducts; client Mixins add the toolbar toggle; the Byproduct Remover item uses AE2's public pattern API.
- `.github/workflows/` holds CI and release automation.

## Style rules (enforced)

- **No em-dashes.** Do not use the em-dash character (Unicode U+2014, the long dash) anywhere in docs, comments, or strings. Use commas, colons, parentheses, or separate sentences instead. Check with `grep -rP '\x{2014}' --include='*.md' .` (it must return nothing).
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
6. The **`.github/workflows/release.yml`** workflow then builds and publishes the jar to the GitHub Release and CurseForge automatically.

### Pre-release checklist
- [ ] `./gradlew build` green, tests pass
- [ ] `mod_version` bumped to match the release tag (the release workflow fails if they differ)
- [ ] `CHANGELOG.md` updated
- [ ] `README.md` + `CURSEFORGE.md` aligned, no em-dashes
- [ ] Supported-versions tables current (`README.md`, `CURSEFORGE.md`)
- [ ] `release.yml` `game-versions` / `loaders` match what this jar actually supports

## When the build or targets change

- If you add a loader or MC version (Architectury/Stonecutter work), **update `.github/workflows/release.yml`**: the build command, the published `files` glob (multiple jars), and `loaders`. The `game-versions` value is read from `gradle.properties` automatically, so update `mc_version` there. Also update the supported-versions tables in `README.md` and `CURSEFORGE.md`.
- Each new MC/AE2 target needs the Mixin targets re-verified against that AE2 version (see `CONTRIBUTING.md`).

## Secrets the release needs (already configured)

- `CF_API_TOKEN` (secret): CurseForge API token. Configured.
- `MODRINTH_TOKEN` (secret): Modrinth token. Configured.
- The CurseForge project id (`1590300`) and Modrinth slug (`ae2-no-byproduct`) are set directly in `release.yml`; the published game version is read from `gradle.properties` (`mc_version`). Change them in `release.yml` only if the project itself changes.
