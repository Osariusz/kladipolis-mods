package com.makrowave.kladipolis_util.mixins;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(MultiNoiseBiomeSource.class)
public class MultiNoiseBiomeSourceMixin {
    private static final int COLD_BIOME_THRESHOLD = 0;
    private static final int BIOME_PROBE_Y = 100;
    private static final int BIOME_FORCE_MAX = 64;

    List<ResourceKey<Biome>> EXCEPTIONS = new ArrayList<>(List.of(
            Biomes.DEEP_DARK
    ));

    private boolean isForcedBiome(Holder<Biome> biome) {
        return biome.value().getModifiedClimateSettings().temperature() <= COLD_BIOME_THRESHOLD || biome.is(Biomes.RIVER);
    }

    private boolean isExceptionBiome(Holder<Biome> biome) {
        return EXCEPTIONS.stream().anyMatch(
                (resourceKey) -> resourceKey.equals(
                        biome.unwrapKey().orElse(null)
                )
        );
    }

    @Inject(method = "getNoiseBiome", at = @At("TAIL"), cancellable = true)
    private void modifyBiome(int x, int y, int z, Climate.Sampler sampler, CallbackInfoReturnable<Holder<Biome>> cir) {
        if(y < BIOME_FORCE_MAX) {
            MultiNoiseBiomeSource source = (MultiNoiseBiomeSource)(Object)this;
            Holder<Biome> biomeAt100 = source.getNoiseBiome(x, BIOME_PROBE_Y, z, sampler);
            Holder<Biome> originalBiome = cir.getReturnValue();
            if (isForcedBiome(biomeAt100) && !isExceptionBiome(originalBiome)) {
                cir.setReturnValue(biomeAt100);
            }
        }
    }
}
