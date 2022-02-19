package com.squareup.workflow1.buildsrc.artifacts

import org.gradle.api.file.ProjectLayout
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

open class ArtifactDumpTask @Inject constructor(
  projectLayout: ProjectLayout
) : ArtifactsTask(projectLayout) {

  @TaskAction
  fun run() {

    val json = moshiAdapter.indent("  ")
      .toJson(currentList)

    reportFile.asFile.writeText(json)
  }
}
