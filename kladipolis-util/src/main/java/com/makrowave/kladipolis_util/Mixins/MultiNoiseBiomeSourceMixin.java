package com.makrowave.kladipolis_util.Mixins;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiNoiseBiomeSource.class)
public class MultiNoiseBiomeSourceMixin {
    @Inject(method = "getNoiseBiome", at = @At("TAIL"), cancellable = true)
    private void modifyBiome(int x, int y, int z, Climate.Sampler sampler, CallbackInfoReturnable<Holder<Biome>> cir) {
        if(y < 64) {
            MultiNoiseBiomeSource source = (MultiNoiseBiomeSource)(Object)this;
            Holder<Biome> biomeAt100 = source.getNoiseBiome(x, 100, z, sampler);
            if (biomeAt100.is(Biomes.TAIGA) && y < 100) {
                cir.setReturnValue(biomeAt100);
            }
        }
    }
}
