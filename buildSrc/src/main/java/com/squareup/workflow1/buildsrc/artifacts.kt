package com.squareup.workflow1.buildsrc

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

open class ArtifactCheckTask : DefaultTask() {

  @get:OutputFile
  val reportFile: RegularFileProperty = project.objects.fileProperty()
    .convention { project.file("artifacts.json") }

  @TaskAction
  fun run() {

    val moshi = Moshi.Builder().build()
    val type = Types.newParameterizedType(List::class.java, ArtifactConfig::class.java)

    val fromJson = moshi.adapter<List<ArtifactConfig>>(type)
      .fromJson(reportFile.get().asFile.readText())
      .orEmpty()
      .associateBy { it.projectPath }

    val currentList = project.createArtifactList()

    val currentPaths = currentList.mapTo(mutableSetOf()) { it.projectPath }

    val extraFromJson = fromJson.values.filterNot { it.projectPath in currentPaths }
    val extraFromCurrent = currentList.filterNot { it.projectPath in fromJson.keys }

    val diff = currentList.minus(fromJson.values)
      .minus(extraFromCurrent)
      .map { artifact ->
        fromJson.getValue(artifact.projectPath) to artifact
      }

    val message = buildString {

      appendLine(
        "Artifact definitions don't match.  If this is intended, " +
          "run `./gradlew artifactsDump` and commit changes."
      )
      appendLine()

      fun ArtifactConfig.message() : String {
        return """
            |  projectPath: $projectPath
            |  artifactId : $artifactId
            |  pom name : $pomName
            |  packaging : $packaging
            """.trimMargin()
      }

      if (extraFromJson.isNotEmpty()) {
        appendLine("These artifacts are defined in `artifacts.json` but are missing from the project:\n")
        extraFromJson.forEach {
          appendLine(it.toString())
          appendLine()
        }
      }
      if (extraFromCurrent.isNotEmpty()) {
        appendLine("These artifacts are new:\n")
        extraFromCurrent.forEach {
          appendLine(it.toString())
          appendLine()
        }
      }
      if (diff.isNotEmpty()) {
        appendLine("These artifacts are changed:\n")
        diff.forEach { (old, new) ->
          appendLine("old:")
          appendLine(old.toString())
          appendLine()
          appendLine("new:")
          appendLine(new.toString())
          appendLine()
        }
      }
    }

    logger.error(message)

    throw GradleException("Artifacts check failed")
  }
}

open class ArtifactDumpTask : DefaultTask() {

  @get:OutputFile
  val reportFile: RegularFileProperty = project.objects.fileProperty()
    .convention { project.file("artifacts.json") }

  @TaskAction
  fun run() {

    val artifacts = project.createArtifactList()

    val moshi = Moshi.Builder().build()

    val type = Types.newParameterizedType(List::class.java, ArtifactConfig::class.java)

    val json = moshi.adapter<List<ArtifactConfig>>(type)
      .indent("  ")
      .toJson(artifacts)

    reportFile.get().asFile.writeText(json)
  }
}

@JsonClass(generateAdapter = true)
data class ArtifactConfig(
  val projectPath: String,
  val artifactId: String,
  val pomName: String,
  val packaging: String
)

private fun Project.createArtifactList(): List<ArtifactConfig> {

  val map = subprojects
    .mapNotNull { sub ->

      val artifactId = sub.properties["POM_ARTIFACT_ID"] as? String
      val pomName = sub.properties["POM_NAME"] as? String
      val packaging = sub.properties["POM_PACKAGING"] as? String

      listOfNotNull(artifactId, pomName, packaging)
        .also { allProperties ->

          require(allProperties.isEmpty() || allProperties.size == 3) {
            "expected all properties to be null or none to be null for project `${sub.path}, " +
              "but got:\n" +
              "artifactId : $artifactId\n" +
              "pom name : $pomName\n" +
              "packaging : $packaging"
          }
        }
        .takeIf { it.isNotEmpty() }
        ?.let { (artifactId, pomName, packaging) ->
          ArtifactConfig(
            projectPath = sub.path,
            artifactId = artifactId,
            pomName = pomName,
            packaging = packaging
          )
        }
    }

  return map
}

private fun compare(
  expected: Map<String, ArtifactConfig>,
  actual: Map<String, ArtifactConfig>
) {
}
