@Suppress("UnstableApiUsage")
plugins {
  `kotlin-dsl`
  alias(libs.plugins.google.ksp)
}

repositories {
  mavenCentral()
  google()
}

dependencies {
  compileOnly(gradleApi())

  implementation(libs.android.gradle.plugin)
  implementation(libs.dokka.gradle.plugin)
  implementation(libs.squareup.moshi)
  implementation(libs.squareup.moshi.adapters)
  implementation(libs.vanniktech.publish)

  ksp(libs.squareup.moshi.codegen)
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}
