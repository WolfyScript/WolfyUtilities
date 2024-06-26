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

package com.wolfyscript.viewportl.gui.animation;

import com.wolfyscript.viewportl.gui.*;
import com.wolfyscript.viewportl.gui.components.Component;
import com.wolfyscript.viewportl.gui.animation.AnimationFrame;
import com.wolfyscript.viewportl.gui.animation.AnimationFrameBuilder;
import com.wolfyscript.viewportl.gui.reactivity.Effect;
import com.wolfyscript.viewportl.gui.reactivity.Signal;
import com.wolfyscript.viewportl.gui.rendering.RenderContext;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AnimationImpl<F extends AnimationFrame> extends AnimationCommonImpl<F> {

    AnimationImpl(Component owner, List<? extends AnimationFrameBuilder<F>> animationFrameBuilders, Signal<?> updateSignal) {
        super(owner, animationFrameBuilders, updateSignal);
    }

    public void render(ViewRuntime viewManager, GuiHolder guiHolder, RenderContext context) {

        AtomicInteger frameDelay = new AtomicInteger(0);
        AtomicInteger frameIndex = new AtomicInteger(0);
        viewManager.getWolfyUtils().getCore().getPlatform().getScheduler()
                .task(viewManager.getWolfyUtils())
                .execute(task -> {
                    int delay = frameDelay.getAndIncrement();
                    int frame = frameIndex.get();
                    if (frames().size() <= frame) {
                        task.cancel();
                        if (owner() instanceof Effect) {
                            // TODO
                        }
                        return;
                    }

                    AnimationFrame frameObj = frames().get(frame);
                    if (delay <= frameObj.duration()) {
                        frameObj.render(viewManager, guiHolder, context);
                        return;
                    }
                    frameIndex.incrementAndGet();
                    frameDelay.set(0);
                })
                .interval(1)
                .build();
    }
}
