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

package com.wolfyscript.utilities.common.gui;

import java.util.Optional;
import java.util.Set;
import net.kyori.adventure.text.Component;

public interface Window<D extends Data> extends MenuComponent<D> {

    @Override
    MenuComponent<D> parent();

    @Override
    Set<? extends SlotComponent<D>> children();

    /**
     * Gets the type that is configured for this Window.<br>
     * <b>When this is empty, then {@link #getSize()} will return the specified size.</b>
     *
     * @return The specified type; or empty Optional when no type is configured.
     * @see #getSize()
     */
    Optional<WindowType> getType();

    /**
     * Gets the size that is configured for this Window.<br>
     *
     * <b>When this is empty, then {@link #getType()} will return the specified type.</b>
     *
     * @return The specified size: or empty Optional when no size is configured.
     */
    Optional<Integer> getSize();

    /**
     * Creates the title of this window for the specified holder.
     *
     * @param holder The holder to create the title for.
     * @return The title component.
     */
    Component createTitle(GuiHolder<D> holder);

}
