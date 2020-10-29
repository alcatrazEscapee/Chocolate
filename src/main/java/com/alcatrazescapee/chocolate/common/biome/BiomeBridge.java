package com.alcatrazescapee.chocolate.common.biome;

import net.minecraft.util.RegistryKey;
import net.minecraft.world.biome.Biome;

/**
 * Bridge interface that is applied to {@link Biome} via mixin.
 *
 * Stores a non null, unique {@link RegistryKey} on every biome.
 */
public interface BiomeBridge
{
    RegistryKey<Biome> bridge$getKey();

    void bridge$setKey(RegistryKey<Biome> key);
}
