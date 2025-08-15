package com.makrowave.kladipolis_util.Blocks;

import com.makrowave.kladipolis_util.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;

import java.util.ArrayList;
import java.util.List;

import static com.makrowave.kladipolis_util.KladipolisUtil.FIRE_GLEAM;
import static com.makrowave.kladipolis_util.KladipolisUtil.FIRE_GLEAM_SEEDS;

public class FireGleam extends CropBlock {

    public FireGleam(Properties properties) {
        super(properties);
    }

    @Override
    public int getMaxAge() {
        return 7;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        int age = state.getValue(this.getAgeProperty());
        if (age >= getMaxAge()) {
            return false;
        }
        ResourceKey<Biome> biome = level.getBiome(pos).unwrapKey().orElse(null);
        return biome != null && Constants.FIRE_GLEAM_BIOMES.contains(biome);
    }


    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        ResourceKey<Biome> biome = level.getBiome(pos).unwrapKey().orElse(null);
        if (biome != null &&  Constants.FIRE_GLEAM_BIOMES.contains(biome)) {
            super.randomTick(state, level, pos, random);
        }
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> drops = new ArrayList<>();
        int age = state.getValue(this.getAgeProperty());
        RandomSource random = builder.getLevel().random;
        if (age >= getMaxAge()) {
            if (random.nextFloat() < 0.5f) {
                drops.add(new ItemStack(FIRE_GLEAM.get()));
            }
            int seeds = 1 + random.nextInt(2);
            for (int i = 0; i < seeds; i++) {
                drops.add(new ItemStack(FIRE_GLEAM_SEEDS.get()));
            }
        } else {
            drops.add(new ItemStack(FIRE_GLEAM_SEEDS.get()));
        }
        return drops;
    }
}
