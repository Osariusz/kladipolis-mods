package com.makrowave.kladipolis_util;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.world.item.context.UseOnContext;
import java.util.ArrayList;
import java.util.List;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(KladipolisUtil.MODID)
public class KladipolisUtil {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "kladipolisutil";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "kladipolisutil" namespace
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "kladipolisutil" namespace
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "kladipolisutil" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredBlock<Block> FIRE_GLEAM_BLOCK = BLOCKS.register("fire_gleam", () -> new CropBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.CROP).pushReaction(PushReaction.DESTROY)) {
        private final ResourceKey<Biome> VOLCANIC_CRATER = ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath("terralith", "volcanic_crater"));
        private final ResourceKey<Biome> VOLCANIC_PEAKS = ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath("terralith", "volcanic_peaks"));

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
            return biome != null && (biome.equals(VOLCANIC_CRATER) || biome.equals(VOLCANIC_PEAKS));
        }


        @Override
        public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
            ResourceKey<Biome> biome = level.getBiome(pos).unwrapKey().orElse(null);
            if (biome != null && (biome.equals(VOLCANIC_CRATER) || biome.equals(VOLCANIC_PEAKS))) {
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
    });

    public static final DeferredItem<Item> FIRE_GLEAM_SEEDS = ITEMS.register("fire_gleam_seeds", () -> new ItemNameBlockItem(FIRE_GLEAM_BLOCK.get(), new Item.Properties()) {
        private final ResourceKey<Biome> VOLCANIC_CRATER = ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath("terralith", "volcanic_crater"));
        private final ResourceKey<Biome> VOLCANIC_PEAKS = ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath("terralith", "volcanic_peaks"));

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
    });

    public static final DeferredItem<Item> FIRE_GLEAM = ITEMS.register("fire_gleam", () -> new Item(new Item.Properties()));

    // Creates a creative tab with the id "kladipolisutil:example_tab" for the example item, that is placed after the combat tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.kladipolisutil")) //The language key for the title of your CreativeModeTab
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> FIRE_GLEAM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(FIRE_GLEAM.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
                output.accept(FIRE_GLEAM_SEEDS.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
            }).build());

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public KladipolisUtil(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (KladipolisUtil) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        //modEventBus.addListener(this::addCreative);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

//    // Add the example block item to the building blocks tab
//    private void addCreative(BuildCreativeModeTabContentsEvent event) {
//        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
//            event.accept(FIRE_GLEAM);
//            event.accept(FIRE_GLEAM_SEEDS);
//        }
//    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
