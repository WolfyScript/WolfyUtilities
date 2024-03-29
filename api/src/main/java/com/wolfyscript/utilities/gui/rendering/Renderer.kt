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
package com.wolfyscript.utilities.gui.rendering

import com.wolfyscript.utilities.gui.Window
import com.wolfyscript.utilities.gui.model.UpdateInformation

interface Renderer<C: RenderContext> {

    fun changeWindow(window: Window)

    fun render()

    /**
     * Called whenever the render graph is updated.
     * @param information info about the update (e.g. which nodes changed).
     */
    fun update(information: UpdateInformation)

}
