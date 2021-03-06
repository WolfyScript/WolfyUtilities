package me.wolfyscript.utilities.api.inventory.custom_items.references;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Objects;

/**
 * Links to and saves a vanilla Bukkit ItemStack.
 */
public class VanillaRef extends APIReference {

    private final ItemStack itemStack;

    public VanillaRef(ItemStack itemStack) {
        super();
        this.itemStack = itemStack;
    }

    public VanillaRef(VanillaRef vanillaRef) {
        super(vanillaRef);
        this.itemStack = vanillaRef.itemStack.clone();
    }

    @Override
    public ItemStack getLinkedItem() {
        return itemStack;
    }

    @Override
    public ItemStack getIdItem() {
        return itemStack;
    }

    @Override
    public boolean isValidItem(ItemStack itemStack) {
        return true;
    }

    @Override
    public void serialize(JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeFieldName("item");
        JacksonUtil.getObjectMapper().writeValue(gen, itemStack);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VanillaRef that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(itemStack, that.itemStack);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), itemStack);
    }

    @Override
    public VanillaRef clone() {
        return new VanillaRef(this);
    }

    public static class Parser extends APIReference.Parser<APIReference> {

        public Parser() {
            super("item", 1000);
        }

        @Override
        public @Nullable VanillaRef construct(ItemStack itemStack) {
            return new VanillaRef(itemStack);
        }

        @Override
        public @Nullable APIReference parse(JsonNode element) {
            var itemStack = JacksonUtil.getObjectMapper().convertValue(element, ItemStack.class);
            if (itemStack != null) {
                APIReference.Parser<?> parser = CustomItem.getApiReferenceParser("wolfyutilities");
                if (parser != null) {
                    APIReference reference = parser.construct(itemStack);
                    if (reference != null) {
                        return reference;
                    }
                }
            }
            return new VanillaRef(itemStack);
        }
    }
}
