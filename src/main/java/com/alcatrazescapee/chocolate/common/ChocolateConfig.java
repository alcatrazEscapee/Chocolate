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

    public static class Server
    {
        public final ForgeConfigSpec.BooleanValue logBiomesRemovedFromChunks;
        public final ForgeConfigSpec.BooleanValue logIdsMissingFromPalette;

        Server(ForgeConfigSpec.Builder builder)
        {
            builder.push("logging");

            logBiomesRemovedFromChunks = builder.comment("Should Chocolate log errors when a biome was found saved to a chunk, but there was no biome of that name found in the registry? (This will happen when you remove biomes.)").define("logBiomesRemovedFromChunks", true);
            logIdsMissingFromPalette = builder.comment("Should Chocolate log errors when a biome ID is found in the serialization but not recorded in the palette? (This will happen when the serialization contract was broken)").define("logIdsMissingFromPalette", true);

            builder.pop();
        }
    }
}
