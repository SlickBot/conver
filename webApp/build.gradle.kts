import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.compose.multiplatform)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.detekt)
}

kotlin {
  @OptIn(ExperimentalWasmDsl::class)
  wasmJs {
    browser()
    binaries.executable()
    useEsModules()  // worker uses import.meta.url → the whole bundle must be ES modules
  }
  sourceSets {
    wasmJsMain.dependencies {
      implementation(project(":sharedUI"))
      implementation(project(":sharedLogic"))
      implementation(project(":sqliteWasmWorker"))
      implementation(project.dependencies.platform(libs.koin.bom))
      implementation(libs.koin.core)
      implementation(libs.jb.compose.runtime)
      implementation(libs.jb.compose.ui)
      implementation(libs.jb.navigation.compose)  // for bindToBrowserNavigation
    }
  }
}
