package com.makrowave.kladipolis_util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.List;

public class Constants {
    public static final ResourceKey<Biome> VOLCANIC_CRATER = ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath("terralith", "volcanic_crater"));
    public static final ResourceKey<Biome> VOLCANIC_PEAKS = ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath("terralith", "volcanic_peaks"));

    public static final List<ResourceKey<Biome>> FIRE_GLEAM_BIOMES = new ArrayList<>(List.of(
            VOLCANIC_CRATER, VOLCANIC_PEAKS
    ));

}
