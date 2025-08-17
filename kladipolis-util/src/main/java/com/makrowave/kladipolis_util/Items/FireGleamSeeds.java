package com.makrowave.kladipolis_util.Items;

import com.makrowave.kladipolis_util.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;

public class FireGleamSeeds extends ItemNameBlockItem {
    public FireGleamSeeds(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        ResourceKey<Biome> biome = level.getBiome(pos).unwrapKey().orElse(null);
        if (biome != null &&  Constants.FIRE_GLEAM_BIOMES.contains(biome)) {
            return super.useOn(context);
        }
        return InteractionResult.FAIL;
    }


    @Override
    public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
        return 50;
    }

}
