package com.alcatrazescapee.chocolate.common;

import net.minecraft.util.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeMaker;
import net.minecraft.world.biome.Biomes;

import com.alcatrazescapee.chocolate.common.biome.BiomeBridge;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ConstantConditions")
public class BiomeBridgeTest
{
    @Test
    public void testBiomeBridge()
    {
        // Assert that the biome mixin was successful
        final Object biome = BiomeMaker.theVoidBiome();
        assertTrue(biome instanceof BiomeBridge);
    }

    @Test
    public void testKey()
    {
        // Assert the key setting and getting works correctly
        final BiomeBridge biome = (BiomeBridge) (Object) BiomeMaker.theVoidBiome();
        final RegistryKey<Biome> key = Biomes.PLAINS;

        biome.bridge$setKey(key);

        assertSame(biome.bridge$getKey(), Biomes.PLAINS);
    }

    @Test
    public void testKeyFromRegistryName()
    {
        // Assert the key inferring from the forge registry name works
        final Biome biome = BiomeMaker.theVoidBiome();
        final BiomeBridge bridge = (BiomeBridge) (Object) biome;

        biome.setRegistryName("minecraft", "deep_ocean");

        assertSame(Biomes.DEEP_OCEAN, bridge.bridge$getKey());
    }

    @Test
    public void testNoKey()
    {
        // Assert that no key throws an error
        final BiomeBridge biome = (BiomeBridge) (Object) BiomeMaker.theVoidBiome();

        assertThrows(IllegalStateException.class, biome::bridge$getKey);
    }
}
