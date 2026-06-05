import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.compose.multiplatform)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.detekt)
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
    nativeDistributions {
      targetFormats(TargetFormat.Deb, TargetFormat.Msi, TargetFormat.Dmg)
      packageName = "Conver"
      packageVersion = (project.findProperty("desktopVersion") as? String) ?: "1.0.0"
    }
    // ProGuard keep-rules are not yet configured for the desktop target;
    // disable obfuscation so the release distributable builds cleanly.
    // Safe to re-enable once proper keep rules are in place.
    buildTypes.release.proguard {
      isEnabled.set(false)
    }
  }
}
