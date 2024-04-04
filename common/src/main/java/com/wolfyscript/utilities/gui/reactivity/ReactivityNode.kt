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

package com.wolfyscript.utilities.gui.reactivity

import com.wolfyscript.utilities.gui.ViewRuntimeImpl

class ReactivityNode<V>(
    val id: NodeId,
    var value: V?,
    val type: Type<V>,
    private var state: State = State.CLEAN
) {

    fun mark(state: State) {
        this.state = state
    }

    fun state() : State {
        return state
    }

    fun update(viewRuntime: ViewRuntimeImpl) : Boolean {
        return type.runUpdate(viewRuntime, this);
    }

    fun subscribe() {
        id.runtime.reactiveSource.subscribe(id)
    }

    override fun toString(): String {
        return "{${id} (${type}): ${value}}"
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other is ReactivityNode<*>) {
            return id == other.id
        }
        return false
    }

    interface Type<T> {

        fun runUpdate(runtime: ViewRuntimeImpl, reactivityNode: ReactivityNode<T>) : Boolean = true

        interface Trigger : Type<Any>

        interface Signal<T> : Type<T>

        interface Effect<T> : Type<T> {

            override fun runUpdate(runtime: ViewRuntimeImpl, reactivityNode: ReactivityNode<T>): Boolean {
                runtime.reactiveSource.runWithObserver(reactivityNode.id) {
                    runtime.reactiveSource.cleanupSourcesFor(reactivityNode.id)
                    computation().run(runtime, reactivityNode.value) { reactivityNode.value = it }
                }
                return true
            }

            fun computation(): AnyComputation<T?>

        }

        interface Memo<T> : Type<T> {

            override fun runUpdate(runtime: ViewRuntimeImpl, reactivityNode: ReactivityNode<T>): Boolean {
                var changed = false
                runtime.reactiveSource.runWithObserver(reactivityNode.id) {
                    runtime.reactiveSource.cleanupSourcesFor(reactivityNode.id)
                    changed = computation().run(runtime, reactivityNode.value) { reactivityNode.value = it }
                }
                return changed
            }

            fun computation(): AnyComputation<T?>

        }

    }

    enum class State {
        CLEAN,
        CHECK,
        DIRTY,

        DIRTY_MARKED
    }

}