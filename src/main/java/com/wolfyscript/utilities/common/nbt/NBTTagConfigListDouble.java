package com.wolfyscript.utilities.common.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.common.WolfyUtils;
import java.util.List;

@KeyedStaticId(key = "list/double")
public class NBTTagConfigListDouble extends NBTTagConfigListPrimitive<Double, NBTTagConfigDouble> {

    public NBTTagConfigListDouble(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("elements") List<Element<NBTTagConfigDouble>> elements, @JacksonInject("key") String key, @JacksonInject("nbt_tag_config.parent") NBTTagConfig parent) {
        super(wolfyUtils, elements, key, parent, NBTTagConfigDouble.class);
    }

    public NBTTagConfigListDouble(NBTTagConfigList<NBTTagConfigDouble> other) {
        super(other);
    }

    @Override
    public NBTTagConfigListDouble copy() {
        return new NBTTagConfigListDouble(this);
    }
}
