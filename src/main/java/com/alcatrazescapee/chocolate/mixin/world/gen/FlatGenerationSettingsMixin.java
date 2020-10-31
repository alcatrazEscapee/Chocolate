package com.alcatrazescapee.chocolate.mixin.world.gen;

import java.util.function.Supplier;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.FlatGenerationSettings;

import com.alcatrazescapee.chocolate.mixin.minecraftforge.registries.ForgeRegistryEntryAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This conditionally applies the change from MinecraftForge#7434 - allows temporary compatibility with forge versions lower than this.
 */
@Mixin(FlatGenerationSettings.class)
@SuppressWarnings("ConstantConditions")
public class FlatGenerationSettingsMixin
{
    @Shadow private Supplier<Biome> biome;

    @Inject(method = "getBiomeFromSettings", at = @At("RETURN"))
    private void inject$getBiomeFromSettings(CallbackInfoReturnable<Biome> cir)
    {
        final Biome biome = cir.getReturnValue();
        final ResourceLocation id = this.biome.get().getRegistryName();
        if (biome.getRegistryName() == null && id != null)
        {
            ((ForgeRegistryEntryAccessor) (Object) biome).accessor$setRegistryName(id);
        }
    }
}
