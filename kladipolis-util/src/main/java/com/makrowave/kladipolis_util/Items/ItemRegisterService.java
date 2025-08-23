package com.makrowave.kladipolis_util.Items;

import com.makrowave.kladipolis_util.KladipolisUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

import static com.makrowave.kladipolis_util.Blocks.BlockRegisterService.FIRE_GLEAM_BLOCK;

public class ItemRegisterService {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(KladipolisUtil.MODID);

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, KladipolisUtil.MODID);

    public static final DeferredItem<Item> FIRE_GLEAM_SEEDS = ITEMS.register("fire_gleam_seeds",
            () -> new FireGleamSeeds(FIRE_GLEAM_BLOCK.get(), new Item.Properties()));

    public static final DeferredItem<Item> FIRE_GLEAM = ITEMS.register("fire_gleam",
            () -> new FuelItem(new Item.Properties(), 1600));

    public static final DeferredItem<Item> EQUIPMENT_UPGRADE_1 = ITEMS.register("equipment_upgrade_1",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> EQUIPMENT_UPGRADE_2 = ITEMS.register("equipment_upgrade_2",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> EQUIPMENT_UPGRADE_3 = ITEMS.register("equipment_upgrade_3",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> EQUIPMENT_UPGRADE_4 = ITEMS.register("equipment_upgrade_4",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> EQUIPMENT_UPGRADE_5 = ITEMS.register("equipment_upgrade_5",
            () -> new Item(new Item.Properties()));
    //Żkocja currency
    public static final DeferredItem<Item> BLOONIK = ITEMS.register("bloonik",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> DABLOON = ITEMS.register("dabloon",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> MOABLOON = ITEMS.register("moabloon",
            () -> new Item(new Item.Properties()));


    private static final List<DeferredItem<Item>> CREATIVE_TAB_ITEMS = List.of(
            FIRE_GLEAM_SEEDS,
            FIRE_GLEAM,
            EQUIPMENT_UPGRADE_1,
            EQUIPMENT_UPGRADE_2,
            EQUIPMENT_UPGRADE_3,
            EQUIPMENT_UPGRADE_4,
            EQUIPMENT_UPGRADE_5,
            BLOONIK,
            DABLOON,
            MOABLOON
    );



    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> KLADIPOLIS_TAB = CREATIVE_MODE_TABS.register("kladipolis_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.kladipolisutil")) //The language key for the title of your CreativeModeTab
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> FIRE_GLEAM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                CREATIVE_TAB_ITEMS.forEach(item -> output.accept(item.get()));
            }).build());

    public static void registerCreativeTab(IEventBus modEventBus) {
        CREATIVE_MODE_TABS.register(modEventBus);
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
