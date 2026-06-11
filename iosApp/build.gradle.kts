plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.compose.multiplatform)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.detekt)
}

kotlin {
  listOf(iosArm64(), iosSimulatorArm64(), iosX64()).forEach { target ->
    target.binaries.framework {
      baseName = "ConverApp"
      isStatic = true
      export(project(":sharedUI"))
    }
  }
  sourceSets {
    iosMain.dependencies {
      api(project(":sharedUI"))
      implementation(project(":sharedLogic"))
      implementation(project.dependencies.platform(libs.koin.bom))
      implementation(libs.koin.core)
    }
  }
}
