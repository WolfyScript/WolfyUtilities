package com.wolfyscript.utilities.gui.example

import com.wolfyscript.utilities.gui.GuiAPIManager
import com.wolfyscript.utilities.gui.InteractionResult
import com.wolfyscript.utilities.gui.reactivity.createSignal
import com.wolfyscript.utilities.gui.rendering.PropertyPosition
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import java.util.*

/**
 * A Counter GUI Example, that allows the viewer:
 *
 *  * to click one Button to increase the count,
 *  * another button to decrease the count,
 *  * and a button to reset the count to 0.
 *
 *
 *
 * The reset Button is only displayed, when the count is not 0.<br></br>
 * Whenever the GUI is open the count is increased periodically every second, without requiring any input.
 * <br></br>
 * The count is displayed in the title of the Inventory and in the item name of the button in the middle.
 * Those parts are automatically updated when the count changes.
 */
fun registerExampleCounter(manager: GuiAPIManager) {
    manager.registerGuiFromFiles("example_counter") {
        /**
         * Everything in this section is called **async** and only once per runtime initiation.
         * It constructs the component tree and reactive graph as specified.
         **/
        window {
            // Use signals that provide a simple value storage & synchronisation. Signals do not store the value themselves.
            val count = createSignal(0)
            count.tagName("count")

            size(9 * 3)
            titleSignals(count)

            // Update the count periodically (every second increases it by 1)
            addIntervalTask(
                { count.update { value -> value + 1 } },
                20
            )

            button("count_down") {
                interact { _, _ ->
                    count.update { old -> old - 1 }
                    InteractionResult.cancel(true)
                }
            }
            // Sometimes we want to render components dependent on signals
            whenever { count.get() != 0 } then {
                // This section is run just once up on the initial construction too, not when the condition changes
                button("reset") {
                    interact { _, _ ->
                        count.set(0) // The set method changes the value of the signal and prompts the listener of the signal to re-render.
                        InteractionResult.cancel(true)
                    }
                    sound {
                        Optional.of(
                            Sound.sound(
                                Key.key("minecraft:entity.dragon_fireball.explode"),
                                Sound.Source.MASTER,
                                0.25f,
                                1f
                            )
                        )
                    }
                }
            }
            button("count_up") {
                interact { _, _ ->
                    count.update { old -> old + 1 }
                    InteractionResult.cancel(true)
                }
                animation {
                    // Here we specify the frames to render after each other
                    // So it first renders the cyan_concrete for a tick, then lime_concrete for a tick
                    frame {
                        duration(1)
                        stack("cyan_concrete") { }
                    }

                    frame {
                        duration(1)
                        stack("lime_concrete") { }
                    }
                }
            }

            button("counter") {
                icon { updateOnSignals(count) }
            }
        }
    }
}
