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
      implementation(compose.desktop.currentOs)
    }
  }
}

compose.desktop {
  application {
    mainClass = "eu.slickbot.conver.desktop.MainKt"
  }
}
