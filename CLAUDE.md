# CLAUDE.md

Instructions for Claude Code (and other AI assistants) in this repo.

**Read [AGENTS.md](AGENTS.md) first.** It is the source of truth for build commands, the release process, versioning, the changelog, style rules, and the pre-release checklist. Everything below is a short reminder layer on top of it.

## Always remember
- **No em-dashes** (Unicode U+2014, the long dash) anywhere. Use commas, colons, parentheses, or new sentences. Verify with `grep -rP '\x{2014}' --include='*.md' --include='*.java' --include='*.json' --include='*.yml' --include='*.yaml' --include='*.properties' .` before finishing docs work.
- **`README.md` and `CURSEFORGE.md` must stay aligned.** If you change a feature, recipe, config option, or supported version, update BOTH.
- This is an **AE2 add-on**: when touching anything that targets AE2 internals (Mixins, the Byproduct Remover's pattern access), consult the AE2 source for the exact version, since signatures drift between versions.

## Before claiming a release is ready
Run the **Pre-release checklist in [AGENTS.md](AGENTS.md)**: `./gradlew chiseledBuild` green for every loader/version, `mod.version` in `gradle.properties` bumped to match the tag, `CHANGELOG.md` updated, docs aligned and em-dash-free, and the supported-versions tables current.

## After changing the build, loaders, or MC versions
Add a Stonecutter node in `settings.gradle.kts` and a matching `versions/dependencies/<mc>.properties` file, update the supported-versions tables in both docs and the matrix note in `AGENTS.md`, and re-verify the Mixin targets against the new AE2 version. The release workflow is loader/version-agnostic (`chiseledPublishMods`), so it needs no per-version edits.

## Workflow
- Branch from `main`, keep changes focused, open a PR (CodeRabbit reviews PRs automatically).
- Verify in-game with `./gradlew runActive` (set the active version first via the Stonecutter `Set active project to ...` task) when behavior changes; visual/GUI confirmation is the human's job.
