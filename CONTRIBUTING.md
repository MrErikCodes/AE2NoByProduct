# Contributing to AE2 No Byproduct

Thank you for your interest in contributing! This document covers everything you need to get started.

---

## Development Environment

**Prerequisites**

- **Java 17** (the Gradle build uses a toolchain resolver — if you don't have JDK 17 installed locally, Gradle will automatically download and provision one; you just need a working internet connection on the first build).
- **Git**

**Clone and build**

```bash
git clone https://github.com/ErikCodes/AE2NoByProduct.git
cd AE2NoByProduct
./gradlew build
```

The output jar will appear in `build/libs/`.

**Launch the dev client (in-game testing)**

```bash
./gradlew runClient
```

This starts a Minecraft 1.20.1 Forge client with the mod loaded. You will need a copy of Applied Energistics 2 in `run/mods/` for the dev environment to work correctly (download it from CurseForge/Modrinth and drop it there).

---

## Project Layout

```
src/main/java/dev/erikcodes/ae2nobyproduct/
  ├── AE2NoByProduct.java          # Mod entry point
  ├── config/                      # Server config (TOML, Forge)
  ├── client/                      # Client widgets + cached state (toggle button, ClientByproductState)
  ├── config/                      # Server config (Forge TOML)
  ├── core/                        # EffectiveState, ByproductState (NBT), ByproductService
  ├── event/                       # Server events (sync state on terminal open)
  ├── item/                        # Byproduct Remover item (right-click a Pattern Provider)
  ├── mixin/                       # Mixin classes
  │   ├── PatternEncodingTermMenuMixin.java       # Server-side: strips byproducts at encode time
  │   └── client/                  # Client-side: toolbar toggle button
  │       ├── AEBaseScreenInvoker.java             # @Invoker for AE2's addToLeftToolbar
  │       └── PatternEncodingTermScreenMixin.java
  ├── network/                     # Packets for syncing player toggle state
  └── registry/                    # Item registration + creative tab
```

**Architecture at a glance**

- The mod is primarily **Mixin-based**: byproduct stripping and the toolbar button are injected into AE2's existing classes. It also registers one item (the Byproduct Remover) that uses AE2's public pattern API.
- **Server-side Mixin** (`PatternEncodingTermMenuMixin`): intercepts pattern encoding in `PatternEncodingTermMenu` and strips extra output slots if the player's toggle is ON. Stripping is server-authoritative and cannot be bypassed by clients.
- **Client-side Mixin** (`PatternEncodingTermScreenMixin`): injects the toggle button into AE2's left-hand toolbar at the same position and style as native AE2 buttons.
- **Byproduct Remover item** (`item/ByproductRemoverItem`): a server-side `useOn` handler that reads a Pattern Provider's pattern inventory via AE2's public API (`PatternProviderBlockEntity.getLogic().getPatternInv()`), decodes each pattern, and re-encodes processing patterns keeping only the first output.
- **Player data**: the per-player toggle state is stored in the player's persistent NBT (`Player.getPersistentData()` under the `PlayerPersisted` sub-tag), which survives relog, server restart, and death.

---

## Adding Support for a New Minecraft / AE2 Version

When multi-version support lands (see README roadmap), each new MC/AE2 target will need:

1. Verify the **Mixin target** (`PatternEncodingTermMenu`) still exists under the same class name and that the injection point method signature hasn't changed. AE2 occasionally refactors internals between minor versions — check the AE2 source or decompiled jar before assuming compatibility.
2. Update `build.gradle` / `gradle.properties` with the new MC, Forge, and AE2 version coordinates.
3. Run `./gradlew runClient` and verify the button appears and stripping works end-to-end.
4. Update the version table in `README.md`.

---

## Coding Conventions

- Match the style of the existing source files (4-space indentation, Allman-style braces in the existing Java files, etc.).
- Keep Mixins minimal — inject only what is necessary; avoid `@Overwrite` unless there is no alternative.
- All packet handling must be validated on the server; never trust values sent from the client.
- Add a brief Javadoc comment on every Mixin class explaining what it targets and why.

---

## Pull Request Process

1. **Fork** the repository and create a branch from `main` with a descriptive name (e.g. `fix/byproduct-strip-crash`, `feat/fabric-port`).
2. Make your changes and ensure the build is green:
   ```bash
   ./gradlew build
   ```
3. Test in-game with `./gradlew runClient`.
4. Open a pull request against `main` using the PR template. Fill in every section — especially "How tested."
5. A maintainer will review your PR. Please be responsive to feedback; stale PRs may be closed after 30 days of inactivity.

**Before opening a PR, please:**

- Search open issues and PRs to avoid duplicates.
- Keep changes focused — one logical change per PR.
- Update documentation (`README.md`, config descriptions) if your change affects user-visible behavior.

---

## Reporting Bugs

Use the [Bug Report](.github/ISSUE_TEMPLATE/bug_report.yml) issue template. Include logs and exact reproduction steps — without them, bugs are very hard to diagnose.

---

## Questions?

For general questions that don't belong in an issue, use [GitHub Discussions](https://github.com/ErikCodes/AE2NoByProduct/discussions).
