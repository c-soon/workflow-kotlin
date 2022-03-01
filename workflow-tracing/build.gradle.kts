plugins {
  `java-library`
  kotlin("jvm")
  published
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
  compileOnly(libs.jetbrains.annotations)

  api(project(":trace-encoder"))
  api(project(":workflow-runtime"))
  api(libs.kotlin.jdk8)
  api(libs.kotlinx.coroutines.core)

  implementation(libs.squareup.okio)
  implementation(libs.squareup.moshi.adapters)
  implementation(libs.squareup.moshi)

  testImplementation(libs.kotlin.test.jdk)
  testImplementation(libs.mockito.kotlin)
}
