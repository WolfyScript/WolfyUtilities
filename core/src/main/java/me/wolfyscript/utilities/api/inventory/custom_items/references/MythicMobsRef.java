package me.wolfyscript.utilities.api.inventory.custom_items.references;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.lumine.xikage.mythicmobs.MythicMobs;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.nms.nbt.NBTItem;
import me.wolfyscript.utilities.api.nms.nbt.NBTTagString;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Objects;

/**
 * Links to MythicMobs items and saves the specified item type.
 * <p>
 * For items to be detected by plugins in-game you need to add an additional Option to your MythicMobs item!
 * <pre>
 * Options:
 *     AppendType: true
 * </pre>
 */
public class MythicMobsRef extends APIReference {

    private static final String ITEM_KEY = "MYTHIC_TYPE";

    private final String itemName;

    public MythicMobsRef(String itemName) {
        super();
        this.itemName = itemName;
    }

    public MythicMobsRef(MythicMobsRef mythicMobsRef) {
        super(mythicMobsRef);
        this.itemName = mythicMobsRef.itemName;
    }

    @Override
    public ItemStack getLinkedItem() {
        return MythicMobs.inst().getItemManager().getItemStack(itemName);
    }

    @Override
    public ItemStack getIdItem() {
        return getLinkedItem();
    }

    @Override
    public boolean isValidItem(ItemStack itemStack) {
        var nbtItem = WolfyUtilities.getWUCore().getNmsUtil().getNBTUtil().getItem(itemStack);
        if (nbtItem != null && nbtItem.hasKey(ITEM_KEY) && nbtItem.getTag(ITEM_KEY) instanceof NBTTagString nbtTagString) {
            return Objects.equals(this.itemName, nbtTagString.asString());
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MythicMobsRef that = (MythicMobsRef) o;
        return Objects.equals(itemName, that.itemName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), itemName);
    }

    @Override
    public MythicMobsRef clone() {
        return new MythicMobsRef(this);
    }

    @Override
    public void serialize(JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStringField("mythicmobs", itemName);
    }

    public static class Parser extends APIReference.PluginParser<MythicMobsRef> {

        public Parser() {
            super("MythicMobs", "mythicmobs");
        }

        @Override
        public @Nullable MythicMobsRef construct(ItemStack itemStack) {
            NBTItem nbtItem = WolfyUtilities.getWUCore().getNmsUtil().getNBTUtil().getItem(itemStack);
            if (nbtItem != null && nbtItem.hasKey(ITEM_KEY) && nbtItem.getTag(ITEM_KEY) instanceof NBTTagString nbtTagString) {
                String name = nbtTagString.asString();
                if (MythicMobs.inst().getItemManager().getItem(name).isPresent()) {
                    return new MythicMobsRef(name);
                }
            }
            return null;
        }

        @Override
        public @Nullable MythicMobsRef parse(JsonNode element) {
            return new MythicMobsRef(element.asText());
        }
    }
}
