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

package com.wolfyscript.viewportl.gui.reactivity;

import java.util.function.Function;

/**
 * Keeps track of value changes and updates Components listening to a Signal accordingly.
 * Signals are shared across all children of the Component that creates the Signal,
 * meaning that children can listen to that of the parent, but not the other way around!
 *
 * @param <V> The value type this Signal holds. Can be any Object.
 */
public interface Signal<V> extends SignalTaggable, SignalLike<V> {

    /**
     * Sets the tracked value to a new value and causes a re-render.
     *
     * @param newValue The new value to apply.
     */
    void set(V newValue);

    /**
     * Gets the tracked value, then updates it, and causes a re-render.
     *
     * @param updateFunction The function to update the value.
     */
    void update(Function<V, V> updateFunction);

}
