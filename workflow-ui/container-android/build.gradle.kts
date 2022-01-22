plugins {
  id("com.android.library")
  kotlin("android")
  id("org.jetbrains.dokka")
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

apply(from = rootProject.file(".buildscript/configure-maven-publish.gradle"))
apply(from = rootProject.file(".buildscript/configure-android-defaults.gradle"))
apply(from = rootProject.file(".buildscript/android-ui-tests.gradle"))

dependencies {
  api(project(":workflow-core"))
  api(project(":workflow-ui:core-android"))
  api(project(":workflow-ui:container-common"))

  api(libs.androidx.transition)
  api(libs.kotlin.jdk6)

  implementation(project(":workflow-runtime"))
  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.activity.core)
  implementation(libs.androidx.fragment.core)
  implementation(libs.androidx.savedstate)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)

  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.kotlin.test.jdk)
  testImplementation(libs.mockito.kotlin)
  testImplementation(libs.junit)
  testImplementation(libs.truth)
  testImplementation(libs.androidx.test.core)
  testImplementation(libs.robolectric)

  androidTestImplementation(libs.truth)
}
