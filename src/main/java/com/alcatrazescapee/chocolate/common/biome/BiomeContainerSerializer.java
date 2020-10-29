package com.alcatrazescapee.chocolate.common.biome;

import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraftforge.common.util.Constants;

import com.alcatrazescapee.chocolate.common.ChocolateConfig;

/**
 * Advanced {@link BiomeContainer} serialization, which stores an additional palette to chunk data.
 */
public final class BiomeContainerSerializer
{
    private static final String PALETTE_KEY = "ChocolateBiomePalette";
    private static final String KEYS_KEY = "Keys";
    private static final String IDS_KEY = "Ids";

    private static final String BIOMES_KEY = "Biomes";

    private static final Logger LOGGER = LogManager.getLogger();

    public static BiomeContainer readBiomeContainer(Registry<Biome> biomeRegistry, ChunkPos pos, BiomeProvider biomeProvider, @Nullable int[] biomeData, CompoundNBT nbt)
    {
        if (biomeData != null && nbt.contains(PALETTE_KEY, Constants.NBT.TAG_COMPOUND))
        {
            // Read and apply additional palette data
            final CompoundNBT paletteNbt = nbt.getCompound(PALETTE_KEY);
            final ListNBT keysNbt = paletteNbt.getList(KEYS_KEY, Constants.NBT.TAG_STRING);
            final int[] savedIds = paletteNbt.getIntArray(IDS_KEY);
            final int[] actualIds = new int[savedIds.length];
            final List<ResourceLocation> missingBiomeNames = new ArrayList<>();
            for (int i = 0; i < keysNbt.size(); i++)
            {
                final ResourceLocation key = new ResourceLocation(keysNbt.getString(i));
                final Biome biome = biomeRegistry.get(key);
                if (biome != null)
                {
                    actualIds[i] = biomeRegistry.getId(biome);
                }
                else
                {
                    actualIds[i] = 0; // Assume 0 is always a valid key - and it should be and default to minecraft:ocean
                    missingBiomeNames.add(key);
                }
            }

            if (!missingBiomeNames.isEmpty() && ChocolateConfig.SERVER.logBiomesRemovedFromChunks.get())
            {
                // There will be missing biomes from the world! This is likely due to removing biomes
                LOGGER.error("There are biomes in the chunk which are missing from the world! These will be defaulted: " + missingBiomeNames.stream().map(ResourceLocation::toString).collect(Collectors.joining(", ")));
            }

            final Set<Integer> missingIds = new HashSet<>();
            for (int i = 0; i < biomeData.length; i++)
            {
                int biomeId = biomeData[i];
                boolean foundId = false;
                for (int j = 0; j < savedIds.length; j++)
                {
                    if (biomeId == savedIds[j])
                    {
                        biomeData[i] = actualIds[j];
                        foundId = true;
                        break;
                    }
                }

                if (!foundId)
                {
                    // Batch errors together to avoid spamming the log as this is done for every chunk
                    missingIds.add(biomeId);
                }
            }

            if (!missingIds.isEmpty() && ChocolateConfig.SERVER.logIdsMissingFromPalette.get())
            {
                // This should never happen, it means somehow the serialization contract was broken
                LOGGER.error("There are int IDs present in the biome container with no entry in the palette!");
                LOGGER.error("The biome palette was: " + paletteNbt);
                LOGGER.error("The biome array was: [" + Arrays.stream(biomeData).mapToObj(String::valueOf).collect(Collectors.joining()) + ']');
                LOGGER.error("The missing int IDs were: " + missingIds);
            }
        }
        return new BiomeContainer(biomeRegistry, pos, biomeProvider, biomeData);
    }

    /**
     * Writes a biome container's palette information to the specified nbt tag
     * At this point, the biome container's {@link BiomeContainer#writeBiomes()} method has already been invoked (which is going to invoke the mixin modified version.
     * Under that assumption, every biome that is written as an int ID will have serialized itself as biome -(bridge)-> key -(registry)-> biome -(registry)-> int ID
     * We are then free to assume this process will hold for all biomes found in the biome container at this point
     *
     * @param biomeContainer The biome container
     * @param nbt The nbt to put extra palette data onto
     */
    @SuppressWarnings("ConstantConditions")
    public static void writeBiomeContainer(@Nullable BiomeContainer biomeContainer, CompoundNBT nbt)
    {
        if (biomeContainer != null && nbt.contains(BIOMES_KEY))
        {
            // Write additional palette data
            final CompoundNBT paletteNbt = new CompoundNBT();
            final Registry<Biome> biomeRegistry = ((BiomeContainerBridge) biomeContainer).bridge$getActualBiomeRegistry();
            final Biome[] uniqueBiomes = Arrays.stream(((BiomeContainerBridge) biomeContainer).bridge$getInternalBiomeArray())
                .distinct()
                .toArray(Biome[]::new);
            final int[] ids = new int[uniqueBiomes.length];
            final ListNBT keysNbt = new ListNBT();

            // This is free of any checks as they have already been performed in BiomeContainer#writeBiomes
            for (int i = 0; i < uniqueBiomes.length; i++)
            {
                // Use the key on each biome and compute an ID mapping
                final Biome biome = uniqueBiomes[i];
                final RegistryKey<Biome> key = ((BiomeBridge) (Object) biome).bridge$getKey();
                final int id = biomeRegistry.getId(biomeRegistry.get(key));
                if (key != null)
                {
                    keysNbt.add(StringNBT.valueOf(key.location().toString()));
                    ids[i] = id;
                }
            }

            paletteNbt.putIntArray(IDS_KEY, ids);
            paletteNbt.put(KEYS_KEY, keysNbt);
            nbt.put(PALETTE_KEY, paletteNbt);
        }
    }
}
