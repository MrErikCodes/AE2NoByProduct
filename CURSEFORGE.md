# AE2 No Byproduct

**Tired of Applied Energistics 2 autocrafting choking on byproducts? This fixes it.**

Many AE2 **processing patterns** (ore doubling, chemical reactions, smelting chains) produce a primary output *plus* one or more secondary outputs ("byproducts"). In a lot of modpacks those extra outputs jam ME autocrafting: the network can't route every output to a valid place, and the craft stalls.

**AE2 No Byproduct** solves it at the source, and gives you a tool to clean up the patterns you've *already* made.

---

## ✨ What You Get

### 🔘 A toggle in the Pattern Encoding Terminal
A small button sits in AE2's native left-hand toolbar (right next to AE2's own buttons):

- **Green check = ON**: when you encode a processing pattern, only the **first output** is kept; all byproducts are dropped before the pattern is saved.
- **Red cross = OFF**: patterns encode normally, with every output.

It's **per-player**, **server-authoritative** (no client bypass), and your choice is **remembered** across relog, restart, and death. Crafting, smithing, and stonecutting patterns are never touched; they only have one output anyway.

### 🧹 The Byproduct Remover tool
Already encoded a bunch of patterns with byproducts? Don't re-do them. Craft the **Byproduct Remover** and **shift + right-click any Pattern Provider** (the full block or the cable-mounted panel), and every processing pattern stored inside it is instantly re-encoded to keep only its first output. You get a chat message telling you how many were cleaned.

> Works on AE2's Pattern Providers **and** add-on providers (tested with Extended AE and MEGA Cells).

**Recipe (shapeless):** `16k Storage Component` + `Blank Pattern` + `Crafting Unit`

### 🧨 Network-wide cleanup command
Need to clean a whole base at once instead of one provider at a time? Operators can run **`/ae2nobyproduct strip-all`** while looking at any block of an AE2 network: it strips byproducts from **every Pattern Provider in that network** (block and cable-part forms alike). It is deliberately two-step so a bulk edit is never an accident: the first run **previews** how many patterns would change, and running it again on that same network within 30 seconds applies it. Prefer to just look first? **`/ae2nobyproduct inspect`** reports the same counts read-only, without touching anything.

### ⚙️ Full operator / pack-maker control
A server config lets pack authors decide exactly how it behaves, including **forcing** byproduct removal for everyone with no per-player UI at all.

---

## 🛠️ Configuration

The config is created on first launch in your `config/` folder like other mods: a single JSON file, `config/ae2nobyproduct.json`, on every loader. The options are identical on all loaders. On a multiplayer server, the server's config is the one that applies.

| Option | Default | What it does |
|--------|---------|--------------|
| `enableFeature` | `true` | Master switch. `false` = the mod does nothing and shows no button. |
| `allowPlayerToggle` | `true` | `false` = the button is hidden and `defaultStrip` is **forced** for everyone. |
| `defaultStrip` | `false` | Starting value for new players; also the forced value when `allowPlayerToggle = false`. |
| `consumeOnUse` | `false` | `true` = the Byproduct Remover is consumed after it cleans a provider. Default keeps it reusable. |
| `showMessages` | `true` | `false` = silence the Byproduct Remover's chat feedback. |

**Want byproducts gone pack-wide, no questions asked?**
```json
{
  "allowPlayerToggle": false,
  "defaultStrip": true
}
```
The button disappears and every player's processing patterns are stripped automatically.

---

## 🧩 Compatibility

- **AE2 add-ons:** any terminal that reuses AE2's Pattern Encoding Terminal (such as wireless pattern terminals) gets the toggle automatically. The Byproduct Remover works on add-on Pattern Providers too.
- **KubeJS:** the Byproduct Remover's recipe is a standard shapeless recipe, so packs can freely change or remove it with KubeJS.
- Plays nicely alongside other AE2 add-ons; it only touches the encoding step.

---

## 📦 Requirements

> **Server and client mod.** Install AE2 No Byproduct on **both** the server and the client. On a dedicated or LAN server it must be present on the server *and* on every client that connects: the toggle button and Byproduct Remover live on the client, while stripping is enforced on the server. In single player, one copy in your `mods/` folder covers both.

| Minecraft | Loader | Required: [Applied Energistics 2](https://www.curseforge.com/minecraft/mc-mods/applied-energistics-2) | Java |
|---|---|---|---|
| **1.20.1** | Forge 47.x, or Fabric (Loader 0.16+ with Fabric API) | 15.4.x | 17 |
| **1.21.1** | NeoForge 21.1.x | 19.2.x | 21 |

*(AE2 requires **GuideME**: bundled inside AE2 on 1.20.1, and a separate mod that CurseForge and Modrinth auto-install with AE2 on 1.21.1. Either way, having AE2 covers it.)*

1.21.1 is NeoForge only, because Applied Energistics 2 has no Fabric or Forge build for 1.21.1.

Pick the jar that matches your loader and Minecraft version: it is named `ae2nobyproduct-<loader>-<modversion>+mc<mcversion>.jar`, for example `ae2nobyproduct-forge-0.2.0+mc1.20.1.jar`, `ae2nobyproduct-fabric-0.2.0+mc1.20.1.jar`, or `ae2nobyproduct-neoforge-0.2.0+mc1.21.1.jar`. All loaders and versions are built from the same single codebase using the Stonecraft Gradle plugin (Stonecutter + Architectury Loom).

---

## 📖 Open Source

AE2 No Byproduct is **open source under the MIT License**: use it, fork it, bundle it in your modpack freely; just keep the credit. Source, issues, and contributions welcome on [GitHub](https://github.com/AE2NoByproduct/AE2NoByproduct).

*Not affiliated with or endorsed by the Applied Energistics team. AE2 and its assets belong to their respective owners.*
