package me.wolfyscript.utilities.api.inventory.custom_items.meta;


import me.wolfyscript.utilities.util.inventory.item_builder.ItemBuilder;
import org.bukkit.inventory.meta.ItemMeta;

public class CustomModelDataMeta extends Meta {

    public CustomModelDataMeta() {
        setOption(MetaSettings.Option.EXACT);
        setAvailableOptions(MetaSettings.Option.EXACT, MetaSettings.Option.IGNORE, MetaSettings.Option.HIGHER, MetaSettings.Option.LOWER);
    }

    @Override
    public boolean check(ItemBuilder itemOther, ItemBuilder item) {
        ItemMeta meta1 = itemOther.getItemMeta();
        ItemMeta meta2 = item.getItemMeta();
        switch (option) {
            case IGNORE:
                meta1.setCustomModelData(0);
                meta2.setCustomModelData(0);
                itemOther.setItemMeta(meta1);
                item.setItemMeta(meta2);
                return true;
            case LOWER:
                return meta1.getCustomModelData() < meta2.getCustomModelData();
            case HIGHER:
                return meta1.getCustomModelData() > meta2.getCustomModelData();
            default:
                return true;
        }
    }
}
