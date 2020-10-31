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
    /**
     * At compile time this will only allow biomes (which will at runtime implement {@link BiomeBridge}) to be casted.
     * This avoids IDE constant conditions warnings, or supressing them and obscuring actual errors
     */
    @SuppressWarnings("ConstantConditions")
    static BiomeBridge of(Biome biome)
    {
        return (BiomeBridge) (Object) biome;
    }

    RegistryKey<Biome> bridge$getKey();

    void bridge$setKey(RegistryKey<Biome> key);
}
