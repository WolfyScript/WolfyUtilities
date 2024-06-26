package com.wolfyscript.utilities.bukkit.adapters

import com.wolfyscript.utilities.NamespacedKey
import com.wolfyscript.utilities.WolfyUtils
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey
import com.wolfyscript.utilities.bukkit.data.ItemStackDataComponentMap
import com.wolfyscript.utilities.bukkit.world.items.BukkitItemStackConfig
import com.wolfyscript.utilities.world.items.ItemStackConfig
import org.bukkit.inventory.ItemStack

class ItemStackImpl(private val wolfyUtils: WolfyUtils, bukkitRef: ItemStack?) : BukkitRefAdapter<ItemStack?>(bukkitRef), com.wolfyscript.utilities.platform.adapters.ItemStack {
    private val componentMap = ItemStackDataComponentMap(this)

    override fun getItem(): NamespacedKey {
        if (getBukkitRef() == null) {
            return BukkitNamespacedKey.of("minecraft:air")!!
        }
        return BukkitNamespacedKey.fromBukkit(getBukkitRef()!!.type.key)
    }

    override fun getAmount(): Int {
        return getBukkitRef()?.amount ?: 0
    }

    override fun snapshot(): ItemStackConfig {
        return BukkitItemStackConfig(wolfyUtils, this)
    }

    override fun data(): ItemStackDataComponentMap {
        return componentMap
    }
}
