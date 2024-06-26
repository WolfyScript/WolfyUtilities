package com.wolfyscript.utilities.bukkit.gui;

import com.wolfyscript.utilities.bukkit.gui.interaction.InventoryGUIInteractionHandler;
import com.wolfyscript.utilities.bukkit.gui.rendering.InventoryGUIRenderer;
import com.wolfyscript.viewportl.gui.*;
import com.wolfyscript.viewportl.gui.interaction.InteractionHandler;
import com.wolfyscript.viewportl.gui.rendering.Renderer;
import com.wolfyscript.utilities.platform.gui.GuiUtils;

public class GuiUtilsImpl implements GuiUtils {

    @Override
    public Renderer<?> createRenderer(ViewRuntime viewRuntime) {
        return new InventoryGUIRenderer((ViewRuntimeImpl) viewRuntime);
    }

    @Override
    public InteractionHandler createInteractionHandler(ViewRuntime viewRuntime) {
        return new InventoryGUIInteractionHandler((ViewRuntimeImpl) viewRuntime);
    }
}
