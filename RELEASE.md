# Releasing AE2 No Byproduct

Publishing is automated. When you publish a GitHub Release, the [`release.yml`](.github/workflows/release.yml) workflow builds the jar and uploads it to the GitHub Release, CurseForge, and Modrinth. The CurseForge id, Modrinth slug, and API tokens are already configured.

## Cutting a release

1. Make sure `main` builds: `./gradlew build` (tests pass).
2. Confirm `README.md` and `CURSEFORGE.md` are aligned and contain no em-dashes.
3. Bump `mod_version` in `gradle.properties` (semver). The git tag will be `v<mod_version>`, and the published game version is read from `mc_version` in the same file.
4. Update `CHANGELOG.md`: move the `[Unreleased]` items into a new `## [x.y.z] - YYYY-MM-DD` section, refresh the compare links, and leave a fresh empty `[Unreleased]`.
5. Commit and push to `main`.
6. On GitHub, go to **Releases → Draft a new release**:
   - Tag `vx.y.z` (matching `mod_version`).
   - Click **Generate release notes** for the body, or paste the `CHANGELOG.md` section.
   - **Publish release.**
7. Watch the **Actions** tab. The `release.yml` run builds and publishes the jar to GitHub, CurseForge, and Modrinth with that changelog.

## Keeping the published metadata correct

- **Version match is enforced.** Before building or publishing, the release workflow fails if the git tag does not match `gradle.properties` `mod_version` (ignoring a leading `v`). The published file and the release label are therefore always the same version.
- **Game version** is read automatically from `gradle.properties` (`mc_version`), so it always matches what was built. No manual edit needed.
- **Loaders** are set in `release.yml` (`loaders: forge`). If you add a loader (Fabric, NeoForge), update that line and the `files` glob, and re-verify the Mixin targets for the new AE2 version. See [AGENTS.md](AGENTS.md).

## After publishing

Verify the file appears on the [Releases page](https://github.com/MrErikCodes/AE2NoByProduct/releases), on [CurseForge](https://www.curseforge.com/minecraft/mc-mods/ae2-no-byproduct), and on [Modrinth](https://modrinth.com/mod/ae2-no-byproduct).
