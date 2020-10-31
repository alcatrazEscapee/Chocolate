package com.alcatrazescapee.chocolate.mixin.world.biome;

import net.minecraft.util.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeRegistry;

import com.alcatrazescapee.chocolate.common.biome.BiomeBridge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BiomeRegistry.class)
public abstract class BiomeRegistryMixin
{
    /**
     * For all vanilla minecraft hard coded biomes, copy the key to the biome when it is registered.
     * This sets the key slightly earlier than letting the forge defaulted registry handle it.
     */
    @Inject(method = "register", at = @At("RETURN"))
    private static void inject$register(int rawId, RegistryKey<Biome> key, Biome biome, CallbackInfoReturnable<Biome> cir)
    {
        BiomeBridge.of(biome).bridge$setKey(key);
    }
}
