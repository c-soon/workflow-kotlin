plugins {
  id("com.android.application")
  kotlin("android")
}

apply(from = rootProject.file(".buildscript/android-sample-app.gradle"))
apply(from = rootProject.file(".buildscript/android-ui-tests.gradle"))

android {
  defaultConfig {
    applicationId = "com.squareup.sample.dungeon"
    multiDexEnabled = true

    testInstrumentationRunner = "com.squareup.sample.dungeon.DungeonTestRunner"
    testInstrumentationRunnerArguments["listener"] = "leakcanary.FailTestOnLeakRunListener"
  }

  compileOptions {
    // Required for SnakeYAML.
    isCoreLibraryDesugaringEnabled = true
  }
}

dependencies {
  // Required for SnakeYAML.
  "coreLibraryDesugaring"(libs.android.desugar)

  debugImplementation(libs.squareup.leakcanary.android)

  implementation(project(":samples:dungeon:common"))
  implementation(project(":samples:dungeon:timemachine-shakeable"))
  implementation(project(":workflow-ui:container-android"))
  implementation(project(":workflow-tracing"))

  implementation(libs.androidx.activity.ktx)
  implementation(libs.androidx.constraintlayout)
  implementation(libs.google.android.material)
  implementation(libs.androidx.gridlayout)
  implementation(libs.kotlinx.coroutines.rx2)
  implementation(libs.squareup.okio)
  implementation(libs.rxjava2.rxandroid)
  implementation(libs.squareup.cycler)

  testImplementation(libs.junit)
  testImplementation(libs.truth)

  androidTestImplementation(libs.squareup.leakcanary.instrumentation)
  androidTestImplementation(libs.androidx.test.uiautomator)
}
