plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.compose.multiplatform)
  alias(libs.plugins.kotlin.compose)
}

kotlin {
  jvm()
  sourceSets {
    jvmMain.dependencies {
      implementation(project(":sharedUI"))
      implementation(project(":sharedLogic"))
      implementation(project.dependencies.platform(libs.koin.bom))
      implementation(libs.koin.core)
      implementation(compose.desktop.currentOs)
      implementation(libs.kotlinx.coroutines.swing)
    }
  }
}

compose.desktop {
  application {
    mainClass = "eu.slickbot.conver.desktop.MainKt"
  }
}
