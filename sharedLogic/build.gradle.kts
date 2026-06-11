import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.android.kotlin.multiplatform.library)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.detekt)
}

kotlin {
  compilerOptions {
    freeCompilerArgs.add("-Xexpect-actual-classes")
    optIn.add("com.russhwolf.settings.ExperimentalSettingsApi")
    optIn.add("kotlin.uuid.ExperimentalUuidApi")
    optIn.add("kotlin.io.encoding.ExperimentalEncodingApi")
  }

  jvm()

  android {
    namespace = "eu.slickbot.conver.shared"
    compileSdk = 36
    minSdk = 23
    compilations.all { compileTaskProvider.configure { compilerOptions { jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21) } } }
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
      implementation(project.dependencies.platform(libs.kotlincrypto.hash.bom))
      implementation(libs.kotlincrypto.hash.md5)
      implementation(libs.kotlincrypto.hash.sha1)
      implementation(libs.kotlincrypto.hash.sha2)
      implementation(libs.kotlinx.coroutines.core)
      implementation(libs.kotlinx.serialization.json)
      implementation(libs.kotlinx.datetime)
      implementation(libs.multiplatform.settings)
      implementation(libs.multiplatform.settings.coroutines)
      implementation(libs.jb.lifecycle.viewmodel)
      implementation(project.dependencies.platform(libs.koin.bom))
      implementation(libs.koin.core)
      implementation(libs.koin.core.viewmodel)
    }
    // StorageSettings (browser localStorage) is not ObservableSettings, so wrap it with
    // make-observable; the Android/JVM/iOS Settings implementations are already observable.
    wasmJsMain.dependencies {
      implementation(libs.multiplatform.settings.make.observable)
    }
    commonTest.dependencies {
      implementation(kotlin("test"))
      implementation(libs.kotlinx.coroutines.test)
      implementation(libs.turbine)
    }
    jvmTest.dependencies {
      implementation(project.dependencies.platform(libs.koin.bom))
      implementation(libs.koin.test)
    }
  }
}
