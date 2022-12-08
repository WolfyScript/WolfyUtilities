/*
 *       ____ _  _ ____ ___ ____ _  _ ____ ____ ____ ____ ___ _ _  _ ____
 *       |    |  | [__   |  |  | |\/| |    |__/ |__| |___  |  | |\ | | __
 *       |___ |__| ___]  |  |__| |  | |___ |  \ |  | |     |  | | \| |__]
 *
 *       CustomCrafting Recipe creation and management tool for Minecraft
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.wolfyscript.utilities.common.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.eval.operator.BoolOperator;

@KeyedStaticId(key = "bool")
public class NBTTagConfigBoolean extends NBTTagConfig {

    private final BoolOperator value;

    @JsonCreator
    public NBTTagConfigBoolean(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("value") BoolOperator value, @JacksonInject("key") String key, @JacksonInject("nbt_tag_config.parent") NBTTagConfig parent) {
        super(wolfyUtils, key, parent);
        this.value = value;
    }

    private NBTTagConfigBoolean(NBTTagConfigBoolean other) {
        super(other.wolfyUtils, other.key, other.parent);
        this.value = other.value;
    }

    @Override
    public NBTTagConfigBoolean copy() {
        return new NBTTagConfigBoolean(this);
    }
}
