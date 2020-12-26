/*
 * Part of the Chocolate mod by AlcatrazEscapee.
 * Licensed under the MIT License. See LICENSE.md for details.
 */

package com.alcatrazescapee.chocolate.common;

import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public final class ChocolateConfig
{
    public static final Server SERVER = register(ModConfig.Type.SERVER, Server::new);

    public static void init() {}

    private static <C> C register(ModConfig.Type type, Function<ForgeConfigSpec.Builder, C> factory)
    {
        Pair<C, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(factory);
        ModLoadingContext.get().registerConfig(type, specPair.getRight());
        return specPair.getLeft();
    }

    public enum Severity
    {
        NONE, LOG, THROW
    }

    public static class Server
    {
        public final ForgeConfigSpec.EnumValue<Severity> onBiomesRemovedFromChunks;
        public final ForgeConfigSpec.EnumValue<Severity> onIdsMissingFromPalette;

        Server(ForgeConfigSpec.Builder builder)
        {
            builder.push("logging");

            onBiomesRemovedFromChunks = builder.comment(
                "How should Chocolate handle biome IDs which are present in a chunk palette, but there was no biome of that name found in the registry? (This will happen when you remove biomes.)",
                "NONE = Default vanilla behavior (regenerate the biome)",
                "LOG = Default behavior plus logging an error message",
                "THROW = Forcibly throw an error (crash)"
            ).defineEnum("onBiomesRemovedFromChunks", Severity.LOG, Severity.values());

            onIdsMissingFromPalette = builder.comment(
                "How should Chocolate handle biome IDs found in the biome data but not recorded in the palette? (This will happen when the serialization contract was broken by external means)",
                "NONE = Default vanilla behavior (regenerate the biome)",
                "LOG = Default behavior plus logging an error message",
                "THROW = Forcibly throw an error (crash)"
            ).defineEnum("onIdsMissingFromPalette", Severity.LOG, Severity.values());

            builder.pop();
        }
    }
}
