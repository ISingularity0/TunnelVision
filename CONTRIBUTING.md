# Contributing

## Setup

1. Install JDK 25 (e.g. [Temurin](https://adoptium.net/)).
2. Clone the repo and open it in IntelliJ IDEA. Set the Gradle JVM to JDK 25 (Settings → Build Tools → Gradle).
3. Run `./gradlew runClient` to start Minecraft with the mod.

## Workflow

1. Pick or create an issue for what you want to do.
2. Create a branch from `main`:
   - `feature/<issue>-<short-name>` for new features, e.g. `feature/12-waypoints`
   - `fix/<issue>-<short-name>` for bug fixes
   - `chore/<short-name>` for build, CI, or dependency changes
   - `docs/<short-name>` for documentation
3. Open a pull request into `main` and write `Closes #<issue>` in the description.
4. The build must pass and one maintainer must approve before merging.
5. PRs are squash-merged, so the PR title becomes the commit message on `main`. Keep it short and in the imperative, e.g. "Add waypoint renderer".

## Code

- Kotlin for mod code, Java only for mixins (`src/main/java/.../mixin`).
- Keep PRs small and focused on one change.
