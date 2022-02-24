package com.squareup.workflow1.ui.container

import com.squareup.workflow1.ui.Compatible
import com.squareup.workflow1.ui.ContainerScreen
import com.squareup.workflow1.ui.Screen
import com.squareup.workflow1.ui.ViewEnvironment
import com.squareup.workflow1.ui.ViewRegistry
import com.squareup.workflow1.ui.WorkflowUiExperimentalApi
import com.squareup.workflow1.ui.merge

/**
 * Pairs a [screen] rendering with a [viewEnvironment] to support its display.
 * Typically the rendering type (`RenderingT`) of the root of a UI workflow,
 * but can be used at any point to modify the [ViewEnvironment] received from
 * a parent view.
 *
 * Use [withEnvironment] or [withRegistry] to create or update instances.
 */
@WorkflowUiExperimentalApi
public class EnvironmentScreen<S : Screen>(
  public val screen: S,
  public override val viewEnvironment: ViewEnvironment
) : ContainerScreen<S>(listOf(screen)), Compatible {
  /**
   * Returns an [EnvironmentScreen] derived from the receiver,
   * whose [EnvironmentScreen.viewEnvironment] includes the values in the given [viewEnvironment].
   *
   * If the receiver is an [EnvironmentScreen], uses [ViewEnvironment.merge]
   * to preserve the [ViewRegistry] entries of both.
   */
  public override fun withEnvironment(viewEnvironment: ViewEnvironment): EnvironmentScreen<S> {
    return if (viewEnvironment.map.isEmpty()) this
    else EnvironmentScreen(screen, this.viewEnvironment merge viewEnvironment)
  }

  /**
   * Ensures that we make the decision to update or replace the root view based on
   * the wrapped [screen].
   */
  override val compatibilityKey: String = Compatible.keyFor(screen, "EnvironmentScreen")
}

/**
 * Returns an [EnvironmentScreen] derived from the receiver, whose
 * [EnvironmentScreen.viewEnvironment] includes [viewRegistry].
 *
 * If the receiver is an [EnvironmentScreen], uses [ViewRegistry.merge]
 * to preserve the [ViewRegistry] entries of both.
 */
@WorkflowUiExperimentalApi
public fun Screen.withRegistry(viewRegistry: ViewRegistry): EnvironmentScreen<out Screen> {
  return withEnvironment(ViewEnvironment.EMPTY merge viewRegistry)
}
