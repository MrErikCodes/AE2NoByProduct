## Summary

<!-- A clear and concise description of what this PR does and why. -->

## Related Issue

<!-- Link to the issue this PR addresses, e.g. "Closes #42" or "Related to #17". -->
Closes #<issue-number>

## Type of Change

- [ ] Bug fix (non-breaking change that fixes an issue)
- [ ] New feature (non-breaking change that adds functionality)
- [ ] Documentation update
- [ ] Refactor / code cleanup (no behavior change)
- [ ] Breaking change (fix or feature that would cause existing behavior to change)

## How Tested

<!-- Describe how you verified this change works correctly. -->

- [ ] Built successfully with `./gradlew chiseledBuild`
- [ ] Tested in-game via `./gradlew runActive`

**In-game test steps:**
<!-- e.g. "Opened Pattern Encoding Terminal, toggled strip ON, encoded a pattern with two outputs, and confirmed only the first output was kept." -->

## Checklist

- [ ] `./gradlew chiseledBuild` passes with no errors or warnings introduced by this PR
- [ ] Code follows the existing style (indentation, brace style, naming)
- [ ] Mixin targets verified against the AE2 version for the target MC (`versions/dependencies/<mc>.properties`)
- [ ] Documentation updated if this changes user-visible behavior (README, config descriptions)
- [ ] `README.md` and `CURSEFORGE.md` kept aligned; no em-dashes added
- [ ] `CHANGELOG.md` updated under `[Unreleased]` if this changes user-visible behavior
- [ ] No new files added that are not needed (no IDE project files, no build artifacts)
