@Suppress("UnstableApiUsage")
plugins {
  `kotlin-dsl`
  alias(libs.plugins.google.ksp)
}

repositories {
  mavenCentral()
}

dependencies {

  implementation(libs.squareup.moshi)
  implementation(libs.squareup.moshi.adapters)
  ksp(libs.squareup.moshi.codegen)
}
