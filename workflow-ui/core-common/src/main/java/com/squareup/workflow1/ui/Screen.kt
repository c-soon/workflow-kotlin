package com.squareup.workflow1.ui

import com.squareup.workflow1.ui.container.EnvironmentScreen

/**
 * Marker interface implemented by renderings that map to a UI system's 2d view class.
 */
@WorkflowUiExperimentalApi
public sealed interface Screen {

  /**
   * Returns an [EnvironmentScreen] derived from the receiver,
   * whose [EnvironmentScreen.viewEnvironment] includes the values in the given [viewEnvironment].
   *
   * If the receiver is an [EnvironmentScreen], uses [ViewEnvironment.merge]
   * to preserve the [ViewRegistry] entries of both.
   */
  public fun withEnvironment(viewEnvironment: ViewEnvironment): EnvironmentScreen<out Screen> {
    require(this is LeafScreen)

    return EnvironmentScreen(this, viewEnvironment)
  }
}

@WorkflowUiExperimentalApi
public interface LeafScreen : Screen

@WorkflowUiExperimentalApi
public abstract class ContainerScreen<S : Screen>(
  public val children: List<S>
) : Screen {
  public abstract val viewEnvironment: ViewEnvironment
}
