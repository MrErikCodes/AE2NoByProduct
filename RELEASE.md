# Releasing AE2 No Byproduct

Publishing is fully automated **on merge**. When a `mod.version` bump lands on `main`, the [`release.yml`](.github/workflows/release.yml) workflow builds **every loader/version jar** (Forge + Fabric on 1.20.1, NeoForge on 1.21.1), creates the `v<version>` GitHub Release (jars + changelog notes), and publishes them to CurseForge and Modrinth. The CurseForge id/slug, Modrinth id, and API tokens are already configured. See [How publishing works](#how-publishing-works) for the details.

## Cutting a release

A release fires automatically the moment a new `mod.version` reaches `main`, so cutting one is just preparing the change:

1. Make sure `main` builds: `./gradlew chiseledBuild` (tests pass).
2. Confirm `README.md` and `CURSEFORGE.md` are aligned and contain no em-dashes.
3. Bump `mod.version` in `gradle.properties` (semver). The tag and GitHub Release will be `v<mod.version>`; each loader/version's MC version is derived from its Stonecutter node and `versions/dependencies/<mc>.properties`.
4. Finalize `CHANGELOG.md`: move the `[Unreleased]` items into a new `## [x.y.z] - YYYY-MM-DD` section, add a fresh empty `[Unreleased]`, and update the compare links at the bottom. The workflow uses that `## [x.y.z]` section verbatim as the GitHub Release notes, so its header must match `mod.version`.
5. Open a PR with those changes and merge it to `main` (or push directly).

That is the whole flow: when the version bump lands on `main`, the workflow tags `v<x.y.z>`, builds, creates the GitHub Release, and publishes to CurseForge + Modrinth. Watch the **Actions** tab. There is no manual "draft a release" step.

## How publishing works

`release.yml` triggers on a push to `main` that changes `gradle.properties`. It reads `mod.version`; if a `v<version>` release does not exist yet, it runs `./gradlew chiseledBuildAndCollect` to build every loader/version jar into `build/libs/`, creates the `v<version>` GitHub Release (jars attached, body taken from that version's `CHANGELOG.md` section), then runs `./gradlew chiseledPublishMods`. That single Gradle task (Stonecraft's built-in mod-publish-plugin) uploads every loader/version jar as its own correctly-tagged file to CurseForge and Modrinth in one go.

There are no longer any per-loader, per-store publish steps. Stonecraft derives each file's loader, Minecraft version, and dependency tags from its Stonecutter node and `versions/dependencies/<mc>.properties`, so the Forge jar is tagged Forge, the NeoForge jar is tagged NeoForge, and so on, with no `+forge` / `+fabric` Modrinth version suffixes and no risk of mis-grouping.

### Two flags control the store upload (both already correct for a normal release)

- **`DO_PUBLISH`** (set in `release.yml`) is Stonecraft's **dry-run** switch, *not* a publish switch, so its value is inverted from what the name suggests. Stonecraft does `dryRun = DO_PUBLISH.toBoolean()`:
  - `"false"` -> `dryRun=false` -> it **really uploads** (this is what we want, and what the workflow hard-codes).
  - `"true"` or unset -> `dryRun=true` -> dry run: builds the upload but sends nothing.
  - Leave it `"false"`. To skip the stores, use `[skip-stores]` (below), do not flip this to `"true"`.
- **`[skip-stores]`** in the commit message that bumps the version skips the Modrinth/CurseForge step entirely (GitHub-only), see [below](#github-only-release-skip-curseforge--modrinth).

So a normal release (notes *without* `[skip-stores]`) publishes to both stores automatically; you never touch `DO_PUBLISH`.

**Adding a loader or version:** add a Stonecutter node plus a `versions/dependencies/<mc>.properties` file (see [AGENTS.md](AGENTS.md)). `chiseledBuildAndCollect` and `chiseledPublishMods` then pick it up automatically; no workflow edits are needed.

## GitHub-only release (skip CurseForge / Modrinth)

If that version is already on CurseForge and Modrinth (or you only want it on the GitHub Release), put `[skip-stores]` in the commit message that bumps the version (the merge or squash commit). The workflow still builds, creates the `v<version>` GitHub Release, and attaches every loader/version jar, but skips both stores.

## Keeping the published metadata correct

- **The tag is derived from `mod.version`**, so the release label and the published files are always the same version by construction. The workflow skips entirely if `v<mod.version>` already exists, so editing `gradle.properties` without bumping the version never double-releases.
- **Game version and loader** are derived per file from each Stonecutter node and `versions/dependencies/<mc>.properties`, so they always match what was built (see [How publishing works](#how-publishing-works)). The jars carry the loader and MC version in their filename (`ae2nobyproduct-<loader>-<modversion>+mc<mcversion>.jar`). When adding a loader or version, add its Stonecutter node and `versions/dependencies/<mc>.properties`, and re-verify the Mixin targets for the new AE2 version. See [AGENTS.md](AGENTS.md).

## After publishing

Verify the file appears on the [Releases page](https://github.com/AE2NoByproduct/AE2NoByproduct/releases), on [CurseForge](https://www.curseforge.com/minecraft/mc-mods/ae2-no-byproduct), and on [Modrinth](https://modrinth.com/mod/ae2-no-byproduct).
