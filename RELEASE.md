# Releasing AE2 No Byproduct

Publishing is automated. When you publish a GitHub Release, the [`release.yml`](.github/workflows/release.yml) workflow builds **every loader/version jar** (Forge + Fabric on 1.20.1, NeoForge on 1.21.1) and publishes them to the GitHub Release, CurseForge, and Modrinth. The CurseForge id/slug, Modrinth id, and API tokens are already configured. See [How publishing works](#how-publishing-works) for the details.

## Cutting a release

1. Make sure `main` builds: `./gradlew chiseledBuild` (tests pass).
2. Confirm `README.md` and `CURSEFORGE.md` are aligned and contain no em-dashes.
3. Bump `mod.version` in `gradle.properties` (semver). The git tag will be `v<mod.version>`; each loader/version's MC version is derived from its Stonecutter node and `versions/dependencies/<mc>.properties`.
4. Update `CHANGELOG.md`: move the `[Unreleased]` items into a new `## [x.y.z] - YYYY-MM-DD` section, refresh the compare links, and leave a fresh empty `[Unreleased]`.
5. Commit and push to `main`.
6. On GitHub, go to **Releases → Draft a new release**:
   - Tag `vx.y.z` (matching `mod.version`).
   - Click **Generate release notes** for the body, or paste the `CHANGELOG.md` section.
   - **Publish release.**
7. Watch the **Actions** tab. The `release.yml` run builds and publishes every loader/version jar to GitHub, CurseForge, and Modrinth with that changelog.

## How publishing works

`release.yml` runs `./gradlew chiseledBuildAndCollect` to build every loader/version jar and gather them into `build/libs/`, attaches those jars to the GitHub Release, then runs `./gradlew chiseledPublishMods`. That single Gradle task (Stonecraft's built-in mod-publish-plugin) uploads every loader/version jar as its own correctly-tagged file to CurseForge and Modrinth in one go, reading the release notes from `CHANGELOG.md`.

There are no longer any per-loader, per-store publish steps. Stonecraft derives each file's loader, Minecraft version, and dependency tags from its Stonecutter node and `versions/dependencies/<mc>.properties`, so the Forge jar is tagged Forge, the NeoForge jar is tagged NeoForge, and so on, with no `+forge` / `+fabric` Modrinth version suffixes and no risk of mis-grouping.

**Adding a loader or version:** add a Stonecutter node plus a `versions/dependencies/<mc>.properties` file (see [AGENTS.md](AGENTS.md)). `chiseledBuildAndCollect` and `chiseledPublishMods` then pick it up automatically; no workflow edits are needed.

## GitHub-only release (skip CurseForge / Modrinth)

If that version is already on CurseForge and Modrinth (or you only want it attached to the GitHub Release), put `[skip-stores]` anywhere in the release notes. The workflow still builds and attaches every loader/version jar to the GitHub Release, but skips both stores. Handy for backfilling a GitHub Release for a version that was uploaded to the stores manually.

## Keeping the published metadata correct

- **Version match is enforced.** Before building or publishing, the release workflow fails if the git tag does not match `gradle.properties` `mod.version` (ignoring a leading `v`). The published file and the release label are therefore always the same version.
- **Game version and loader** are derived per file from each Stonecutter node and `versions/dependencies/<mc>.properties`, so they always match what was built (see [How publishing works](#how-publishing-works)). The jars carry the loader and MC version in their filename (`ae2nobyproduct-<loader>-<modversion>+mc<mcversion>.jar`). When adding a loader or version, add its Stonecutter node and `versions/dependencies/<mc>.properties`, and re-verify the Mixin targets for the new AE2 version. See [AGENTS.md](AGENTS.md).

## After publishing

Verify the file appears on the [Releases page](https://github.com/AE2NoByproduct/AE2NoByproduct/releases), on [CurseForge](https://www.curseforge.com/minecraft/mc-mods/ae2-no-byproduct), and on [Modrinth](https://modrinth.com/mod/ae2-no-byproduct).
