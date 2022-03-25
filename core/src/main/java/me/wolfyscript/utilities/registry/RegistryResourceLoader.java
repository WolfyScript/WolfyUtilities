/*
 *       WolfyUtilities, APIs and Utilities for Minecraft Spigot plugins
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

package me.wolfyscript.utilities.registry;

import me.wolfyscript.utilities.expansions.ResourceLoader;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.util.LinkedList;
import java.util.List;

public class RegistryResourceLoader extends RegistrySimple<ResourceLoader> {

    private final List<NamespacedKey> registerOrder = new LinkedList<>();

    public RegistryResourceLoader(Registries registries) {
        super(new NamespacedKey(registries.getCore(), "expansions/loaders"), registries);
    }

    @Override
    public void register(ResourceLoader value) {
        super.register(value);
        if (!registerOrder.contains(value.getNamespacedKey())) {
            registerOrder.add(value.getNamespacedKey());
        }
    }

    @Override
    public void register(NamespacedKey namespacedKey, ResourceLoader value) {
        super.register(namespacedKey, value);
    }

    public List<NamespacedKey> getRegisterOrder() {
        return List.copyOf(registerOrder);
    }
}
