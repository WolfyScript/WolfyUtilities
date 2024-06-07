package com.wolfyscript.utilities.gui

import com.fasterxml.jackson.databind.InjectableValues
import com.google.common.base.Preconditions
import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import com.google.common.collect.Multimap
import com.google.common.collect.MultimapBuilder
import com.wolfyscript.jackson.dataformat.hocon.HoconMapper
import com.wolfyscript.utilities.WolfyUtils
import com.wolfyscript.utilities.functions.ReceiverConsumer
import com.wolfyscript.utilities.gui.router.Router
import it.unimi.dsi.fastutil.longs.Long2ObjectMap
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import java.io.File
import java.io.IOException
import java.util.*
import java.util.function.Consumer
import java.util.function.Function
import java.util.regex.Pattern
import java.util.stream.Stream
import kotlin.collections.set

class GuiAPIManagerImpl(private val wolfyUtils: WolfyUtils) : GuiAPIManager {
    private val entriesMap: BiMap<String, Function<ViewRuntime, Window>> = HashBiMap.create()

    private var guiDataSubFolder: File

    private val VIEW_RUNTIMES: Long2ObjectMap<ViewRuntime> = Long2ObjectOpenHashMap()
    private val CACHED_VIEW_RUNTIMES: Multimap<String, Long> = MultimapBuilder.hashKeys().hashSetValues().build()
    private val VIEW_RUNTIMES_PER_PLAYER: Multimap<UUID, Long> = MultimapBuilder.hashKeys().hashSetValues().build()

    init {
        this.guiDataSubFolder = File(wolfyUtils.dataFolder, "gui")
        // TODO: Customization
    }

    override fun getViewManagersFor(uuid: UUID): Stream<ViewRuntime> {
        return VIEW_RUNTIMES_PER_PLAYER[uuid].stream().map { VIEW_RUNTIMES[it] }
    }

    override fun getViewManagersFor(uuid: UUID, guiID: String): Stream<ViewRuntime> {
        val ids = CACHED_VIEW_RUNTIMES[guiID]
        return VIEW_RUNTIMES_PER_PLAYER[uuid].stream()
            .filter { ids.contains(it) }
            .map { VIEW_RUNTIMES[it] }
    }

    override fun registerGui(key: String, windowConsumer: ReceiverConsumer<Window>) {
        // TODO: maybe wrap in an extra object?
        registerGui(key) { runtime ->
            val buildContext = BuildContext(runtime, (runtime as ViewRuntimeImpl).reactiveSource, wolfyUtils)
            val window: Window = WindowImpl(key, 54, null, wolfyUtils, buildContext)
            with(windowConsumer) { window.consume() }
            window
        }
    }

    override fun createViewAndThen(guiId: String, callback: Consumer<ViewRuntime>, vararg viewers: UUID) {
        getGui(guiId).ifPresent { constructor ->
            val viewerSet = mutableSetOf(*viewers)
            val viewManagersForID = CACHED_VIEW_RUNTIMES[guiId]
            val runtime = viewManagersForID.map { VIEW_RUNTIMES[it] }.firstOrNull { it.viewers == viewerSet }
            if (runtime != null) {
                callback.accept(runtime)
            } else {
                // Construct the new view manager async, so it doesn't affect the main thread!
                wolfyUtils.core.platform.scheduler.asyncTask(wolfyUtils) {
                    val viewManager = ViewRuntimeImpl(wolfyUtils, constructor, viewerSet)
                    synchronized(VIEW_RUNTIMES) {
                        viewManagersForID.add(viewManager.id)
                        VIEW_RUNTIMES.put(viewManager.id, viewManager)
                    }
                    synchronized(VIEW_RUNTIMES_PER_PLAYER) {
                        for (viewer in viewerSet) {
                            VIEW_RUNTIMES_PER_PLAYER.put(viewer, viewManager.id)
                        }
                    }
                    callback.accept(viewManager)
                }
            }
        }
    }

    private fun registerGui(id: String, constructor: Function<ViewRuntime, Window>) {
        entriesMap[id] = constructor
    }

    override fun createViewAndOpen(guiID: String, vararg viewers: UUID) {
        createViewAndThen(guiID, { it.open() }, *viewers)
    }

    override fun getGui(id: String): Optional<Function<ViewRuntime, Window>> {
        return Optional.ofNullable(
            entriesMap[id]
        )
    }

}
