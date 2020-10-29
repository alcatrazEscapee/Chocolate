package com.alcatrazescapee.chocolate.mixin.world.biome;

import net.minecraft.util.IObjectIntIterable;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeContainer;

import com.alcatrazescapee.chocolate.common.biome.BiomeBridge;
import com.alcatrazescapee.chocolate.common.biome.BiomeContainerBridge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BiomeContainer.class)
@SuppressWarnings("ConstantConditions")
public abstract class BiomeContainerMixin implements BiomeContainerBridge
{
    @Shadow @Final private Biome[] biomes;

    /**
     * A copy of the biome registry, with a narrower type than the super class requires
     */
    private Registry<Biome> chocolate$biomeRegistry;

    @Override
    public Biome[] bridge$getInternalBiomeArray()
    {
        return biomes;
    }

    @Override
    public Registry<Biome> bridge$getActualBiomeRegistry()
    {
        return chocolate$biomeRegistry;
    }

    /**
     * Verifies that the passed in biome registry was of a wider type than vanilla assumes it to be.
     * This is *technically* breaking the vanilla contract here, and it may cause issues.
     * If this is the case, we dump *A WHOLE LOAD* of information into the log to try and diagnose the cause
     * This is done in order to ensure that when serializing, we have access to a full registry, and can safeguard against potential error propigation later.
     */
    @Inject(method = "<init>(Lnet/minecraft/util/IObjectIntIterable;[Lnet/minecraft/world/biome/Biome;)V", at = @At("RETURN"))
    private void inject$init(IObjectIntIterable<Biome> biomeRegistry, Biome[] biomes, CallbackInfo ci)
    {
        if (!(biomeRegistry instanceof Registry))
        {
            throw new IllegalArgumentException("[Please Report this to Chocolate!] Biome Registry was not a subclass of Registry<Biome>. This is very bad and will cause many problems!");
        }
        this.chocolate$biomeRegistry = (Registry<Biome>) biomeRegistry;
    }

    /**
     * Modify biome serialization to do two important things:
     * 1. (A minor optimization) - don't unduly query registries for IDs, should be faster as this will not often have many different biome IDs
     * 2. (The important fix) - Instead of directly serializing biome -> (registry) -> int, use {@link BiomeBridge} to go biome -> (bridge) -> registry key -> (registry) -> int
     */
    @Inject(method = "writeBiomes", at = @At(value = "HEAD"), cancellable = true)
    private void inject$writeBiomes(CallbackInfoReturnable<int[]> cir)
    {
        final int[] biomeIds = new int[biomes.length];

        Biome lastBiome = null;
        int lastId = -1;

        for (int i = 0; i < biomes.length; i++)
        {
            final Biome biome = biomes[i];
            if (biome != lastBiome)
            {
                lastBiome = biome;
                lastId = chocolate$biomeRegistry.getId(chocolate$biomeRegistry.get(((BiomeBridge)(Object) biome).bridge$getKey()));
            }
            biomeIds[i] = lastId;
        }
        cir.setReturnValue(biomeIds);
    }
}
