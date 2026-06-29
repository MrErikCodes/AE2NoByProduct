<p align="center">
  <img src="logo.png" alt="AE2 No Byproduct" width="160">
</p>

<h1 align="center">AE2 No Byproduct</h1>

<p align="center">
  <em>Strip autocrafting byproducts from AE2 processing patterns: an in-terminal toggle, a server/pack config, and a tool to clean patterns you already encoded.</em>
</p>

<p align="center">
  <a href="https://www.curseforge.com/minecraft/mc-mods/ae2-no-byproduct"><img alt="CurseForge" src="https://img.shields.io/curseforge/dt/1590300?logo=curseforge&logoColor=white&label=CurseForge&color=f16436"></a>
  <a href="https://modrinth.com/mod/ae2-no-byproduct"><img alt="Modrinth" src="https://img.shields.io/modrinth/dt/ae2-no-byproduct?logo=modrinth&logoColor=white&label=Modrinth&color=00af5c"></a>
  <a href="LICENSE"><img alt="License: MIT" src="https://img.shields.io/badge/License-MIT-blue"></a>
</p>

---

## What It Does

Applied Energistics 2 lets you encode **processing patterns** with multiple output slots. Many recipes (smelting, chemical reactions, ore processing chains) produce a primary output and one or more secondary outputs ("byproducts"). In some modpacks those byproduct slots cause ME autocrafting to stall because AE2 cannot route every output to a valid destination.

**AE2 No Byproduct** solves this cleanly at the source: it adds a small toggle button to the AE2 Pattern Encoding Terminal. When the toggle is **ON**, encoding a processing pattern keeps only the **first output** and silently discards every other output before the pattern is saved. Your crafting network no longer needs to handle byproducts it can't route.

Already have patterns encoded with byproducts? The mod also includes a **Byproduct Remover** tool: shift + right-click a Pattern Provider to clean every processing pattern stored inside it at once.

---

## Features

- **Per-player toggle**: a single button in AE2's native left-hand toolbar (green check = ON, red cross = OFF). Each player controls their own preference; it is not a global switch.
- **Server-authoritative**: stripping happens on the server at encode time. There is no client-side bypass.
- **Persistent**: the setting is remembered across relog, server restart, and death.
- **Processing patterns only**: crafting, smithing, and stonecutting patterns (which only have one output anyway) are never affected.
- **Works with AE2 add-ons**: any terminal that reuses AE2's Pattern Encoding Terminal (such as wireless pattern terminals) picks up the toggle automatically, with no conflicts with other AE2 add-ons.
- **Operator control**: a small server config lets pack makers force-enable stripping for all players with no per-player UI (see [Configuration](#configuration)).
- **Byproduct Remover tool**: a craftable item for cleaning patterns you *already* encoded. Shift + right-click a Pattern Provider and every processing pattern inside it is stripped of byproducts in one go.
- **Network-wide cleanup command**: operators can run `/ae2nobyproduct strip-all` while looking at any block of an AE2 network to strip byproducts from every Pattern Provider in that whole network at once. It runs in two steps (once to preview the count, again within 30 seconds to apply), so a bulk edit is never accidental. There is also `/ae2nobyproduct inspect`, a read-only version that just reports how many patterns have byproducts, changing nothing.

---

## Requirements & Supported Versions

> **Server and client mod.** AE2 No Byproduct must be installed on **both** the server and the client. On a dedicated or LAN server, install it on the server *and* on every client that connects: the toggle button and the Byproduct Remover live on the client, while stripping is enforced on the server. In single player, one copy in your `mods/` folder covers both sides.

**Available now**

| Minecraft | Mod loader | Applied Energistics 2 | Java |
|-----------|------------|-----------------------|------|
| 1.20.1 | Forge 47.x, or Fabric (Fabric Loader 0.16+ with Fabric API) | 15.4.x | 17 |
| 1.21.1 | NeoForge 21.1.x | 19.2.x | 21 |

> The Gradle toolchain auto-provisions both Java 17 (for 1.20.1) and Java 21 (for 1.21.1).

> 1.21.1 is NeoForge only, because Applied Energistics 2 has no Fabric or Forge build for 1.21.1.

> AE2 requires **GuideME**. On 1.20.1 it ships inside AE2; on 1.21.1 GuideME is a separate mod that AE2 depends on, which CurseForge and Modrinth install automatically alongside AE2. Either way, having AE2 covers it, with no separate download needed.

All loaders and versions are built from the same single codebase using the Stonecraft Gradle plugin (Stonecutter + Architectury Loom).

---

## Installation

1. Download the jar **for your loader and Minecraft version** from the [Releases](https://github.com/AE2NoByproduct/AE2NoByproduct/releases) page, [CurseForge](https://www.curseforge.com/minecraft/mc-mods/ae2-no-byproduct), or [Modrinth](https://modrinth.com/mod/ae2-no-byproduct). The jar name tells you which: it is `ae2nobyproduct-<loader>-<modversion>+mc<mcversion>.jar`, for example `ae2nobyproduct-forge-0.2.0+mc1.20.1.jar` (Forge, 1.20.1), `ae2nobyproduct-fabric-0.2.0+mc1.20.1.jar` (Fabric, 1.20.1), or `ae2nobyproduct-neoforge-0.2.0+mc1.21.1.jar` (NeoForge, 1.21.1).
2. Drop the jar into your `mods/` folder alongside Applied Energistics 2. In multiplayer, do this on the **server and on every client** (it is a server and client mod).
3. Launch Minecraft. No extra setup required.

That's it. The toggle button will appear in the Pattern Encoding Terminal as soon as the mod is loaded.

---

## Usage

### The toggle button

1. **Open the Pattern Encoding Terminal** as you normally would.
2. **Find the toggle button** in AE2's left-hand toolbar (the same column as other AE2 toolbar buttons, such as the crafting mode selector). The button shows a **red cross** by default (stripping is OFF).
3. **Click the button** to enable stripping. It switches to a **green check** (ON).
4. **Encode a processing pattern** with multiple output slots. When you click "Encode", only the first output is saved into the pattern; all other outputs are discarded.
5. Click again to return to the **red cross** (OFF). Patterns encoded while OFF keep all their outputs as usual.

Your choice is saved automatically. You can close and reopen the terminal, relog, or restart the server, and your setting persists.

> **Note:** The toggle has no effect on crafting patterns, smithing patterns, or stonecutting patterns, since those only ever produce one output anyway.

### The Byproduct Remover tool

For patterns that were *already* encoded with byproducts, craft the **Byproduct Remover** (a shapeless recipe of a **16k Storage Component** + a **Blank Pattern** + a **Crafting Unit**). **Shift + right-click a Pattern Provider** with it, either the full block or the cable-mounted panel, and every processing pattern stored in that provider is re-encoded to keep only its first output. A chat message reports how many patterns were cleaned (this can be silenced; see `showMessages` below). The tool is reusable by default and works on add-on Pattern Providers too, such as Extended AE and MEGA Cells.

---

## Configuration

The config is created automatically on first launch with defaults, in your `config/` folder like other mods: a single JSON file, `config/ae2nobyproduct.json`, on every loader. The options are identical on all loaders. The mod is server-authoritative: on a multiplayer server, the server's config is what applies.

| Option | Default | Description |
|--------|---------|-------------|
| `enableFeature` | `true` | Master switch. When `false`, the mod is completely inactive: no button is shown and no stripping occurs. |
| `allowPlayerToggle` | `true` | When `true`, each player sees the toggle button and controls their own preference. When `false`, the button is hidden and `defaultStrip` is applied to everyone with no exceptions. |
| `defaultStrip` | `false` | The starting value for players who have never toggled the button. Also the forced value for all players when `allowPlayerToggle = false`. |
| `consumeOnUse` | `false` | When `true`, the Byproduct Remover item is consumed (one is used up) each time it successfully cleans at least one pattern. Default keeps it a reusable tool. |
| `showMessages` | `true` | When `true`, the Byproduct Remover sends a chat message after use. Set to `false` to silence it. |

**Pack-maker tip: force stripping for all players**

To make byproduct stripping always-on with no player choice (useful when you want every autocrafting pattern in the pack to be byproduct-free), set:

```json
{
  "allowPlayerToggle": false,
  "defaultStrip": true
}
```

No button will appear in the terminal; all processing patterns will silently strip byproducts for every player.

---

## Compatibility

- **AE2 add-ons:** any terminal that reuses AE2's Pattern Encoding Terminal (such as wireless pattern terminals) gets the toggle automatically, and the Byproduct Remover works on add-on Pattern Providers (tested with Extended AE and MEGA Cells).
- **KubeJS:** the Byproduct Remover's recipe is a standard shapeless recipe, so packs can change or remove it with KubeJS.

---

## Building from Source

The project uses the Stonecraft Gradle plugin (Stonecutter + Architectury Loom) with a single flat source tree; each loader/version is a Stonecutter "node". The Gradle toolchain provisions both Java 17 (1.20.1) and Java 21 (1.21.1) automatically if they are not found locally.

```bash
git clone https://github.com/AE2NoByproduct/AE2NoByproduct.git
cd AE2NoByproduct
./gradlew chiseledBuild
```

`chiseledBuild` builds every loader and version. Use `./gradlew chiseledBuildAndCollect` to gather all jars into `build/libs/`. The output jars are named `ae2nobyproduct-<loader>-<modversion>+mc<mcversion>.jar` (for example `ae2nobyproduct-neoforge-0.2.0+mc1.21.1.jar`); each node also keeps its own jar under `versions/<node>/build/libs/`.

To launch a dev client for in-game testing, run the currently active version:

```bash
./gradlew runActive
```

Switch the active version with the Stonecutter task, for example:

```bash
./gradlew "Set active project to 1.21.1-neoforge"
```

See [CONTRIBUTING.md](CONTRIBUTING.md) for full dev environment setup instructions.

---

## Contributing

Contributions are welcome! Please read [CONTRIBUTING.md](CONTRIBUTING.md) before opening a pull request. It covers the dev environment, project layout, coding conventions, and the PR process.

**Found a bug?** Open an issue using the [Bug Report](.github/ISSUE_TEMPLATE/bug_report.yml) template. Include your Minecraft version, mod loader version, AE2 version, AE2 No Byproduct version, logs, and clear reproduction steps.

**Have a question?** Use [GitHub Discussions](https://github.com/AE2NoByproduct/AE2NoByproduct/discussions) rather than an issue.

---

## License

This project is licensed under the **MIT License**. See [LICENSE](LICENSE) for the full text.

---

## Credits

[Applied Energistics 2](https://github.com/AppliedEnergistics/Applied-Energistics-2) is the foundation this mod builds on. All AE2 trademarks and assets belong to their respective owners. AE2 No Byproduct is an independent add-on and is not affiliated with or endorsed by the AE2 team.
