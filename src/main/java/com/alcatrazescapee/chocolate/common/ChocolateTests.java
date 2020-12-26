/*
 * Part of the Chocolate mod by AlcatrazEscapee.
 * Licensed under the MIT License. See LICENSE.md for details.
 */

package com.alcatrazescapee.chocolate.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeMaker;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static com.alcatrazescapee.chocolate.Chocolate.MOD_ID;

/**
 * This is a test which adds a couple dummy biomes, in order to verify fixes for biome ID resolution, and biome registration order fixing.
 */
public final class ChocolateTests
{
    private static final Logger LOGGER = LogManager.getLogger();

    public static void init()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Biome.class, ChocolateTests::registerBiomes);
    }

    private static void registerBiomes(RegistryEvent.Register<Biome> event)
    {
        LOGGER.info("Registering Biomes!");
        event.getRegistry().registerAll(
            biome("first"),
            biome("second")
        );
    }

    private static Biome biome(String name)
    {
        LOGGER.info("Constructing Biome: " + name);
        return BiomeMaker.theVoidBiome().setRegistryName(MOD_ID, name);
    }
}
