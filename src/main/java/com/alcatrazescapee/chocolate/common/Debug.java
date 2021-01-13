/*
 * Part of the Chocolate mod by AlcatrazEscapee.
 * Licensed under the MIT License. See LICENSE.md for details.
 */

package com.alcatrazescapee.chocolate.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeMaker;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

import static com.alcatrazescapee.chocolate.Chocolate.MOD_ID;

/**
 * This is a test which adds a couple dummy biomes, in order to verify fixes for biome ID resolution, and biome registration order fixing.
 */
public final class Debug
{
    public static final boolean ENABLE = !FMLEnvironment.production;
    public static final boolean ENABLE_BIOME_FIXES = true;

    private static final Logger LOGGER = LogManager.getLogger();

    public static void init()
    {
        if (ENABLE)
        {
            LOGGER.error("Enabling Chocolate's Debug Features! You should not see this!");
            FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Biome.class, Debug::registerBiomes);
        }
    }

    private static void registerBiomes(RegistryEvent.Register<Biome> event)
    {
        for (final String name : new String[] {"second", "third", "fourth"})
        {
            final ResourceLocation id = new ResourceLocation(MOD_ID, name);
            final RegistryKey<Biome> key = RegistryKey.create(Registry.BIOME_REGISTRY, id);
            final Biome biome = BiomeMaker.theVoidBiome().setRegistryName(id);

            event.getRegistry().register(biome);
            for (final BiomeManager.BiomeType type : BiomeManager.BiomeType.values())
            {
                BiomeManager.addBiome(type, new BiomeManager.BiomeEntry(key, 100));
            }
        }
    }
}
