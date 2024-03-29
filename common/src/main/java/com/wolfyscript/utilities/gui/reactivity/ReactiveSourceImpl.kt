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

import com.google.common.collect.Multimaps
import com.google.common.collect.SetMultimap
import com.wolfyscript.utilities.gui.ViewRuntime
import com.wolfyscript.utilities.gui.ViewRuntimeImpl
import com.wolfyscript.utilities.gui.functions.ReceiverBiConsumer
import com.wolfyscript.utilities.gui.functions.ReceiverFunction
import com.wolfyscript.utilities.gui.functions.SignalableReceiverFunction
import com.wolfyscript.utilities.platform.Platform
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import org.apache.commons.lang3.function.TriFunction
import java.util.*
import java.util.function.BiFunction
import java.util.function.Function
import kotlin.reflect.KClass

class ReactiveSourceImpl(private val viewRuntime: ViewRuntimeImpl) : ReactiveSource {

    private var owner: NodeId?
    private var observer: NodeId? = null

    // Graph
    private val nodes: MutableMap<NodeId, ReactivityNode<*>> = Object2ObjectOpenHashMap()
    private val rootNodes: MutableMap<NodeId, ReactivityNode<*>> = Object2ObjectOpenHashMap()
    private val nodeSubscribers: SetMultimap<NodeId, NodeId> =
        Multimaps.newSetMultimap(mutableMapOf()) { mutableSetOf() }
    private val nodeSources: SetMultimap<NodeId, NodeId> = Multimaps.newSetMultimap(mutableMapOf()) { mutableSetOf() }

    // Effects that need to be updated
    private val pendingEffects: MutableList<NodeId> = ArrayList()

    init {
        owner = createNode(object : ReactivityNode.Type.Trigger {}, null)
    }

    internal fun owner(): Trigger? {
        return owner?.let { TriggerImpl(it) }
    }

    private fun markClean(nodeId: NodeId) {
        val node = nodes[nodeId] ?: return
        node.mark(ReactivityNode.State.CLEAN)
    }

    /**
     * A depth-first DAG (Direct Acyclic Graph) with its origin at the specified node.
     *
     * It recursively marks all child nodes as [ReactivityNode.State.CHECK].
     * If a node is already [ReactivityNode.State.DIRTY] then we mark it as visited ([ReactivityNode.State.DIRTY_MARKED]).
     *
     * We do this like Leptos here. Instead of pushing each and every node onto the stack, we push the iterators of child nodes.
     */
    fun markDirty(node: NodeId) {
        val currentNode = nodes[node] ?: return
        mark(currentNode, ReactivityNode.State.DIRTY)

        val children = nodeSubscribers[node]
        val stack: ArrayDeque<Iterator<NodeId>> = ArrayDeque(children.size)
        stack.push(children.iterator())

        while (stack.isNotEmpty()) {
            val childIterator = stack.first()
            val (iterResult, iter) = iterateChildren(childIterator)
            when (iterResult) {
                IterResult.CONTINUE -> continue
                IterResult.NEW -> iter?.let { stack.push(it) }
                IterResult.EMPTY -> stack.pop()
            }
        }
    }

    private fun iterateChildren(childIterator: Iterator<NodeId>): Pair<IterResult, Iterator<NodeId>?> {
        if (!childIterator.hasNext()) {
            return Pair(IterResult.EMPTY, null) // When the iterator is done we remove from the stack
        }
        var child = childIterator.next()

        while (nodes[child] != null) {
            val childNode = nodes[child]!!

            if (childNode.state() == ReactivityNode.State.CHECK || childNode.state() == ReactivityNode.State.DIRTY_MARKED) {
                return Pair(IterResult.CONTINUE, null)
            }

            mark(childNode, ReactivityNode.State.CHECK)

            val childsChildren = nodeSubscribers[child]

            if (childsChildren.isNotEmpty()) {
                if (childsChildren.size == 1) {
                    // No need to iterate over a single element
                    child = childsChildren.elementAt(0)
                    continue
                }
                return Pair(IterResult.NEW, childsChildren.iterator())
            }
            break
        }
        return Pair(IterResult.CONTINUE, null)
    }

    private fun mark(node: ReactivityNode<*>, state: ReactivityNode.State) {
        if (state > node.state()) {
            node.mark(state)
        }

        if (node.type is ReactivityNode.Type.Effect && node.id != observer) {
            pendingEffects.add(node.id)
        }

        if (node.state() == ReactivityNode.State.DIRTY) {
            node.mark(ReactivityNode.State.DIRTY_MARKED)
        }
    }

    fun runEffects() {
        for (pendingEffect in pendingEffects) {
            updateIfNecessary(pendingEffect)
        }
        pendingEffects.clear()
    }

    fun updateIfNecessary(nodeId: NodeId) {
        if (currentNodeState(nodeId) == ReactivityNode.State.CHECK) {
            for (source in nodeSources[nodeId]) {
                updateIfNecessary(source)

                if (currentNodeState(nodeId) >= ReactivityNode.State.DIRTY) {
                    break
                }
            }
        }

        if (currentNodeState(nodeId) >= ReactivityNode.State.DIRTY) {
            update(nodeId)
        }
        markClean(nodeId)
    }

    private fun update(nodeId: NodeId) {
        val node = nodes[nodeId] ?: return

        val changed = node.update(viewRuntime)
        if (changed) {
            // Mark the subscribers (children) dirty
            for (subscriber in nodeSubscribers[nodeId]) {
                nodes[subscriber]?.mark(ReactivityNode.State.DIRTY)
            }
        }
        markClean(nodeId)
    }

    fun cleanupSourcesFor(id: NodeId) {
        for (sourceNode in nodeSources[id]) {
            nodeSubscribers[sourceNode].remove(id)
        }
    }

    private fun currentNodeState(nodeId: NodeId): ReactivityNode.State {
        val reactivityNode = nodes[nodeId] ?: return ReactivityNode.State.CLEAN
        return reactivityNode.state()
    }

    fun disposeNode(nodeId: NodeId) {
        nodeSources.removeAll(nodeId)
        nodeSubscribers.removeAll(nodeId)
        nodes.remove(nodeId)
    }

    enum class IterResult {
        CONTINUE,
        NEW,
        EMPTY
    }

    fun untypedNode(id: NodeId): ReactivityNode<*>? {
        return nodes[id]
    }

    inline fun <reified V : ReactivityNode<*>> node(id: NodeId): V? {
        val untyped = untypedNode(id) ?: return null
        if (untyped !is V) {
            throw IllegalStateException("Node ($id) cannot be converted to ${V::class}!")
        }
        return untyped
    }

    inline fun <reified V : Any> getValue(nodeId: NodeId): V? {
        updateIfNecessary(nodeId)
        val reactivityNode = node<ReactivityNode<V>>(nodeId)
        return reactivityNode?.value
    }

    inline fun <reified V : Any> setValue(nodeId: NodeId, value: V?) {
        val reactivityNode = node<ReactivityNode<V>>(nodeId)
        reactivityNode?.value = value
        markDirty(nodeId)
    }

    fun <T : Any> setValue(nodeId: NodeId, valueType: KClass<T>, value: T?) {
        val reactivityNode = node<ReactivityNode<T>>(nodeId)
        reactivityNode?.value = value
        markDirty(nodeId)
    }

    private fun <V> createNode(
        type: ReactivityNode.Type<V>,
        initialValue: V?,
        state: ReactivityNode.State = ReactivityNode.State.CLEAN
    ): NodeId {
        val id = NodeId((nodes.size + 1).toLong(), viewRuntime)

        val reactivityNode: ReactivityNode<*> = ReactivityNode(id, initialValue, type, state)
        nodes[id] = reactivityNode

        if (type is ReactivityNode.Type.Signal) {
            rootNodes[id] = reactivityNode
        }

        return id
    }

    override fun createTrigger(): Trigger {
        val id = createNode(object : ReactivityNode.Type.Trigger {}, null)
        return TriggerImpl(id)
    }

    override fun <T : Any> createSignal(
        valueType: Class<T>,
        defaultValueProvider: ReceiverFunction<ViewRuntime, T>
    ): Signal<T> {
        val id =
            createNode(object : ReactivityNode.Type.Signal<T> {}, with(defaultValueProvider) { viewRuntime.apply() })
        return SignalImpl(id, valueType.kotlin)
    }

    override fun <T> createEffect(effect: ReceiverFunction<T?, T>): Effect {
        return createCustomEffect(null, EffectState(effect))
    }

    fun <T> createCustomEffect(value: T?, effect: AnyComputation<T?>): Effect {
        val id = createNode(
            object : ReactivityNode.Type.Effect<T> {
                override fun computation(): AnyComputation<T?> = effect
            },
            value,
            ReactivityNode.State.DIRTY
        )

        pendingEffects.add(id)

        return EffectImpl(id)
    }

    override fun <T : Any> createMemo(valueType: Class<T>, fn: Function<T?, T?>): Memo<T> {
        val reactivityNodeId = createNode(object : ReactivityNode.Type.Memo<T> {

            override fun computation(): AnyComputation<T?> = MemoState {
                val newValue = fn.apply(it)
                Pair(newValue, newValue != it)
            }

        }, null, ReactivityNode.State.DIRTY)

        return MemoImpl(reactivityNodeId, valueType.kotlin)
    }

    override fun <T> resourceSync(fetch: BiFunction<Platform, ViewRuntime, T>): Signal<Optional<T>> {
        TODO("Not yet implemented!")
    }

    override fun <I, T> resourceSync(
        input: Signal<I>,
        fetch: TriFunction<Platform, ViewRuntime, I, T>
    ): Signal<Optional<T>> {
        TODO("Not yet implemented!")
    }

    override fun <T> resourceAsync(fetch: BiFunction<Platform, ViewRuntime, T>): Signal<Optional<T>> {
        TODO("Not yet implemented!")
    }

    fun subscribe(node: NodeId) {
        if (observer != null) {
            nodeSubscribers[node].add(observer)
            nodeSources[observer].add(node)
        } else {
            throw IllegalStateException("")
        }
    }

    fun runWithObserver(nodeId: NodeId, fn: Runnable) {
        val previousObserver = observer
        val previousOwner = owner

        observer = nodeId
        owner = nodeId
        fn.run()

        observer = previousObserver
        owner = previousOwner
    }
}
