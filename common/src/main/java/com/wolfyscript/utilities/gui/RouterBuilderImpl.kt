package com.wolfyscript.utilities.gui

import com.fasterxml.jackson.annotation.*
import com.google.common.base.Preconditions
import com.wolfyscript.utilities.WolfyUtils
import com.wolfyscript.utilities.gui.callback.InteractionCallback
import com.wolfyscript.utilities.gui.functions.ReceiverConsumer
import com.wolfyscript.utilities.gui.reactivity.ReactiveSource

@JsonIgnoreProperties(ignoreUnknown = true)
class RouterBuilderImpl @JsonCreator internal constructor(
    @param:JsonProperty("route") private val route: String,
    @param:JacksonInject("wolfyUtils") private val wolfyUtils: WolfyUtils,
    @param:JacksonInject("context") private val context: BuildContext
) : RouterBuilder, ReactiveSource by context.reactiveSource {
    private val subRouteBuilders: MutableMap<String, RouterBuilder> = HashMap()
    private var windowBuilder: WindowBuilder? = null
    private var interactionCallback =
        InteractionCallback { _, _ -> InteractionResult.def() }

    @JsonSetter("window")
    private fun readWindow(windowBuilder: WindowBuilderImpl) {
        this.windowBuilder = windowBuilder
    }

    @JsonSetter("routes")
    private fun readRoutes(routes: List<RouterBuilderImpl>) {
        for (subRouter in routes) {
            subRouteBuilders[subRouter.route] = subRouter
        }
    }

    override fun interact(interactionCallback: InteractionCallback): RouterBuilder {
        Preconditions.checkArgument(interactionCallback != null)
        this.interactionCallback = interactionCallback
        return this
    }

    override fun route(path: String, subRouteBuilder: ReceiverConsumer<RouterBuilder>): RouterBuilder {
        with(subRouteBuilder) {
            subRouteBuilders.computeIfAbsent(path) { _: String? ->
                RouterBuilderImpl(
                    path,
                    wolfyUtils,
                    context
                )
            }.consume()
        }
        return this
    }

    override fun window(windowBuilder: ReceiverConsumer<WindowBuilder>): RouterBuilder {
        with(windowBuilder) {
            window().consume()
        }
        return this
    }

    override fun window(): WindowBuilder {
        if (windowBuilder == null) {
            windowBuilder = WindowBuilderImpl("", wolfyUtils, context)
        }
        return windowBuilder!!
    }

    override fun create(parent: Router?): Router {
        return RouterImpl(
            route,
            wolfyUtils,
            windowBuilder,
            parent,
            interactionCallback
        )
    }
}
