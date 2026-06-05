# Release signing & GitHub secrets

### Create keystore

```bash
# Create the release keystore (prompts for a password)
keytool -genkeypair -v \
  -keystore release-key.jks \
  -alias key \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -dname "CN=CHANGEME, O=CHANGEME, C=CHANGEME"
```

### Upload to GitHub Secrets

```bash
# Encode the keystore and upload it as the KEYSTORE_BASE64 secret
base64 -w0 release-key.jks | gh secret set KEYSTORE_BASE64
```

```bash
# Key alias (must match the -alias used above)
gh secret set KEY_ALIAS --body "key"
```

```bash
# Passwords — gh prompts for the value.
gh secret set KEYSTORE_PASSWORD
```

```bash
# Enter the SAME password for both.
gh secret set KEY_PASSWORD
```

### Build a signed release locally

```bash
export KEYSTORE_FILE=/path/to/release-key.jks
export KEYSTORE_PASSWORD=...
export KEY_ALIAS=...
export KEY_PASSWORD=...

./gradlew :androidApp:assembleRelease
```

### Add release tag and push

**Important:** release tags must use strict numeric versions - e.g. `v1.0` or `v1.2.3` -
with no pre-release suffixes such as `-rc1`. The desktop installer packaging tool (jpackage)
derives the installer version from the tag, and it rejects any version string that is not
purely numeric (MAJOR.MINOR or MAJOR.MINOR.PATCH). A tag like `v1.0.0-rc1` will strip to
`1.0.0-rc1` and fail the desktop build.

```bash
# Tag + push → triggers the Release workflow (builds & publishes all platforms)
git tag v1.0
git push origin v1.0
```

---

## What the release workflow builds

Pushing a `v*` tag triggers four parallel jobs (after unit tests pass):

### Android

Builds signed release and debug APKs and attaches them to the GitHub Release,
along with a gzipped R8 mapping file for crash decoding.

### Desktop (Windows / macOS / Linux)

Runs on a 3-OS matrix. Each runner calls
`./gradlew :desktopApp:packageReleaseDistributionForCurrentOS` and attaches the
native installer to the GitHub Release:

| Platform | Format |
|----------|--------|
| Linux    | `.deb` |
| macOS    | `.dmg` |
| Windows  | `.msi` |

No extra secrets are needed for desktop builds.

### Web (Kotlin/Wasm)

Builds the Wasm distribution via `./gradlew :webApp:wasmJsBrowserDistribution`,
attaches a zip of the bundle to the GitHub Release, and deploys the same bundle
to GitHub Pages.

**One-time setup required:** in your repository settings, go to
Settings > Pages > Build and deployment > Source and select **GitHub Actions**.
Without this, the deploy step will fail even though the build succeeds.
