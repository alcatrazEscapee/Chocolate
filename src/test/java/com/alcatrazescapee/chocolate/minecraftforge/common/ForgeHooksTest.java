package com.alcatrazescapee.chocolate.minecraftforge.common;

import com.google.gson.JsonElement;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.*;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import com.alcatrazescapee.chocolate.common.biome.BiomeBridge;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ForgeHooksTest
{
    @Test
    public void testEnhanceBiomeSetsRegistryName()
    {
        final Registry<Biome> registry = ServerLifecycleHooks.getCurrentServer().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
        final Biome biome = registry.getOrThrow(Biomes.DARK_FOREST);
        final BiomeBridge bridge = BiomeBridge.of(biome);
        final DynamicOps<JsonElement> ops = JsonOps.INSTANCE;

        assertSame(Biomes.DARK_FOREST, bridge.bridge$getKey());

        // Encode + decode to trigger forge hooks call
        final JsonElement json = Biome.DIRECT_CODEC.encode(biome, ops, ops.empty()).result().orElseGet(() -> fail("Unable to encode biome"));
        final Biome newBiome = Biome.DIRECT_CODEC.decode(ops, json).result().orElseGet(() -> fail("Unable to decode biome")).getFirst();
        final BiomeBridge newBridge = BiomeBridge.of(newBiome);

        assertEquals(Biomes.DARK_FOREST.location(), newBiome.getRegistryName());
        assertSame(Biomes.DARK_FOREST, newBridge.bridge$getKey());
    }
}
