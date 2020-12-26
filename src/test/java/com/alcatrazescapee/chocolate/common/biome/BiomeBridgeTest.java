/*
 * Part of the Chocolate mod by AlcatrazEscapee.
 * Licensed under the MIT License. See LICENSE.md for details.
 */

package com.alcatrazescapee.chocolate.common.biome;

import net.minecraft.util.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeMaker;
import net.minecraft.world.biome.Biomes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BiomeBridgeTest
{
    @Test
    public void testBiomeBridgeOf()
    {
        // Assert that the biome mixin was successful
        assertDoesNotThrow(() -> BiomeBridge.of(BiomeMaker.theVoidBiome()));
    }

    @Test
    public void testKey()
    {
        // Assert the key setting and getting works correctly
        final BiomeBridge biome = BiomeBridge.of(BiomeMaker.theVoidBiome());
        final RegistryKey<Biome> key = Biomes.PLAINS;

        biome.bridge$setKey(key);

        assertSame(biome.bridge$getKey(), Biomes.PLAINS);
    }

    @Test
    public void testKeyFromRegistryName()
    {
        // Assert the key inferring from the forge registry name works
        final Biome biome = BiomeMaker.theVoidBiome();
        final BiomeBridge bridge = BiomeBridge.of(biome);

        biome.setRegistryName("minecraft", "deep_ocean");

        assertSame(Biomes.DEEP_OCEAN, bridge.bridge$getKey());
    }

    @Test
    public void testNoKey()
    {
        // Assert that no key throws an error
        final BiomeBridge biome = BiomeBridge.of(BiomeMaker.theVoidBiome());

        assertThrows(IllegalStateException.class, biome::bridge$getKey);
    }
}
