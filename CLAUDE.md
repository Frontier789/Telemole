# Telegram Android — personal fork

This is a personal fork of the official Telegram Android client. Remotes: `origin` is our fork
(`Frontier789/Telemole`), `upstream` is the official `DrKLO/Telegram`.
It is built and installed on one phone, for one user. The goal is a **clean, simple, fast, usable Telegram**
with the garbage removed. Read the rules below before changing anything.

## The rules

### 1. Minimal code changes — upgrades must stay cheap
Upstream ships a new version every few days and rewrites huge files (`LaunchActivity`, `MessagesController`,
`TLRPC`, ...). Every line this fork changes is a potential merge conflict later, so:

- Hide or disable a feature at the **narrowest point**: the place that creates the UI element, the existing
  feature gate (e.g. `MessagesController.storiesEnabled()`), or the call site. Do **not** delete the feature's
  classes, layouts, resources, network code or TL schema. Dead code is fine; conflicts are not.
- Prefer a one-line change in an upstream file that calls a small new helper living in a **new file**.
  New files never conflict.
- No drive-by cleanups: no reformatting, import reordering, renaming, moving code, or "while I'm here" fixes.
  If a diff touches a line, it must be because the feature needs it.
- Never hand-edit generated or schema code (`TLRPC.java`, `tgnet/`, `buildSrc/` generators) unless there is no other way, and ask first.
- Do not edit the version in `gradle.properties` (`APP_VERSION_*`). Versions come from upstream.
- Do not delete modules, flavors, or files, even unused ones (see rule 6). Deleting upstream files creates conflicts.

### 2. Readable, easy changes
- Code should read like the surrounding code and explain itself through names. Prefer small, talkative
  helper functions (`shouldShowXxx()`, `hideXxxRow()`) over inline conditions.
- Comment only when it is absolutely necessary: a non-obvious *why* that no name can carry
  (a workaround, a trap, an upstream quirk). Never comment *what* the code does.
- Keep each change small enough to review at a glance.

### 3. Goal: a clean, simple, fast, usable app
Remove what gets in the way of messaging. Examples of garbage (not exhaustive):
ads / sponsored messages, stories, AI buttons and AI features, upsells and promo banners,
nagging prompts and suggestions, in-app update prompts (the official update APK can never install over this fork).

- "Remove" means gone from the user's sight and flow, not necessarily deleted from the codebase (rule 1).
- Do not remove or degrade core messaging: chats, media, calls, notifications, search, settings the user would
  actually use, account/login, privacy and security features.
- If you are unsure whether something counts as garbage, that is a rule 4 question.

**Performance improvements are welcome too.** The clearest case: once a feature is hidden, stop paying for it.
A removed feature must not keep fetching its data, preloading, polling, or running startup work for a UI that no
longer exists (e.g. the requests that load stories or sponsored messages). Skipping such requests is pre-approved.

- Do it at the single place the request or work is issued, ideally behind the same gate that hides the feature
  (rule 1).
- Anything else that changes what the app fetches or when (message history, the update/difference sync, read
  state, media) is a rule 4 question: missing a message is far worse than a wasted request.
- Say concretely what is no longer done (which request, which work). Do not claim speedups you did not measure (rule 5).

### 4. Ask — do not assume
When a choice is the user's to make, ask before acting; do not pick a default and continue.
Use the `AskUserQuestion` tool with concrete options and your recommendation first. Batch related questions.

Always ask before:
- deciding that a borderline feature is or isn't garbage;
- choosing between hiding vs. deleting, or between two ways to hide something, when they differ in visible behavior;
- adding a dependency, a new module, or test infrastructure;
- changing build config (ABIs, flavors, signing, Gradle/AGP versions);
- touching login/auth, encryption, or the sync of core messaging data (see the performance notes in rule 3).
  Dropping the requests of features removed under rule 3 is the exception and needs no question;
- installing system-wide tools or downloading large things (Android SDK/NDK, submodules);
- anything hard to reverse or outward-facing (pushing, force-push, history rewrite, deleting files).

Do not ask about what this file already decides, or about trivia (a helper's name, where a small helper lives).

### 5. Test — deploying is laborious, testing is essential
Each deploy means building an APK and installing it on the phone by hand, so catch problems before that.

- **The build must pass.** Always build the shipped configuration (below) after a change. It runs R8, so it
  catches things a plain compile does not. Fix failures; never hand over a broken build.
- **Trace the change.** Grep every reference to what you touched. Telegram screens often build their list with
  row counters (`fooRow = rowCount++`); hiding a row means skipping its counter *and* handling every place that
  compares positions against it. A hidden view that other code still dereferences is a crash.
- **Write a test when logic can be tested without a device.** If that needs new test infrastructure, ask first (rule 4).
  Note: the only existing tests are the generated TL-serialization instrumentation tests in `TMessagesProj_AppTests`;
  there are no UI tests to lean on.
- **If something can only be verified on the phone, say so.** Batch changes into as few deploys as possible and
  give the user a short checklist of exactly what to look at. Ask before setting up an emulator.
- **Report verification honestly:** state what was built, tested and observed, and what was not. "Builds, not run
  on a device" is a valid and expected status. Never claim a feature "works" without saying how you know.

### 6. Only the Android app matters
Everything else can break. This fork only needs the **`TMessagesProj_AppStandalone`** variant to build and run.

- Ignore the other flavors/modules (`TMessagesProj_App`, `_AppHuawei`, `_AppHockeyApp`, `_AppTests`),
  Play/Huawei store concerns, the `Dockerfile`, `Tools/`, the README, and translations. Don't spend effort
  keeping them compiling or consistent, and don't delete them (rule 1).
- Don't add or edit strings in `values-*/strings.xml`. Avoid adding strings at all; if one is unavoidable, ask.

## Project facts

- **Decisions already made:** keep the official logo and the other keys (`google-services.json`, `BuildVars.java`
  besides the app id) as they are. Do not change them or re-ask.
- **App id:** the official `APP_ID = 4` is refused by the server for this build (`API_ID_PUBLISHED_FLOOD`, login does
  nothing). The user's own id from my.telegram.org goes into the gitignored `local.properties` as `TELEGRAM_API_ID` and
  `TELEGRAM_API_HASH`; `TMessagesProj/build.gradle` turns them into `BuildConfig`. Without them the build falls back to
  id 4 and login fails. Never commit the hash. Passkeys are off (`SUPPORTS_PASSKEYS`) because they need an official id.
  If the login code never arrives, `SAFETYNET_KEY` (Firebase SMS) is the next suspect; ask before emptying it.
- **What to remove or keep:** the user's decisions per feature are in `REMOVALS.md`. Read it before removing
  anything, update the status of a row when its commit lands, and do not re-ask what it already decides.
- **Shipped build:** `./gradlew :TMessagesProj_AppStandalone:assembleAfatStandalone`.
  Application id `org.telegram.messenger.web` (installs next to the Play Store app). The APK is named `app.apk`
  under that module's `build/outputs/apk/`. Install with `adb install -r app.apk`.
- **Signing:** installs only update in place if signed with the same key and the versionCode does not go down.
  The repo's `TMessagesProj/config/release.keystore` is a public dummy; the user is expected to use their own
  keystore. Never overwrite or lose it, and never commit one.
- **Toolchain:** JDK 17+ (21 is installed), Android platform 36, build-tools 36.0.0, NDK 27.2.12479018,
  CMake 3.22.1 (from the SDK). The Android SDK is not installed yet; ask first.
- **Submodules are not initialized** in this checkout. Before the first build:
  `git submodule update --init --recursive --depth=1` (large download; ask first).
  The native static libraries are already committed under `TMessagesProj/jni/prebuild/lib/`; do not rebuild them
  unless the user asks.
- **Layout:** nearly all code is in `TMessagesProj/src/main/java/org/telegram/` (`ui/`, `messenger/`, `tgnet/`).
  Standalone-only code is in `TMessagesProj_AppStandalone/src/main/java/`.

## Upgrading to a new upstream version

Upstream publishes source drops as single commits ("update to X.Y.Z (code)") on `upstream/master`.

1. `git fetch upstream`, then rebase this fork's commits onto `upstream/master`, then `git submodule update --recursive`.
   Publishing the rebased history to `origin` needs a force-push (`--force-with-lease`), so ask first.
2. For each fork commit that conflicts or no longer applies, re-find the feature's new location and redo the
   *same narrow change*. Do not widen it. Upstream often renames or moves things.
3. Rebuild, then re-check that each removed feature is still gone. Upstream may have added new entry points to it.
4. Do not fall far behind: the server can force old versions to update and eventually drops old API layers.

## Commits
Commit automatically, without asking, once a change is finished and the build passes (rule 5).
Make one commit per removed feature or logical change, with a message that says what disappeared and where it
was gated, so that `git log upstream/master..HEAD` is the list of fork patches. Don't mix unrelated changes in
one commit; don't commit half-done or non-building work.

Committing is pre-approved, **pushing is not**: don't push to `origin` unless the user asks.
