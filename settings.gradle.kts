pluginManagement {
  repositories {
    google {
      content {
        includeGroupByRegex("com\\.android.*")
        includeGroupByRegex("com\\.google.*")
        includeGroupByRegex("androidx.*")
      }
    }
    mavenCentral()
    gradlePluginPortal()
  }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
  repositories {
    google()
    mavenCentral()
    // Required for Kotlin/Wasm browser target (Node.js and Yarn toolchain downloads)
    ivy("https://nodejs.org/dist") {
      content { includeModule("org.nodejs", "node") }
      patternLayout { artifact("v[revision]/[artifact](-v[revision]-[classifier]).[ext]") }
      metadataSources { artifact() }
    }
    ivy("https://github.com/yarnpkg/yarn/releases/download") {
      content { includeModule("com.yarnpkg", "yarn") }
      patternLayout { artifact("v[revision]/[artifact](-v[revision]).[ext]") }
      metadataSources { artifact() }
    }
    ivy("https://github.com/WebAssembly/binaryen/releases/download") {
      content { includeModule("com.github.webassembly", "binaryen") }
      patternLayout { artifact("version_[revision]/[artifact]-version_[revision]-[classifier].[ext]") }
      metadataSources { artifact() }
    }
  }
}

plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "conver"
include(":androidApp")
include(":sharedLogic")
include(":sharedUI")
include(":desktopApp")
include(":webApp")
include(":iosApp")
include(":sqliteWasmWorker")
