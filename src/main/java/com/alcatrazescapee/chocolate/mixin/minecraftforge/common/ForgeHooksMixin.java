package com.alcatrazescapee.chocolate.mixin.minecraftforge.common;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeAmbience;
import net.minecraft.world.biome.BiomeGenerationSettings;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraftforge.common.ForgeHooks;

import com.alcatrazescapee.chocolate.mixin.minecraftforge.registries.ForgeRegistryEntryAccessor;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This conditionally applies the change from MinecraftForge#7434 - allows temporary compatibility with forge versions lower than this.
 */
@Mixin(ForgeHooks.class)
public class ForgeHooksMixin
{
    @Inject(method = "enhanceBiome", at = @At(value = "RETURN"), remap = false)
    private static void inject$enhanceBiome(ResourceLocation name, Biome.Climate climate, Biome.Category category, Float depth, Float scale, BiomeAmbience effects, BiomeGenerationSettings gen, MobSpawnInfo spawns, RecordCodecBuilder.Instance<Biome> codec, ForgeHooks.BiomeCallbackFunction callback, CallbackInfoReturnable<Biome> cir)
    {
        final Biome biome = cir.getReturnValue();
        if (biome.getRegistryName() == null)
        {
            ((ForgeRegistryEntryAccessor) (Object) biome).accessor$setRegistryName(name);
        }
    }
}
