package me.wolfyscript.utilities.api.inventory.custom_items.references;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import dev.lone.itemsadder.api.CustomStack;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Objects;

/**
 * Links to an ItemsAdder item and saves the item key accordingly.
 * <br>
 * It will always get the latest item version available in ItemsAdder or AIR if not available.
 */
public class ItemsAdderRef extends APIReference {

    private final String itemID;

    public ItemsAdderRef(String itemID) {
        super();
        this.itemID = itemID;
    }

    public ItemsAdderRef(ItemsAdderRef itemsAdderRef) {
        super(itemsAdderRef);
        this.itemID = itemsAdderRef.itemID;
    }

    /**
     * @return The latest ItemStack available in ItemsAdder under the specified itemID or AIR.
     */
    @Override
    public ItemStack getLinkedItem() {
        var customStack = CustomStack.getInstance(itemID);
        if (customStack != null) {
            return customStack.getItemStack();
        }
        return ItemUtils.AIR;
    }

    @Override
    public ItemStack getIdItem() {
        return getLinkedItem();
    }

    @Override
    public boolean isValidItem(ItemStack itemStack) {
        var customStack = CustomStack.byItemStack(itemStack);
        return customStack != null && Objects.equals(itemID, customStack.getNamespacedID());
    }

    @Override
    public void serialize(JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStringField("itemsadder", itemID);
    }

    public String getItemID() {
        return itemID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemsAdderRef itemsAdderRef)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(itemID, itemsAdderRef.itemID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), itemID);
    }

    @Override
    public ItemsAdderRef clone() {
        return new ItemsAdderRef(this);
    }

    public static class Parser extends PluginParser<ItemsAdderRef> {

        public Parser() {
            super("ItemsAdder", "itemsadder");
        }

        @Override
        public @Nullable ItemsAdderRef construct(ItemStack itemStack) {
            var customStack = CustomStack.byItemStack(itemStack);
            if (customStack != null) {
                return new ItemsAdderRef(customStack.getNamespacedID());
            }
            return null;
        }

        @Override
        public @Nullable ItemsAdderRef parse(JsonNode element) {
            return new ItemsAdderRef(element.asText());
        }
    }
}
