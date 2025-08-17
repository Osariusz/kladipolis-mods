package com.makrowave.kladipolis_util.Blocks;

import com.makrowave.kladipolis_util.KladipolisUtil;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlockRegisterService {

    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(KladipolisUtil.MODID);

    public static final DeferredBlock<Block> FIRE_GLEAM_BLOCK = BLOCKS.register("fire_gleam",
            () -> new FireGleam(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .noCollission()
                    .randomTicks()
                    .instabreak()
                    .sound(SoundType.CROP)
                    .pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<Block> FIRE_GLEAM_FARMLAND_BLOCK = BLOCKS.register("fire_gleam_farmland",
            () -> new FireGleamFarmland(BlockBehaviour.Properties.ofFullCopy(Blocks.FARMLAND)));

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}
