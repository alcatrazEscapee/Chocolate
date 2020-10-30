package com.alcatrazescapee.chocolate.common.biome;

import java.util.Arrays;
import java.util.concurrent.Callable;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.SingleBiomeProvider;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import com.alcatrazescapee.chocolate.Chocolate;
import com.alcatrazescapee.chocolate.common.ChocolateConfig;
import com.alcatrazescapee.chocolate.common.biome.BiomeContainerBridge;
import com.alcatrazescapee.chocolate.common.biome.BiomeContainerSerializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ConstantConditions")
public class BiomeContainerSerializerTest
{
    @Test
    public void testReadNoPalette()
    {
        // Assert reading with no palette produces a valid container
        final Registry<Biome> registry = ServerLifecycleHooks.getCurrentServer().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
        final ChunkPos pos = new ChunkPos(0, 0);
        final Biome biome = registry.getOrThrow(Biomes.BEACH);
        final int biomeId = registry.getId(biome);
        final int[] data = new int[BiomeContainer.BIOMES_SIZE];
        Arrays.fill(data, biomeId);

        final CompoundNBT nbt = new CompoundNBT();
        nbt.putIntArray(BiomeContainerSerializer.BIOMES_KEY, data);

        final BiomeContainer container = BiomeContainerSerializer.read(registry, pos, null, data, nbt); // Intentionally passing null into the de-serializer, as it SHOULD NOT be using that to read biomes
        final BiomeContainerBridge bridge = (BiomeContainerBridge) container;

        final Biome[] expectedBiomes = new Biome[BiomeContainer.BIOMES_SIZE];
        Arrays.fill(expectedBiomes, biome);

        assertArrayEquals(expectedBiomes, bridge.bridge$getInternalBiomeArray());
    }

    @Test
    public void testReadPalette()
    {
        // Assert reading with a palette produces a valid container
        final Registry<Biome> registry = ServerLifecycleHooks.getCurrentServer().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
        final ChunkPos pos = new ChunkPos(0, 0);
        final Biome biome = registry.getOrThrow(Biomes.BASALT_DELTAS);
        final int biomeId = registry.getId(biome);
        final int[] data = new int[BiomeContainer.BIOMES_SIZE];
        Arrays.fill(data, biomeId);

        final CompoundNBT nbt = new CompoundNBT();
        nbt.putIntArray(BiomeContainerSerializer.BIOMES_KEY, data);

        final CompoundNBT paletteNbt = new CompoundNBT();
        final int[] ids = {biomeId};
        final ListNBT keys = new ListNBT();
        keys.add(StringNBT.valueOf(Biomes.BASALT_DELTAS.location().toString()));
        paletteNbt.putIntArray(BiomeContainerSerializer.IDS_KEY, ids);
        paletteNbt.put(BiomeContainerSerializer.KEYS_KEY, keys);
        nbt.put(BiomeContainerSerializer.PALETTE_KEY, paletteNbt);

        final BiomeContainer container = BiomeContainerSerializer.read(registry, pos, null, data, nbt); // Intentionally passing null into the de-serializer, as it SHOULD NOT be using that to read biomes
        final BiomeContainerBridge bridge = (BiomeContainerBridge) container;

        final Biome[] expectedBiomes = new Biome[BiomeContainer.BIOMES_SIZE];
        Arrays.fill(expectedBiomes, biome);

        assertArrayEquals(expectedBiomes, bridge.bridge$getInternalBiomeArray());
    }

    @Test
    public void testReadPaletteMismatchedIds()
    {
        // Assert reading with a palette but invalid biome IDs produces a valid container
        final Registry<Biome> registry = ServerLifecycleHooks.getCurrentServer().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
        final ChunkPos pos = new ChunkPos(0, 0);
        final Biome biome1 = registry.getOrThrow(Biomes.BIRCH_FOREST), biome2 = registry.getOrThrow(Biomes.BIRCH_FOREST_HILLS);
        final int[] data = new int[BiomeContainer.BIOMES_SIZE];
        for (int i = 0; i < data.length; i++)
        {
            data[i] = i % 4 == 0 ? 11 : 22;
        }

        final CompoundNBT nbt = new CompoundNBT();
        nbt.putIntArray(BiomeContainerSerializer.BIOMES_KEY, data);

        final CompoundNBT paletteNbt = new CompoundNBT();
        final int[] ids = {11, 22};
        final ListNBT keys = new ListNBT();
        keys.add(StringNBT.valueOf(Biomes.BIRCH_FOREST.location().toString()));
        keys.add(StringNBT.valueOf(Biomes.BIRCH_FOREST_HILLS.location().toString()));
        paletteNbt.putIntArray(BiomeContainerSerializer.IDS_KEY, ids);
        paletteNbt.put(BiomeContainerSerializer.KEYS_KEY, keys);
        nbt.put(BiomeContainerSerializer.PALETTE_KEY, paletteNbt);

        // Sanity check that we aren't writing valid IDs
        assertNotEquals(ids[0], registry.getId(biome1));
        assertNotEquals(ids[1], registry.getId(biome2));

        final BiomeContainer container = BiomeContainerSerializer.read(registry, pos, null, data, nbt); // Intentionally passing null into the de-serializer, as it SHOULD NOT be using that to read biomes
        final BiomeContainerBridge bridge = (BiomeContainerBridge) container;

        final Biome[] expectedBiomes = new Biome[BiomeContainer.BIOMES_SIZE];
        for (int i = 0; i < expectedBiomes.length; i++)
        {
            expectedBiomes[i] = i % 4 == 0 ? biome1 : biome2;
        }

        assertArrayEquals(expectedBiomes, bridge.bridge$getInternalBiomeArray());
    }

    @Test
    public void testReadInvalidIds()
    {
        // Assert reading with a palette produces a valid container
        final Registry<Biome> registry = ServerLifecycleHooks.getCurrentServer().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
        final ChunkPos pos = new ChunkPos(0, 0);
        final Biome defaultBiome = registry.getOrThrow(Biomes.COLD_OCEAN);
        final SingleBiomeProvider provider = new SingleBiomeProvider(defaultBiome);
        int biomeId = 123; // Dummy ID for a biome that used to exist
        final int[] data = new int[BiomeContainer.BIOMES_SIZE];
        Arrays.fill(data, biomeId);

        final CompoundNBT nbt = new CompoundNBT();
        nbt.putIntArray(BiomeContainerSerializer.BIOMES_KEY, data);

        final CompoundNBT paletteNbt = new CompoundNBT();
        final int[] ids = {biomeId};
        final ListNBT keys = new ListNBT();
        keys.add(StringNBT.valueOf("other_mod:not_included"));
        paletteNbt.putIntArray(BiomeContainerSerializer.IDS_KEY, ids);
        paletteNbt.put(BiomeContainerSerializer.KEYS_KEY, keys);
        nbt.put(BiomeContainerSerializer.PALETTE_KEY, paletteNbt);

        final BiomeContainer container = BiomeContainerSerializer.read(registry, pos, provider, data, nbt); // The provider will correct the default biomes
        final BiomeContainerBridge bridge = (BiomeContainerBridge) container;

        final Biome[] expectedBiomes = new Biome[BiomeContainer.BIOMES_SIZE];
        Arrays.fill(expectedBiomes, defaultBiome);

        assertArrayEquals(expectedBiomes, bridge.bridge$getInternalBiomeArray());

        // Additionally, assert that this will throw under the correct config option
        ChocolateConfig.SERVER.onBiomesRemovedFromChunks.set(ChocolateConfig.Severity.THROW);
        assertThrowsWithCleanup(IllegalStateException.class, () -> BiomeContainerSerializer.read(registry, pos, provider, data, nbt), () -> {
            // Reset the config option always
            ChocolateConfig.SERVER.onBiomesRemovedFromChunks.set(ChocolateConfig.Severity.LOG);
        });
    }

    @Test
    public void testReadIdsMissingFromPalette()
    {
        // Assert reading with a palette produces a valid container
        final Registry<Biome> registry = ServerLifecycleHooks.getCurrentServer().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
        final ChunkPos pos = new ChunkPos(0, 0);
        final Biome biome = registry.getOrThrow(Biomes.DARK_FOREST);
        final SingleBiomeProvider provider = new SingleBiomeProvider(biome);
        int biomeId = registry.getId(biome);
        final int[] data = new int[BiomeContainer.BIOMES_SIZE];
        Arrays.fill(data, biomeId);

        final CompoundNBT nbt = new CompoundNBT();
        nbt.putIntArray(BiomeContainerSerializer.BIOMES_KEY, data);

        final CompoundNBT paletteNbt = new CompoundNBT();
        final int[] ids = {};
        final ListNBT keys = new ListNBT();
        paletteNbt.putIntArray(BiomeContainerSerializer.IDS_KEY, ids);
        paletteNbt.put(BiomeContainerSerializer.KEYS_KEY, keys);
        nbt.put(BiomeContainerSerializer.PALETTE_KEY, paletteNbt);

        final BiomeContainer container = BiomeContainerSerializer.read(registry, pos, provider, data, nbt); // The provider will correct the default biomes
        final BiomeContainerBridge bridge = (BiomeContainerBridge) container;

        final Biome[] expectedBiomes = new Biome[BiomeContainer.BIOMES_SIZE];
        Arrays.fill(expectedBiomes, biome);

        assertArrayEquals(expectedBiomes, bridge.bridge$getInternalBiomeArray());

        // Additionally, assert that this will throw under the correct config option
        ChocolateConfig.SERVER.onIdsMissingFromPalette.set(ChocolateConfig.Severity.THROW);
        assertThrowsWithCleanup(IllegalStateException.class, () -> BiomeContainerSerializer.read(registry, pos, provider, data, nbt), () -> {
            // Reset the log level if this fails
            ChocolateConfig.SERVER.onIdsMissingFromPalette.set(ChocolateConfig.Severity.LOG);
        });
    }

    @Test
    public void testRoundTripSerialization()
    {
        final Registry<Biome> registry = ServerLifecycleHooks.getCurrentServer().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
        final ChunkPos pos = new ChunkPos(0, 0);
        final BiomeProvider overworldProvider = ServerLifecycleHooks.getCurrentServer().overworld().getChunkSource().getGenerator().getBiomeSource();

        final BiomeContainer container = new BiomeContainer(registry, pos, overworldProvider);
        final BiomeContainerBridge bridge = (BiomeContainerBridge) container;
        final CompoundNBT nbt = new CompoundNBT();

        BiomeContainerSerializer.write(container, nbt);
        nbt.putIntArray(BiomeContainerSerializer.BIOMES_KEY, container.writeBiomes());

        final int[] biomeData = nbt.getIntArray(BiomeContainerSerializer.BIOMES_KEY);
        final BiomeContainer newContainer = BiomeContainerSerializer.read(registry, pos, null, biomeData, nbt); // Intentionally passing null into the de-serializer, as it SHOULD NOT be using that to read biomes
        final BiomeContainerBridge newBridge = (BiomeContainerBridge) newContainer;

        // Decoded form is equal
        assertArrayEquals(bridge.bridge$getInternalBiomeArray(), newBridge.bridge$getInternalBiomeArray());

        final CompoundNBT newNbt = new CompoundNBT();
        BiomeContainerSerializer.write(newContainer, newNbt);
        newNbt.putIntArray(BiomeContainerSerializer.BIOMES_KEY, newContainer.writeBiomes());

        // Serialized form is equal
        assertEquals(nbt, newNbt);
    }

    private <E extends Throwable> void assertThrowsWithCleanup(Class<E> exception, Executable thrower, Runnable cleanup)
    {
        try
        {
            assertThrows(exception, thrower);
        }
        finally
        {
            cleanup.run();
        }
    }
}
