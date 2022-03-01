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
  api(project(":workflow-ui:core-common"))
  api(libs.kotlin.jdk6)
  api(libs.squareup.okio)

  testImplementation(libs.kotlin.test.jdk)
  testImplementation(libs.truth)
}
