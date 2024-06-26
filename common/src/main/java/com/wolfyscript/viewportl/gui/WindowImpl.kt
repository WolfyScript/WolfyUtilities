/*
 *       WolfyUtilities, APIs and Utilities for Minecraft Spigot plugins
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.wolfyscript.viewportl.gui

import com.fasterxml.jackson.annotation.JacksonInject
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.common.base.Preconditions
import com.wolfyscript.utilities.KeyedStaticId
import com.wolfyscript.utilities.WolfyUtils
import com.wolfyscript.utilities.functions.ReceiverConsumer
import com.wolfyscript.utilities.functions.ReceiverFunction
import com.wolfyscript.viewportl.gui.callback.TextInputCallback
import com.wolfyscript.viewportl.gui.callback.TextInputTabCompleteCallback
import com.wolfyscript.viewportl.gui.reactivity.ReactiveSource
import com.wolfyscript.viewportl.gui.router.Router
import com.wolfyscript.viewportl.gui.router.RouterImpl
import com.wolfyscript.utilities.platform.scheduler.Task
import com.wolfyscript.utilities.tuple.Pair
import net.kyori.adventure.text.Component

@KeyedStaticId(key = "window")
class WindowImpl internal constructor(
    @JsonProperty("id") override val id: String,
    @JsonProperty("size") override var size: Int?,
    @JsonProperty("type") override val type: WindowType? = null,
    @JacksonInject("wolfyutils") override val wolfyUtils: WolfyUtils,
    @JacksonInject("context") private val context: BuildContext,
) :
    Window,
    ReactiveSource by context.reactiveSource {
    override var title: Component? = null
    override val router: Router = RouterImpl(wolfyUtils, context, this)
    override var resourcePath: String? = null

    override var onTextInput: TextInputCallback? = null
    override var onTextInputTabComplete: TextInputTabCompleteCallback? = null

    // Intervalls
    private val intervalRunnables: List<Pair<Runnable, Long>> = ArrayList()
    private val intervalTasks: MutableList<Task> = ArrayList()

    init {
        Preconditions.checkArgument(size != null || type != null, "Either type or size must be specified!")
    }

    override fun title(titleUpdate: ReceiverFunction<Component?, Component?>) {
        context.reactiveSource.createEffect {
            title = with(titleUpdate) { title.apply() }

            (context.runtime as ViewRuntimeImpl).renderer.updateTitle(title)
        }
    }

    override fun open() {
        for (intervalTask in intervalTasks) {
            intervalTask.cancel()
        }
        intervalTasks.clear()
        for (intervalRunnable in intervalRunnables) {
            val task = wolfyUtils.core.platform.scheduler.task(wolfyUtils)
                .interval(intervalRunnable.value)
                .delay(1).execute(intervalRunnable.key).build()
            intervalTasks.add(task)
        }
        router.open()
    }

    override fun close() {
        for (intervalTask in intervalTasks) {
            intervalTask.cancel()
        }
        intervalTasks.clear()

//        (context.runtime as ViewRuntimeImpl).renderingGraph.removeNode(0)
    }

    override fun routes(routerConfiguration: ReceiverConsumer<Router>) {
        with(routerConfiguration) { router.consume() }
    }

    override fun width(): Int {
        return size?.div(height()) ?: 9
    }

    override fun height(): Int {
        return size?.div(9) ?: 1
    }

    override fun onTextInput(inputCallback: TextInputCallback?) {
        this.onTextInput = inputCallback
    }

    override fun onTextInputTabComplete(textInputTabCompleteCallback: TextInputTabCompleteCallback?) {
        this.onTextInputTabComplete = textInputTabCompleteCallback
    }

}
