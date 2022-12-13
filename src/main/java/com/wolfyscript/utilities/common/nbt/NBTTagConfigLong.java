package com.wolfyscript.utilities.common.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.eval.value_provider.ValueProvider;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderLong;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderLongConst;
import com.wolfyscript.utilities.json.ValueSerializer;
import com.wolfyscript.utilities.json.annotations.OptionalValueSerializer;
import java.io.IOException;

@OptionalValueSerializer(serializer = NBTTagConfigLong.OptionalValueSerializer.class)
@KeyedStaticId(key = "long")
public class NBTTagConfigLong extends NBTTagConfigPrimitive<Long> {

    @JsonCreator
    NBTTagConfigLong(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("value") ValueProviderLong value) {
        super(wolfyUtils, value);
    }

    public NBTTagConfigLong(WolfyUtils wolfyUtils, NBTTagConfig parent, ValueProvider<Long> value) {
        super(wolfyUtils, parent, value);
    }

    public NBTTagConfigLong(NBTTagConfigPrimitive<Long> other) {
        super(other);
    }

    @Override
    public NBTTagConfigLong copy() {
        return new NBTTagConfigLong(this);
    }

    public static class OptionalValueSerializer extends ValueSerializer<NBTTagConfigLong> {

        public OptionalValueSerializer() {
            super(NBTTagConfigLong.class);
        }

        @Override
        public boolean serialize(NBTTagConfigLong targetObject, JsonGenerator generator, SerializerProvider provider) throws IOException {
            if (targetObject.value instanceof ValueProviderLongConst longConst) {
                generator.writeObject(longConst);
                return true;
            }
            return false;
        }
    }
}