import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.detekt)
}

kotlin {
  js {
    browser()
    useEsModules()
  }
  @OptIn(ExperimentalWasmDsl::class)
  wasmJs {
    browser()
    useEsModules()
  }

  sourceSets {
    commonMain.dependencies {
      api(libs.androidx.sqlite.web)
      implementation(npm("sqlite-wasm-worker", layout.projectDirectory.dir("worker").asFile))
    }
    wasmJsMain.dependencies {
      implementation(libs.kotlinx.browser)
    }
  }
}
