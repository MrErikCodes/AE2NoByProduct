# Contributing to AE2 No Byproduct

Thank you for your interest in contributing! This document covers everything you need to get started.

---

## Development Environment

**Prerequisites**

- **Java 17 and Java 21** (the Gradle build uses a toolchain resolver: if you don't have these JDKs installed locally, Gradle will automatically download and provision them; you just need a working internet connection on the first build). 1.20.1 builds on Java 17, 1.21.1 on Java 21.
- **Git**

**Clone and build**

```bash
git clone https://github.com/AE2NoByproduct/AE2NoByproduct.git
cd AE2NoByproduct
./gradlew chiseledBuild
```

`chiseledBuild` builds every loader and version. Add `./gradlew chiseledBuildAndCollect` to gather all jars into `build/libs/`. The output jars are named `ae2nobyproduct-<loader>-<modversion>+mc<mcversion>.jar` (for example `ae2nobyproduct-neoforge-0.2.0+mc1.21.1.jar`); each Stonecutter node also keeps its own under `versions/<node>/build/libs/`.

**Launch the dev client (in-game testing)**

```bash
./gradlew runActive
```

`runActive` starts a client for the currently active version. Switch the active version with the Stonecutter task, for example:

```bash
./gradlew "Set active project to 1.21.1-neoforge"
./gradlew "Set active project to 1.20.1-fabric"
```

Applied Energistics 2 and GuideME are resolved automatically, so they load with no manual steps. The Fabric dev runtime additionally pulls the Team Reborn Energy API, a nested dependency AE2-fabric needs that loom does not surface on its own.

---

## Project Layout

This is a **single flat source tree** built with the Stonecraft Gradle plugin (Stonecutter + Architectury Loom). There are no `common/` / `forge/` / `fabric/` subprojects: every loader and Minecraft version is a Stonecutter "node" (`1.20.1-forge`, `1.20.1-fabric`, `1.21.1-neoforge`) compiled from the same `src/`. The few loader/version differences are kept inline in the same files behind Stonecutter `//?` preprocessor comments.

```text
src/main/java/dev/erikcodes/ae2nobyproduct/   # the whole mod; compiled for every node
  ├── AE2NoByproduct.java          # the only loader-specific class: //?-guarded entry point per loader
  ├── CommonMod.java               # shared init (registers networking + items)
  ├── client/                      # toolbar button + cached client state (ClientByproductState)
  ├── core/                        # EffectiveState, ByproductService, ByproductConfig (JSON config), ByproductStore (SavedData)
  ├── item/                        # Byproduct Remover item
  ├── network/                     # networking (set-toggle C2S, state-sync S2C); loader differences are //?-guarded
  ├── registry/                    # item + creative-tab registration (Architectury DeferredRegister)
  └── mixin/
      ├── PatternEncodingTermMenuMixin.java          # server-side: strips byproducts + sync trigger
      └── client/                                    # client-side toolbar button
          ├── AEBaseScreenInvoker.java               # @Invoker for AE2's addToLeftToolbar
          └── PatternEncodingTermScreenMixin.java
src/main/resources/                # bundled into every node's jar
  ├── ae2nobyproduct.mixins.json / ae2nobyproduct.client.mixins.json   # mixin descriptors (//?-guarded where AE2 differs)
  ├── fabric.mod.json / META-INF/mods.toml                              # per-loader mod metadata
  └── assets/ + data/              # lang, item model, texture, recipe
versions/dependencies/<mc>.properties   # per-MC dependency coordinates (AE2, GuideME, loader, ...)
```

Per-version dependency coordinates live in `versions/dependencies/1.20.1.properties` and `versions/dependencies/1.21.1.properties`; build outputs land under `versions/<node>/build/libs/`.

**Architecture at a glance**

- **One source tree.** All gameplay logic compiles for every loader/version. The loader-specific surface is small: the single `//?`-guarded entry point (`AE2NoByproduct`), plus the AE2 mixin descriptor, the networking, and the SavedData persistence differences, all guarded inline with Stonecutter `//?` comments rather than split across modules.
- **Server-side Mixin** (`PatternEncodingTermMenuMixin`): intercepts pattern encoding in `PatternEncodingTermMenu` and strips extra output slots if the player's toggle is ON. Stripping is server-authoritative and cannot be bypassed by clients. The same mixin also pushes a state-sync packet to the player when the menu opens.
- **Client-side Mixin** (`PatternEncodingTermScreenMixin`): injects the toggle button into AE2's left-hand toolbar at the same position and style as native AE2 buttons.
- **Byproduct Remover item** (`item/ByproductRemoverItem`): a server-side `useOn` handler that reads a Pattern Provider's pattern inventory via AE2's API (`PatternProviderBlockEntity.getLogic().getPatternInv()`), decodes each pattern, and re-encodes processing patterns keeping only the first output.
- **Config**: a single JSON file, `config/ae2nobyproduct.json`, on every loader, read through the `ByproductConfig` seam. The options and defaults are identical on all loaders.
- **Player data**: the per-player toggle survives relog, server restart, and death. It is stored as vanilla `SavedData` (`ByproductStore`) on the server overworld, keyed by player UUID, so the same code works on Forge, Fabric, and NeoForge with no loader-specific persistence.

---

## Adding Support for a New Minecraft / AE2 Version

The mod ships for 1.20.1 (Forge + Fabric) and 1.21.1 (NeoForge), all from this one codebase via Stonecutter. Each new MC/AE2 target needs:

1. Verify the **Mixin targets** still exist under the same class/method names against that AE2 version (`PatternEncodingTermMenu`, `AEBaseScreen.addToLeftToolbar`, `PatternProviderBlockEntity`). AE2 refactors internals between major versions, so check the AE2 source or decompiled jar first. The known breaking change at 1.21.1 is `PatternDetailsHelper.encodeProcessingPattern` taking `List<GenericStack>` instead of an array.
2. Add a **Stonecutter node** for the target (in `settings.gradle.kts`) and a **`versions/dependencies/<mc>.properties`** file with its coordinates (MC, loader, AE2, GuideME). Guard any code/resource differences inline with Stonecutter `//?` comments.
3. Switch to it (`./gradlew "Set active project to <node>"`), run `./gradlew runActive`, and verify the button appears and stripping works end-to-end.
4. Update the supported-versions tables in `README.md` and `CURSEFORGE.md`, and `CHANGELOG.md`. (`chiseledBuild` / `chiseledPublishMods` pick up the new node automatically, so no `release.yml` edit is needed.)

---

## Coding Conventions

- Match the style of the existing source files (4-space indentation, Allman-style braces in the existing Java files, etc.).
- Keep Mixins minimal: inject only what is necessary; avoid `@Overwrite` unless there is no alternative.
- All packet handling must be validated on the server; never trust values sent from the client.
- Add a brief Javadoc comment on every Mixin class explaining what it targets and why.

---

## Pull Request Process

1. **Fork** the repository and create a branch from `main` with a descriptive name (e.g. `fix/byproduct-strip-crash`, `feat/fabric-port`).
2. Make your changes and ensure the build is green:
   ```bash
   ./gradlew chiseledBuild
   ```
3. Test in-game with `./gradlew runActive` after switching to each version your change affects (`./gradlew "Set active project to <node>"`).
4. Open a pull request against `main` using the PR template. Fill in every section, especially "How tested."
5. A maintainer will review your PR. Please be responsive to feedback; stale PRs may be closed after 30 days of inactivity.

**Before opening a PR, please:**

- Search open issues and PRs to avoid duplicates.
- Keep changes focused: one logical change per PR.
- Update documentation (`README.md`, config descriptions) if your change affects user-visible behavior.

---

## Reporting Bugs

Use the [Bug Report](.github/ISSUE_TEMPLATE/bug_report.yml) issue template. Include logs and exact reproduction steps; without them, bugs are very hard to diagnose.

---

## Questions?

For general questions that don't belong in an issue, use [GitHub Discussions](https://github.com/AE2NoByproduct/AE2NoByproduct/discussions).
