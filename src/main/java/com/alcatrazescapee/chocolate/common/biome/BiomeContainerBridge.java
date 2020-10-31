package com.alcatrazescapee.chocolate.common.biome;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeContainer;

/**
 * Bridge interface for {@link net.minecraft.world.biome.BiomeContainer}, implemented by mixin
 *
 * Implements two slightly modified accessors
 */
public interface BiomeContainerBridge
{
    static BiomeContainerBridge of(BiomeContainer container)
    {
        return (BiomeContainerBridge) container;
    }

    /**
     * Gets the internal biome registry with a narrower type than vanilla stores.
     * The type is checked at construction time and an invalid registry passed in will throw an error
     */
    Registry<Biome> bridge$getActualBiomeRegistry();

    /**
     * Gets the internal biome array stored on the biome container
     * Used for serialization purposes.
     */
    Biome[] bridge$getInternalBiomeArray();
}
