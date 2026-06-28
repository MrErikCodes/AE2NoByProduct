# Contributing to AE2 No Byproduct

Thank you for your interest in contributing! This document covers everything you need to get started.

---

## Development Environment

**Prerequisites**

- **Java 17** (the Gradle build uses a toolchain resolver: if you don't have JDK 17 installed locally, Gradle will automatically download and provision one; you just need a working internet connection on the first build).
- **Git**

**Clone and build**

```bash
git clone https://github.com/MrErikCodes/AE2NoByProduct.git
cd AE2NoByProduct
./gradlew build
```

This builds both loaders. The output jars appear in `forge/build/libs/` and `fabric/build/libs/`, named `ae2nobyproduct-<loader>-<mcversion>-<modversion>.jar`.

**Launch the dev client (in-game testing)**

```bash
./gradlew :forge:runClient    # Forge
./gradlew :fabric:runClient   # Fabric
```

This starts a Minecraft 1.20.1 client (Forge or Fabric) with the mod loaded. Applied Energistics 2 and GuideME are resolved automatically, so they load with no manual steps. The Fabric dev runtime additionally pulls the Team Reborn Energy API, a nested dependency AE2-fabric needs that loom does not surface on its own.

---

## Project Layout

This is a multi-loader **Architectury** project: all gameplay logic lives in `common/` and runs on both loaders; `forge/` and `fabric/` hold only the loader-specific leaves.

```text
common/src/main/java/dev/erikcodes/ae2nobyproduct/   # shared; runs on Forge AND Fabric
  ├── CommonMod.java               # shared init (registers networking + items)
  ├── client/                      # toolbar button + cached client state (ClientByproductState)
  ├── core/                        # EffectiveState, ByproductService, ByproductConfig (the platform seam)
  ├── item/                        # Byproduct Remover item
  ├── network/                     # Architectury networking (set-toggle C2S, state-sync S2C)
  ├── registry/                    # item + creative-tab registration (Architectury DeferredRegister)
  └── mixin/
      ├── PatternEncodingTermMenuMixin.java          # server-side: strips byproducts + sync trigger
      └── client/                                    # client-side toolbar button
          ├── AEBaseScreenInvoker.java               # @Invoker for AE2's addToLeftToolbar
          └── PatternEncodingTermScreenMixin.java
forge/src/main/java/.../           # Forge-only leaves
  ├── forge/                       # @Mod entry, ForgeByproductConfig, ByproductState (persistent NBT)
  └── config/                      # ForgeConfigSpec config
fabric/src/main/java/.../fabric/   # Fabric-only leaves
  ├── AE2NoByProductFabric.java    # ModInitializer entry
  ├── FabricByproductConfig.java   # the seam impl
  ├── config/                      # JSON config (FabricConfig)
  ├── persistence/                 # ByproductToggleAccess duck interface
  └── mixin/                       # Player / ServerPlayer persistence mixins
```

Shared resources (lang, item model, texture, recipe, and the common + client mixin configs) live in `common/src/main/resources/` and are bundled into both jars.

**Architecture at a glance**

- **Common-first.** All gameplay logic lives in `common/` and runs on both loaders via Architectury. Only three things are loader-specific, and they sit behind the `ByproductConfig.Provider` seam: the entry point, the config source, and the per-player persistence mechanism. Never share a Java package between `common/` and a loader module: Forge/NeoForge enforce JPMS and reject split packages.
- **Server-side Mixin** (`PatternEncodingTermMenuMixin`): intercepts pattern encoding in `PatternEncodingTermMenu` and strips extra output slots if the player's toggle is ON. Stripping is server-authoritative and cannot be bypassed by clients. The same mixin also pushes a state-sync packet to the player when the menu opens.
- **Client-side Mixin** (`PatternEncodingTermScreenMixin`): injects the toggle button into AE2's left-hand toolbar at the same position and style as native AE2 buttons.
- **Byproduct Remover item** (`item/ByproductRemoverItem`): a server-side `useOn` handler that reads a Pattern Provider's pattern inventory via AE2's API (`PatternProviderBlockEntity.getLogic().getPatternInv()`), decodes each pattern, and re-encodes processing patterns keeping only the first output.
- **Config**: read through the `ByproductConfig` seam. Forge uses a `ForgeConfigSpec` (`config/ae2nobyproduct.toml`), Fabric a small JSON file (`config/ae2nobyproduct.json`); the options and defaults are identical.
- **Player data**: the per-player toggle survives relog, server restart, and death. On **Forge** it uses `Player.getPersistentData()` (the `PlayerPersisted` sub-tag, which Forge copies on death). On **Fabric** (no such API) a `Player` mixin stores it and (de)serialises it in `add/readAdditionalSaveData`, plus a `ServerPlayer.restoreFrom` mixin to carry it across death.

---

## Adding Support for a New Minecraft / AE2 Version

Multi-loader (Forge + Fabric on 1.20.1) is done. Multi-version (1.21.1 on NeoForge) is built from this same codebase via Stonecutter. Each new MC/AE2 target needs:

1. Verify the **Mixin targets** still exist under the same class/method names against that AE2 version (`PatternEncodingTermMenu`, `AEBaseScreen.addToLeftToolbar`, `PatternProviderBlockEntity`). AE2 refactors internals between major versions, so check the AE2 source or decompiled jar first. The known breaking change at 1.21.1 is `PatternDetailsHelper.encodeProcessingPattern` taking `List<GenericStack>` instead of an array.
2. Add the target's coordinates (MC, loader, AE2, GuideME, Architectury) and, for a new loader, its entry point + config + persistence leaves behind the `ByproductConfig` seam.
3. Run `./gradlew :<loader>:runClient` and verify the button appears and stripping works end-to-end.
4. Update the supported-versions tables in `README.md` and `CURSEFORGE.md`, the `release.yml` loaders / jar globs, and `CHANGELOG.md`.

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
   ./gradlew build
   ```
3. Test in-game with `./gradlew :forge:runClient` and `./gradlew :fabric:runClient` (whichever loaders your change affects).
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

For general questions that don't belong in an issue, use [GitHub Discussions](https://github.com/MrErikCodes/AE2NoByProduct/discussions).
