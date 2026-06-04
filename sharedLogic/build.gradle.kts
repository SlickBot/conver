plugins {
  alias(libs.plugins.kotlin.multiplatform)
  id("com.android.kotlin.multiplatform.library")
  alias(libs.plugins.ksp)
  alias(libs.plugins.room)
}

kotlin {
  // Room KMP generates expect/actual RoomDatabaseConstructor objects; suppress the Beta warning.
  compilerOptions {
    freeCompilerArgs.add("-Xexpect-actual-classes")
  }

  jvm()

  android {
    namespace = "eu.slickbot.conver.shared"
    compileSdk = 36
    minSdk = 26
    compilations.all { compileTaskProvider.configure { compilerOptions { jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21) } } }
  }

  @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
  wasmJs {
    browser()
  }

  // NOTE: Room 3.0.0-alpha06 and androidx.sqlite 2.7.0-alpha06 do NOT publish an
  // iosX64 variant (only iosArm64 + iosSimulatorArm64), so iosX64 is omitted here.
  iosArm64()
  iosSimulatorArm64()

  sourceSets {
    commonMain.dependencies {
      implementation(project.dependencies.platform(libs.kotlincrypto.hash.bom))
      implementation(libs.kotlincrypto.hash.md5)
      implementation(libs.kotlincrypto.hash.sha2)
      implementation(libs.androidx.room3.runtime)
      implementation(libs.kotlinx.coroutines.core)
    }
    // sqlite-bundled (JNI/native bundled driver) has no wasmJs variant; the wasm
    // target uses sqlite-web instead. Keep it out of commonMain.
    jvmMain.dependencies {
      implementation(libs.androidx.sqlite.bundled)
    }
    androidMain.dependencies {
      implementation(libs.androidx.sqlite.bundled)
    }
    iosMain.dependencies {
      implementation(libs.androidx.sqlite.bundled)
    }
    val wasmJsMain by getting {
      dependencies {
        implementation(libs.androidx.sqlite.web)
      }
    }
    commonTest.dependencies {
      implementation(kotlin("test"))
    }
  }
}

extensions.configure<androidx.room3.gradle.RoomExtension> {
  schemaDirectory("$projectDir/schemas")
}

dependencies {
  add("kspAndroid", libs.androidx.room3.compiler)
  add("kspJvm", libs.androidx.room3.compiler)
  add("kspWasmJs", libs.androidx.room3.compiler)
  add("kspIosArm64", libs.androidx.room3.compiler)
  add("kspIosSimulatorArm64", libs.androidx.room3.compiler)
}
