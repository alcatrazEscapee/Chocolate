package com.alcatrazescapee.chocolate.world.gen;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.FlatGenerationSettings;

import net.minecraftforge.fml.server.ServerLifecycleHooks;

import com.alcatrazescapee.chocolate.common.biome.BiomeBridge;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class FlatGenerationSettingsTest
{
    @Test
    public void testRegistryNameSetFromNewBiomeFromSettings()
    {
        final Registry<Biome> registry = ServerLifecycleHooks.getCurrentServer().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
        final FlatGenerationSettings settings = FlatGenerationSettings.getDefault(registry);
        final Biome biome = settings.getBiome();
        final BiomeBridge bridge = BiomeBridge.of(biome);

        assertSame(Biomes.PLAINS, bridge.bridge$getKey());

        final Biome newBiome = settings.getBiomeFromSettings();
        final BiomeBridge newBridge = BiomeBridge.of(newBiome);

        assertEquals(Biomes.PLAINS.location(), newBiome.getRegistryName());
        assertSame(Biomes.PLAINS, bridge.bridge$getKey());
    }
}
