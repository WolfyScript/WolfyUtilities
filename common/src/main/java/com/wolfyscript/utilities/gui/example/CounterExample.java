package com.wolfyscript.utilities.gui.example;

import com.wolfyscript.utilities.gui.GuiAPIManager;
import com.wolfyscript.utilities.gui.GuiViewManager;
import com.wolfyscript.utilities.gui.InteractionResult;
import com.wolfyscript.utilities.gui.WindowDynamicConstructor;
import com.wolfyscript.utilities.gui.components.ButtonBuilder;
import com.wolfyscript.utilities.gui.signal.Signal;
import com.wolfyscript.utilities.gui.signal.Store;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

/**
 * A Counter GUI Example, that allows the viewer:
 * <ul>
 *     <li>to click one Button to increase the count,</li>
 *     <li>another button to decrease the count,</li>
 *     <li>and a button to reset the count to 0.</li>
 * </ul>
 *
 * The reset Button is only displayed, when the count is not 0.<br>
 * Whenever the GUI is open the count is increased periodically every second, without requiring any input.
 * <br>
 * The count is displayed in the title of the Inventory and in the item name of the button in the middle.
 * Those parts are automatically updated when the count changes.
 */
public class CounterExample {

    private static final Map<GuiViewManager, CounterStore> counterStores = new WeakHashMap<>();

    /**
     * Stores the count value so that it persists when the GUI is closed.
     */
    private static class CounterStore {

        private int count = 0;

        public void setCount(int count) {
            this.count = count;
        }

        public int getCount() {
            return count;
        }
    }

    public static void register(GuiAPIManager manager) {
        manager.registerGuiFromFiles("example_counter", builder -> builder.window(mainMenu -> mainMenu.size(9 * 3).construct((renderer) -> {
            // This is only called upon creation of the component. So this is not called when the signal is updated!

            // Use signals that provide a simple value storage & synchronisation. Signals are not persistent and will get destroyed when the GUI is closed!
            Signal<Integer> countSignal = renderer.signal("count_signal", Integer.class, () -> 0);

            // Optionally, sync your data with the gui using custom data stores. This makes it possible to store persistent data.
            CounterStore counterStore = counterStores.computeIfAbsent(renderer.viewManager(), guiViewManager -> new CounterStore());
            Store<Integer> count = renderer.syncStore("count", Integer.class, counterStore::getCount, counterStore::setCount);

            renderer.addIntervalTask(() -> {
                count.update(integer -> ++integer); // Updates the count periodically (every second increases it by 1)
            }, 20);

            renderer
                    .titleSignals(count)
                    .render("count_down", ButtonBuilder.class, bb -> countDownButton(bb, count))
                    // Sometimes we want to render components dependent on signals
                    .renderWhen(() -> count.get() != 0, "reset", ButtonBuilder.class, bb -> resetButton(bb, count))
                    // The state of a component is only reconstructed if the slot it is positioned at changes.
                    // Here the slot will always have the same type of component, so the state is created only once.
                    .render("count_up", ButtonBuilder.class, bb -> countUpButton(bb, renderer, count))
                    .render("counter", ButtonBuilder.class, bb -> bb.icon(ib -> ib.updateOnSignals(count)));
        })));
    }

    /**
     * Since all the components are declared statically we can easily move them into their own functions
     **/

    static void countDownButton(ButtonBuilder bb, Signal<Integer> count) {
        bb.interact((guiHolder, interactionDetails) -> {
            count.update(old -> --old);
            return InteractionResult.cancel(true);
        });
    }

    static void countUpButton(ButtonBuilder bb, WindowDynamicConstructor renderer, Signal<Integer> count) {
        bb.interact((guiHolder, interactionDetails) -> {
            count.update(old -> ++old);
            return InteractionResult.cancel(true);
        }).animation(renderer, animationBuilder -> animationBuilder
                // Here we specify the frames to render after each other
                // So it first renders the cyan_concrete for a tick, then lime_concrete for a tick
                .frame(frame -> frame.duration(1).stack("cyan_concrete", conf -> {}))
                .frame(frame -> frame.duration(1).stack("lime_concrete", conf -> {}))
        );
    }

    static void resetButton(ButtonBuilder bb, Signal<Integer> count) {
        bb.interact((guiHolder, interactionDetails) -> {
            count.set(0); // The set method changes the value of the signal and prompts the listener of the signal to re-render.
            return InteractionResult.cancel(true);
        }).sound(holder -> Optional.of(Sound.sound(Key.key("minecraft:entity.dragon_fireball.explode"), Sound.Source.MASTER, 0.25f, 1)));
    }

}
