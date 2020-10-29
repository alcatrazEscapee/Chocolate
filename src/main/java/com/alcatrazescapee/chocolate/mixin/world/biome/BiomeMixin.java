package com.alcatrazescapee.chocolate.mixin.world.biome;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import com.alcatrazescapee.chocolate.common.biome.BiomeBridge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Biome.class)
@SuppressWarnings("ConstantConditions")
public abstract class BiomeMixin extends ForgeRegistryEntry.UncheckedRegistryEntry<Biome> implements BiomeBridge
{
    @Unique
    private RegistryKey<Biome> chocolate$key;

    @Override
    public RegistryKey<Biome> bridge$getKey()
    {
        if (chocolate$key == null)
        {
            // Lazily compute the key from the forge registry name - as hooking into that method with an inject is very difficult
            ResourceLocation forgeRegistryId = getRegistryName();
            if (forgeRegistryId != null)
            {
                this.chocolate$key = RegistryKey.create(Registry.BIOME_REGISTRY, forgeRegistryId);
                return this.chocolate$key;
            }
            else
            {
                // Before throwing an error, see if we can bring together any additional context about why this has occurred
                StringBuilder error = new StringBuilder("A biome was missing a key! The biome was [").append(this).append(']');
                final MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                if (server != null)
                {
                    final Registry<Biome> registry = server.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
                    final ResourceLocation id = registry.getKey((Biome)(Object)this);
                    final int intId = registry.getId((Biome)(Object)this);
                    error.append(" The current registry reported the biome as [").append(id).append("] with an id of [").append(intId).append(']');
                }
                final ResourceLocation forgeId = ForgeRegistries.BIOMES.getKey((Biome)(Object)this);
                if (forgeId != null)
                {
                    error.append(" The Forge registry reported the biome as [").append(forgeId).append(']');
                }
                throw new IllegalStateException(error.toString());
            }
        }
        return chocolate$key;
    }

    @Override
    public void bridge$setKey(RegistryKey<Biome> key)
    {
        this.chocolate$key = key;
    }
}
