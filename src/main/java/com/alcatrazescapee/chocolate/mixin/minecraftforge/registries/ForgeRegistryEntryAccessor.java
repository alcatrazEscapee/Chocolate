package com.alcatrazescapee.chocolate.mixin.minecraftforge.registries;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * This conditionally applies the change from MinecraftForge#7434 - allows temporary compatibility with forge versions lower than this.
 */
@Mixin(ForgeRegistryEntry.class)
public interface ForgeRegistryEntryAccessor
{
    @Accessor(value = "registryName", remap = false)
    void accessor$setRegistryName(ResourceLocation registryName);
}
