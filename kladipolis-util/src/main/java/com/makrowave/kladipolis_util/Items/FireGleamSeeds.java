package com.makrowave.kladipolis_util.Items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

public class FireGleamSeeds extends ItemNameBlockItem {
    private final ResourceKey<Biome> VOLCANIC_CRATER = ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath("terralith", "volcanic_crater"));
    private final ResourceKey<Biome> VOLCANIC_PEAKS = ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath("terralith", "volcanic_peaks"));

    public FireGleamSeeds(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        ResourceKey<Biome> biome = level.getBiome(pos).unwrapKey().orElse(null);
        if (biome != null && (biome.equals(VOLCANIC_CRATER) || biome.equals(VOLCANIC_PEAKS))) {
            return super.useOn(context);
        }
        return InteractionResult.FAIL;
    }
}
