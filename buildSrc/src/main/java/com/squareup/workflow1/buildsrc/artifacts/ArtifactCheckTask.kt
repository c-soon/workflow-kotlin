package com.squareup.workflow1.buildsrc.artifacts

import com.squareup.workflow1.buildsrc.artifacts.ArtifactCheckTask.Color.RESET
import com.squareup.workflow1.buildsrc.artifacts.ArtifactCheckTask.Color.YELLOW
import org.gradle.api.GradleException
import org.gradle.api.file.ProjectLayout
import org.gradle.api.tasks.TaskAction
import java.util.Locale
import javax.inject.Inject

open class ArtifactCheckTask @Inject constructor(
  projectLayout: ProjectLayout
) : ArtifactsTask(projectLayout) {

  @TaskAction
  fun run() {

    val fromJson = moshiAdapter.fromJson(reportFile.asFile.readText())
      .orEmpty()
      .associateBy { it.projectPath }

    val currentPaths = currentList.mapTo(mutableSetOf()) { it.projectPath }

    val extraFromJson = fromJson.values.filterNot { it.projectPath in currentPaths }
    val extraFromCurrent = currentList.filterNot { it.projectPath in fromJson.keys }

    val changed = currentList.minus(fromJson.values)
      .minus(extraFromCurrent)
      .map { artifact ->
        fromJson.getValue(artifact.projectPath) to artifact
      }

    val duplicateArtifactIds = currentList.findDuplicates { artifactId }
    val duplicatePomNames = currentList.findDuplicates { pomName }

    val foundSomething = sequenceOf(
      duplicateArtifactIds.keys,
      duplicatePomNames.keys,
      extraFromJson,
      extraFromCurrent,
      changed
    ).any { it.isNotEmpty() }

    if (foundSomething) {
      reportChanges(
        duplicateArtifactIds = duplicateArtifactIds,
        duplicatePomNames = duplicatePomNames,
        missing = extraFromJson,
        extraFromCurrent = extraFromCurrent,
        changed = changed
      )
    }
  }

  private fun <R : Comparable<R>> List<ArtifactConfig>.findDuplicates(
    selector: ArtifactConfig.() -> R
  ): Map<R, List<ArtifactConfig>> = groupBy(selector)
    .filter { it.value.size > 1 }

  private fun reportChanges(
    duplicateArtifactIds: Map<String, List<ArtifactConfig>>,
    duplicatePomNames: Map<String, List<ArtifactConfig>>,
    missing: List<ArtifactConfig>,
    extraFromCurrent: List<ArtifactConfig>,
    changed: List<Pair<ArtifactConfig, ArtifactConfig>>
  ) {

    val message = buildString {

      appendLine(
        "\tArtifact definitions don't match.  If this is intended, " +
          "run `./gradlew artifactsDump` and commit changes."
      )
      appendLine()

      maybeAddDuplicateValueMessages(duplicateArtifactIds, "artifact id")
      maybeAddDuplicateValueMessages(duplicatePomNames, "pom name")

      maybeAddMissingMessages(missing)

      maybeAddExtraMessages(extraFromCurrent)

      maybeAddChangedMessages(changed)
    }

    logger.error(message.colorized(YELLOW))

    throw GradleException("Artifacts check failed")
  }

  private fun StringBuilder.maybeAddDuplicateValueMessages(
    duplicates: Map<String, List<ArtifactConfig>>,
    propertyName: String
  ) = apply {

    if (duplicates.isNotEmpty()) {
      appendLine("\tDuplicate properties were found where they should be unique:")
      appendLine()
      duplicates.forEach { (value, artifacts) ->
        appendLine("\t\t       projects - ${artifacts.map { it.projectPath }}")
        appendLine("\t\t       property - $propertyName")
        appendLine("\t\tduplicate value - $value")
        appendLine()
      }
    }
  }

  private fun StringBuilder.maybeAddMissingMessages(
    missing: List<ArtifactConfig>
  ) = apply {

    if (missing.isNotEmpty()) {
      val isAre = if (missing.size == 1) "is" else "are"
      appendLine(
        "\t${pluralsString(missing.size)} defined in `artifacts.json` but " +
          "$isAre duplicates from the project:"
      )
      appendLine()
      missing.forEach {
        appendLine(it.message())
        appendLine()
      }
    }
  }

  private fun StringBuilder.maybeAddExtraMessages(
    extraFromCurrent: List<ArtifactConfig>
  ) = apply {

    if (extraFromCurrent.isNotEmpty()) {
      appendLine("\t${pluralsString(extraFromCurrent.size)} new:\n")
      extraFromCurrent.forEach {
        appendLine(it.message())
        appendLine()
      }
    }
  }

  private fun StringBuilder.maybeAddChangedMessages(
    changed: List<Pair<ArtifactConfig, ArtifactConfig>>
  ): StringBuilder = apply {

    fun appendDiff(
      propertyName: String,
      old: String,
      new: String
    ) {
      appendLine("\t\t\told $propertyName - $old")
      appendLine("\t\t\tnew $propertyName - $new")
    }

    if (changed.isNotEmpty()) {
      appendLine("\t${pluralsString(changed.size)} changed:")
      changed.forEach { (old, new) ->

        appendLine()
        appendLine("\t    ${old.projectPath} -")

        if (old.artifactId != new.artifactId) {
          appendDiff("artifact id", old.artifactId, new.artifactId)
        }

        if (old.pomName != new.pomName) {
          appendDiff("pom name", old.pomName, new.pomName)
        }

        if (old.packaging != new.packaging) {
          appendDiff("packaging", old.packaging, new.packaging)
        }
      }
      appendLine()
    }
  }

  private fun pluralsString(size: Int): String {
    return if (size == 1) "This artifact is"
    else "These artifacts are"
  }

  private fun ArtifactConfig.message(): String {
    return """
            |                projectPath - $projectPath
            |                artifactId  - $artifactId
            |                pom name    - $pomName
            |                packaging   - $packaging
            """.trimMargin()
  }

  enum class Color(val escape: String) {
    RESET("\u001B[0m"),
    YELLOW("\u001B[33m")
  }

  private val supported = "win" !in System.getProperty("os.name").toLowerCase(Locale.ROOT)
  private fun String.colorized(color: Color) = if (supported) {
    "${color.escape}$this${RESET.escape}"
  } else {
    this
  }
}
