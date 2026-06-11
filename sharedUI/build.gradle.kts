import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.android.kotlin.multiplatform.library)
  alias(libs.plugins.compose.multiplatform)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.detekt)
  alias(libs.plugins.kotlin.serialization)
}

kotlin {
  jvm()

  android {
    namespace = "eu.slickbot.conver.sharedui"
    compileSdk = 36
    minSdk = 23
    compilations.all {
      compileTaskProvider.configure {
        compilerOptions { jvmTarget.set(JvmTarget.JVM_21) }
      }
    }
  }

  @OptIn(ExperimentalWasmDsl::class)
  wasmJs {
    browser()
  }

  iosArm64()
  iosSimulatorArm64()
  iosX64()

  sourceSets {
    commonMain.dependencies {
      implementation(project(":sharedLogic"))
      implementation(libs.jb.compose.runtime)
      implementation(libs.jb.compose.foundation)
      implementation(libs.jb.compose.material3)
      implementation(libs.jb.compose.material.icons.extended)
      implementation(libs.jb.compose.components.resources)
      implementation(libs.jb.compose.ui)
      implementation(libs.jb.compose.adaptive)
      implementation(libs.jb.compose.adaptive.navigation.suite)
      implementation(libs.jb.navigation.compose)
      implementation(libs.jb.lifecycle.viewmodel.compose)
      implementation(libs.jb.lifecycle.runtime.compose)
      implementation(project.dependencies.platform(libs.koin.bom))
      implementation(libs.koin.compose)
      implementation(libs.koin.compose.viewmodel)
    }
  }
}
