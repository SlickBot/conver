import io.gitlab.arturbosch.detekt.extensions.DetektExtension

plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.android.library) apply false
  alias(libs.plugins.android.kotlin.multiplatform.library) apply false
  alias(libs.plugins.kotlin.multiplatform) apply false
  alias(libs.plugins.kotlin.compose) apply false
  alias(libs.plugins.kotlin.serialization) apply false
  alias(libs.plugins.compose.multiplatform) apply false
  alias(libs.plugins.ksp) apply false
  alias(libs.plugins.room) apply false
  alias(libs.plugins.detekt) apply false
}

subprojects {
  pluginManager.withPlugin("io.gitlab.arturbosch.detekt") {
    extensions.configure<DetektExtension> {
      buildUponDefaultConfig = true
      config.from(rootProject.files("config/detekt/detekt.yml"))
      baseline = rootProject.file("config/detekt/baselines/detekt-baseline-${project.name}.xml")
      parallel = true
      source.from(
        "src/commonMain/kotlin",
        "src/jvmMain/kotlin",
        "src/androidMain/kotlin",
        "src/wasmJsMain/kotlin",
        "src/iosMain/kotlin",
        "src/commonTest/kotlin",
        "src/main/kotlin",
        "src/test/kotlin",
        "src/jvmTest/kotlin"
      )
    }

    val libs = rootProject.extensions.getByType<VersionCatalogsExtension>().named("libs")
    dependencies {
      add("detektPlugins", libs.findLibrary("detekt-formatting").get())
      add("detektPlugins", libs.findLibrary("detekt-compose-rules").get())
    }
  }
}
