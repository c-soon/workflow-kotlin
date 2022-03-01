package com.squareup.workflow1.buildsrc.artifacts

import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class ArtifactConfig(
  val projectPath: String,
  val artifactId: String,
  val pomName: String,
  val packaging: String,
  val javaVersion: String
) : Serializable
