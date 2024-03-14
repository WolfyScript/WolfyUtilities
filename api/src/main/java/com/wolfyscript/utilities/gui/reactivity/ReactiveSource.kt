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

import com.wolfyscript.utilities.gui.ViewRuntime
import com.wolfyscript.utilities.gui.functions.ReceiverBiConsumer
import com.wolfyscript.utilities.gui.functions.ReceiverFunction
import com.wolfyscript.utilities.gui.functions.SignalableReceiverFunction
import com.wolfyscript.utilities.platform.Platform
import org.apache.commons.lang3.function.TriFunction
import java.util.*
import java.util.function.BiFunction
import java.util.function.Function

interface ReactiveSource {

    fun <T : Any> createSignal(valueType: Class<T>, defaultValueProvider: ReceiverFunction<ViewRuntime, T?>): Signal<T>

    fun <T : Any> createMemo(fn: Function<T?, T?>) : Memo<T>

    /**
     * Creates a Signal with a value, which is stored externally of the GUI.
     *
     * @param storeProvider The data storage where the data exists
     * @param supplier The value getter
     * @param consumer The value setter
     * @return The signal
     * @param <S>
     * @param <T> The type of the value
    </T></S> */
    fun <S, T> createStore(
        storeProvider: ReceiverFunction<ViewRuntime, S>,
        supplier: ReceiverFunction<S, T>,
        consumer: ReceiverBiConsumer<S, T>
    ): Signal<T>

    /**
     * Must be used to fetch data from the main Minecraft thread (i.e. Entities, World, etc.).
     * This is because the GUI is run async on a different thread!
     *
     *
     * After creation, it runs the specified function on the main thread and fetches the data.
     * The signal gets updated when the data is available.
     * When the signal value is requested before the data is available it returns an empty Optional.
     *
     *
     * @param fetch The function to run on the main thread.
     * @return A signal that contains an Optional wrapping the fetched data; empty by default; non-empty when data has been fetched
     * @param <T> The type of the value
    </T> */
    fun <T> resourceSync(fetch: BiFunction<Platform, ViewRuntime, T>): Signal<Optional<T>>

    fun <I, T> resourceSync(
        input: Signal<I>,
        fetch: TriFunction<Platform, ViewRuntime, I, T>
    ): Signal<Optional<T>>

    /**
     * May be used to fetch data async.
     *
     *
     *
     * After creation, it runs the specified function async and fetches the data.
     * The signal gets updated when the data is available.
     * When the signal value is requested before the data is available it returns an empty Optional.
     *
     *
     * @param fetch The function to run async
     * @return A signal that contains an Optional wrapping the fetched data; empty by default; non-empty when data has been fetched
     * @param <T> The type of the value
    </T> */
    fun <T> resourceAsync(fetch: BiFunction<Platform, ViewRuntime, T>): Signal<Optional<T>>

    fun createEffect(effect: SignalableReceiverFunction<*, Unit>): Effect {
        return createEffect(effect)
    }

    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("createTypedEffect")
    fun <T> createEffect(effect: SignalableReceiverFunction<T?, T>): Effect {
        return createEffect(emptyList(), effect)
    }

    fun <T> createEffect(additionalSignals: List<Signal<*>>, effect: SignalableReceiverFunction<T?, T>): Effect
}

inline fun <reified T : Any> ReactiveSource.createSignal(defaultValue: T? = null): Signal<T> {
    return createSignal(T::class.java) { defaultValue }
}

inline fun <reified T : Any> ReactiveSource.createSignal(defaultValueProvider: ReceiverFunction<ViewRuntime, T?>): Signal<T> {
    return createSignal(T::class.java, defaultValueProvider)
}