package com.squareup.workflow1.ui.container

import com.squareup.workflow1.ui.AliasScreen
import com.squareup.workflow1.ui.Compatible
import com.squareup.workflow1.ui.Screen
import com.squareup.workflow1.ui.ViewEnvironment
import com.squareup.workflow1.ui.ViewRegistry
import com.squareup.workflow1.ui.WorkflowUiExperimentalApi
import com.squareup.workflow1.ui.merge

/**
 * Pairs a [actual] rendering with a [viewEnvironment] to support its display.
 * Typically the rendering type (`RenderingT`) of the root of a UI workflow,
 * but can be used at any point to modify the [ViewEnvironment] received from
 * a parent view.
 */
@WorkflowUiExperimentalApi
public class EnvironmentScreen<V : Screen>(
  public override val actual: V,
  public val viewEnvironment: ViewEnvironment = ViewEnvironment.EMPTY
) : AliasScreen {
  override fun resolve(viewEnvironment: ViewEnvironment): Pair<ViewEnvironment, Screen> {
    return super.resolve(this.viewEnvironment merge viewEnvironment)
  }
}

/**
 * Returns an [EnvironmentScreen] derived from the receiver, whose
 * [EnvironmentScreen.viewEnvironment] includes [viewRegistry].
 *
 * If the receiver is an [EnvironmentScreen], uses [ViewRegistry.merge]
 * to preserve the [ViewRegistry] entries of both.
 */
@WorkflowUiExperimentalApi
public fun Screen.withRegistry(viewRegistry: ViewRegistry): EnvironmentScreen<*> {
  return withEnvironment(ViewEnvironment.EMPTY merge viewRegistry)
}

/**
 * Returns an [EnvironmentScreen] derived from the receiver,
 * whose [EnvironmentScreen.viewEnvironment] includes the values in the given [environment].
 *
 * If the receiver is an [EnvironmentScreen], uses [ViewEnvironment.merge]
 * to preserve the [ViewRegistry] entries of both.
 */
@WorkflowUiExperimentalApi
public fun Screen.withEnvironment(
  environment: ViewEnvironment = ViewEnvironment.EMPTY
): EnvironmentScreen<*> {
  return when (this) {
    is EnvironmentScreen<*> -> {
      if (environment.map.isEmpty()) this
      else EnvironmentScreen(actual, this.viewEnvironment merge environment)
    }
    else -> EnvironmentScreen(this, environment)
  }
}
