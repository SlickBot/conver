plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.compose.multiplatform)
  alias(libs.plugins.kotlin.compose)
}

kotlin {
  listOf(iosArm64(), iosSimulatorArm64()).forEach { target ->
    target.binaries.framework {
      baseName = "ConverApp"
      isStatic = true
      export(project(":sharedUI"))
    }
  }
  sourceSets {
    iosMain.dependencies {
      api(project(":sharedUI"))
    }
  }
}
