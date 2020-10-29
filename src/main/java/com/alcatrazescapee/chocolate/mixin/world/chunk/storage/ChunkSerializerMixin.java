package com.alcatrazescapee.chocolate.mixin.world.chunk.storage;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IObjectIntIterable;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.chunk.storage.ChunkSerializer;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.server.ServerWorld;

import com.alcatrazescapee.chocolate.common.biome.BiomeContainerSerializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Hooks for advanced {@link BiomeContainer} serialization
 *
 * @see BiomeContainerSerializer
 */
@Mixin(ChunkSerializer.class)
public abstract class ChunkSerializerMixin
{
    @Redirect(method = "read", at = @At(value = "NEW", target = "net/minecraft/world/biome/BiomeContainer"))
    private static BiomeContainer redirect$read$newBiomeContainer(IObjectIntIterable<Biome> biomeRegistry, ChunkPos chunkPos, BiomeProvider biomeProvider, @Nullable int[] biomeData, ServerWorld worldIn, TemplateManager templateManagerIn, PointOfInterestManager poiManager, ChunkPos unused, CompoundNBT rootNbt)
    {
        final CompoundNBT levelNbt = rootNbt.getCompound("Level");
        return BiomeContainerSerializer.readBiomeContainer(worldIn.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY), chunkPos, biomeProvider, biomeData, levelNbt);
    }

    @Inject(method = "write", at = @At("RETURN"))
    private static void inject$write(ServerWorld worldIn, IChunk chunkIn, CallbackInfoReturnable<CompoundNBT> cir)
    {
        final CompoundNBT levelNbt = cir.getReturnValue().getCompound("Level");
        BiomeContainerSerializer.writeBiomeContainer(chunkIn.getBiomes(), levelNbt);
    }
}
