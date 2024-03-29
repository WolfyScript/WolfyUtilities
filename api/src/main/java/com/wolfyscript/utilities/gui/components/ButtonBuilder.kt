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
package com.wolfyscript.utilities.gui.components

import com.wolfyscript.utilities.gui.Component
import com.wolfyscript.utilities.gui.ComponentBuilder
import com.wolfyscript.utilities.gui.ItemHelper
import com.wolfyscript.utilities.gui.animation.AnimationBuilder
import com.wolfyscript.utilities.gui.animation.ButtonAnimationFrame
import com.wolfyscript.utilities.gui.animation.ButtonAnimationFrameBuilder
import com.wolfyscript.utilities.gui.callback.InteractionCallback
import com.wolfyscript.utilities.gui.functions.ReceiverConsumer
import com.wolfyscript.utilities.gui.functions.SerializableFunction
import com.wolfyscript.utilities.gui.reactivity.Signal
import com.wolfyscript.utilities.world.items.ItemStackConfig
import net.kyori.adventure.sound.Sound
import java.util.*
import java.util.function.Consumer
import java.util.function.Supplier

/**
 * Builder to create a [Button] instance.
 *
 */
interface ButtonBuilder : ComponentBuilder<Button, Component> {

    /**
     * Creates a new [IconBuilder] to create the icon of the button.
     *
     * @param icon The consumer that provides the [IconBuilder]
     * @return This builder instance for chaining.
     */
    fun icon(icon: ReceiverConsumer<IconBuilder>): ButtonBuilder

    fun interact(interactionCallback: InteractionCallback): ButtonBuilder

    fun sound(soundFunction: Supplier<Optional<Sound>>): ButtonBuilder

    fun animation(animationBuild: ReceiverConsumer<AnimationBuilder<ButtonAnimationFrame, ButtonAnimationFrameBuilder>>): ButtonBuilder

    override fun create(parent: Component?): Button

    /**
     * Provides methods to create an icon for Buttons.
     * Dynamic icons are recreated each time the component is re-rendered, while static icons are just created once and then reused.
     * By default, all icons are static to improve performance.
     */
    interface IconBuilder {
        fun stack(itemId: String, configure: Consumer<ItemStackConfig>): IconBuilder

        fun stack(stackConfigSupplier: SerializableFunction<ItemHelper, ItemStackConfig>): IconBuilder

        fun updateOnSignals(vararg signals: Signal<*>): IconBuilder

        fun create(button: Button): ButtonIcon
    }
}
