/*
 * Part of the Chocolate mod by AlcatrazEscapee.
 * Licensed under the MIT License. See LICENSE.md for details.
 */

package com.alcatrazescapee.chocolate.mixin.util.registry;

import com.alcatrazescapee.chocolate.common.util.LenientUnboundedMapCodec;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Fixes MC-197860
 * Would be fixed upstream by Mojang in https://github.com/Mojang/DataFixerUpper/pull/55
 * Would be fixed by Forge in https://github.com/MinecraftForge/MinecraftForge/pull/7527
 * (I have spoken to Lex about the above PR, he said he'd rather Mojang fixed it. So be it. That's why this mod exists.)
 *
 * Many more details are in the linked issues and PRs. This solution simply replaces the codec at the least invasive call side (to do with mixin)
 */
@Mixin(SimpleRegistry.class)
public abstract class SimpleRegistryMixin<T> extends MutableRegistry<T>
{
    @SuppressWarnings("unchecked")
    @Inject(method = "directCodec", at = @At("HEAD"), cancellable = true, require = 0)
    private static <T> void redirect$directCodec$unboundedMap(RegistryKey<? extends Registry<T>> registryKey, Lifecycle lifecycle, Codec<T> mapCodec, CallbackInfoReturnable<Codec<SimpleRegistry<T>>> cir)
    {
        cir.setReturnValue(new LenientUnboundedMapCodec<>(ResourceLocation.CODEC.xmap(RegistryKey.elementKey(registryKey), RegistryKey::location), mapCodec).xmap(registryMap -> {
            SimpleRegistry<T> registry = new SimpleRegistry<>(registryKey, lifecycle);
            registryMap.forEach((key, value) -> registry.register(key, value, lifecycle));
            return registry;
        }, (registryIn) -> ImmutableMap.copyOf(((SimpleRegistryAccessor<T>) registryIn).accessor$getKeyStorage())));
    }

    private SimpleRegistryMixin(RegistryKey<? extends Registry<T>> registryKey, Lifecycle lifecycle)
    {
        super(registryKey, lifecycle);
    }
}
