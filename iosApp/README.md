# Conver — iOS app

The iOS app wraps the **`ConverApp`** Kotlin/Native framework, which is built by the `:iosApp`
Gradle module from [`src/iosMain/.../MainViewController.kt`](src/iosMain/kotlin/eu/slickbot/conver/ios/MainViewController.kt).
`MainViewController()` starts Koin once and returns a `ComposeUIViewController` rendering the
shared Compose `App()` — the exact same UI as Android / Desktop / Web.

This scaffold is authored on Linux. **It must be generated, built, and run on macOS with Xcode.**
The `.xcodeproj` is generated from [`project.yml`](project.yml) by [XcodeGen](https://github.com/yonaskolb/XcodeGen)
and is gitignored — `project.yml` is the source of truth.

## Build & run (on a Mac)

```sh
# one-time tooling
brew install xcodegen                 # generates the .xcodeproj from project.yml
# also required: Xcode + Command Line Tools, and a JDK 21 on PATH for Gradle

# from the repo root
cd iosApp
xcodegen generate                     # creates iosApp.xcodeproj
open iosApp.xcodeproj
```

In Xcode: select the **iosApp** scheme and an **arm64 iOS Simulator** (e.g. iPhone 15) or a device, then **Run**.

The **"Compile Kotlin Framework (Gradle)"** pre-build phase automatically runs
`./gradlew :iosApp:embedAndSignAppleFrameworkForXcode`, which compiles `ConverApp.framework`
for the selected configuration/SDK and places it under
`iosApp/build/xcode-frameworks/$(CONFIGURATION)/$(SDK_NAME)` (on the `FRAMEWORK_SEARCH_PATHS`).

## Persistence on iOS

Same shared code as the other targets; per-platform seams in
[`AppDataFactories.ios.kt`](../sharedLogic/src/iosMain/kotlin/eu/slickbot/conver/data/AppDataFactories.ios.kt):
- **Room** via the bundled SQLite driver at `NSDocumentDirectory/conver.db`
- **DataStore** at `NSDocumentDirectory/conver.preferences_pb`

## Notes / troubleshooting

- **Apple-silicon only for the simulator.** Targets are `iosArm64` + `iosSimulatorArm64`; `iosX64`
  (Intel simulator) is intentionally omitted because Room 3.0.0-alpha06 / androidx.sqlite
  2.7.0-alpha06 don't publish that variant yet. Run on an Apple-silicon Mac / arm64 simulator.
- **Deployment target** is iOS 15. Bump in `project.yml` if a dependency requires higher.
- **`ENABLE_USER_SCRIPT_SANDBOXING = NO`** lets the Gradle build phase write the framework
  (Xcode 15+ sandboxes script phases by default).
- **Linker can't find `ConverApp`?** Confirm the build phase ran and that
  `iosApp/build/xcode-frameworks/$(CONFIGURATION)/$(SDK_NAME)/ConverApp.framework` exists; that dir
  must be on `FRAMEWORK_SEARCH_PATHS`.
- **This was scaffolded on Linux and not built here** — the Kotlin/Native klibs compile on Linux,
  but linking the framework and building the `.app` require macOS/Xcode. Expect to do minor
  first-build fixups in Xcode (signing team, simulator selection).
