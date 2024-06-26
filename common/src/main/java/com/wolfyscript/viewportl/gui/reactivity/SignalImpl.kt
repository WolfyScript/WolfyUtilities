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

package com.wolfyscript.viewportl.gui.reactivity

import java.util.*
import java.util.function.Function
import kotlin.reflect.KClass
import kotlin.reflect.safeCast

class SignalImpl<MT : Any>(private val id: NodeId, private val type: KClass<MT>) :
    Signal<MT> {
    private var tagName: String? = null

    override fun tagName(tagName: String) {
        this.tagName = tagName
    }

    override fun tagName(): String {
        return if (tagName == null || tagName!!.isBlank()) ("internal_$id") else tagName!!
    }

    fun id(): NodeId {
        return this.id
    }

    override fun set(newValue: MT) {
        id.runtime.reactiveSource.setValue(id, type, newValue)
    }

    override fun update(function: Function<MT, MT>) {
        val node = id.runtime.reactiveSource.node<ReactivityNode<MT>>(id)
        if (node != null) {
            node.value?.let {
                id.runtime.reactiveSource.setValue(id, type, function.apply(it))
            }
        }
    }

    override fun get(): MT? {
        id.runtime.reactiveSource.subscribe(id)
        return getNoTracking()
    }

    override fun getNoTracking(): MT? {
        val value = id.runtime.reactiveSource.getValue<Any>(id)
        if (type.isInstance(value)) {
            return type.safeCast(value)
        }
        return null
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val signal = other as SignalImpl<*>
        return id == signal.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }
}
