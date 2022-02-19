plugins {
  `kotlin-jvm`
}

dependencies {
  implementation(project(":workflow-core"))

  implementation(libs.kotlin.jdk8)

  testImplementation(libs.kotlin.test.jdk)
  testImplementation(libs.hamcrest)
  testImplementation(libs.junit)
  testImplementation(libs.truth)
  testImplementation(project(":workflow-testing"))
}
