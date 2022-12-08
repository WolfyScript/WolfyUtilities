package com.wolfyscript.utilities.common.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.common.WolfyUtils;
import java.util.List;

@KeyedStaticId(key = "list/int_array")
public class NBTTagConfigListIntArray extends NBTTagConfigListPrimitive<int[], NBTTagConfigIntArray> {

    public NBTTagConfigListIntArray(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("elements") List<Element<NBTTagConfigIntArray>> elements, @JacksonInject("key") String key, @JacksonInject("nbt_tag_config.parent") NBTTagConfig parent) {
        super(wolfyUtils, elements, key, parent, NBTTagConfigIntArray.class);
    }

    public NBTTagConfigListIntArray(NBTTagConfigList<NBTTagConfigIntArray> other) {
        super(other);
    }

    @Override
    public NBTTagConfigListIntArray copy() {
        return new NBTTagConfigListIntArray(this);
    }
}
