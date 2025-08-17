package com.makrowave.kladipolis_util.Mixins;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.inventory.AnvilMenu;
import net.neoforged.neoforge.common.CommonHooks;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AnvilMenu.class)
public class AnvilMenuMixin {

    @Shadow
    public int repairItemCountCost;

    @Shadow
    private String itemName;

    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * @author makrowave
     * @reason Custom anvil behavior: no enchantment level addition, only first item enchantments preserved
     */
    @Overwrite
    public void createResult() {
        AnvilMenuAccessor anvilAccessor = (AnvilMenuAccessor) this;
        ItemCombinerMenuAccessor combinerAccessor = (ItemCombinerMenuAccessor) this;
        ItemStack itemstack = combinerAccessor.getInputSlots().getItem(0);
        anvilAccessor.getCost().set(1);
        int i = 0;
        long j = 0L;
        int k = 0;

        if (!itemstack.isEmpty() && EnchantmentHelper.canStoreEnchantments(itemstack)) {
            ItemStack itemstack1 = itemstack.copy();
            ItemStack itemstack2 = combinerAccessor.getInputSlots().getItem(1);
            ItemEnchantments.Mutable itemenchantments$mutable = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(itemstack1));
            j += (long)(Integer)itemstack.getOrDefault(DataComponents.REPAIR_COST, 0) + (long)(Integer)itemstack2.getOrDefault(DataComponents.REPAIR_COST, 0);
            this.repairItemCountCost = 0;
            boolean flag = false;

            if (!CommonHooks.onAnvilChange((AnvilMenu)(Object)this, itemstack, itemstack2, combinerAccessor.getResultSlots(), this.itemName, j, combinerAccessor.getPlayer())) {
                return;
            }

            if (!itemstack2.isEmpty()) {
                flag = itemstack2.has(DataComponents.STORED_ENCHANTMENTS);

                if (itemstack1.isDamageableItem() && itemstack1.getItem().isValidRepairItem(itemstack, itemstack2)) {
                    int l2 = Math.min(itemstack1.getDamageValue(), itemstack1.getMaxDamage() / 4);
                    if (l2 <= 0) {
                        combinerAccessor.getResultSlots().setItem(0, ItemStack.EMPTY);
                        anvilAccessor.getCost().set(0);
                        return;
                    }

                    int j3;
                    for(j3 = 0; l2 > 0 && j3 < itemstack2.getCount(); ++j3) {
                        int k3 = itemstack1.getDamageValue() - l2;
                        itemstack1.setDamageValue(k3);
                        ++i;
                        l2 = Math.min(itemstack1.getDamageValue(), itemstack1.getMaxDamage() / 4);
                    }

                    this.repairItemCountCost = j3;
                } else {
                    if (!flag && (!itemstack1.is(itemstack2.getItem()) || !itemstack1.isDamageableItem())) {
                        combinerAccessor.getResultSlots().setItem(0, ItemStack.EMPTY);
                        anvilAccessor.getCost().set(0);
                        return;
                    }

                    if (itemstack1.isDamageableItem() && !flag) {
                        int l = itemstack.getMaxDamage() - itemstack.getDamageValue();
                        int i1 = itemstack2.getMaxDamage() - itemstack2.getDamageValue();
                        int j1 = i1 + itemstack1.getMaxDamage() * 12 / 100;
                        int k1 = l + j1;
                        int l1 = itemstack1.getMaxDamage() - k1;
                        if (l1 < 0) {
                            l1 = 0;
                        }

                        if (l1 < itemstack1.getDamageValue()) {
                            itemstack1.setDamageValue(l1);
                            i += 2;
                        }
                    }

                    // CUSTOM LOGIC: Only preserve enchantments from first item, don't add levels
                    ItemEnchantments itemenchantments = ItemEnchantments.EMPTY;
                    boolean isSecondItemBook = itemstack2.has(DataComponents.STORED_ENCHANTMENTS)
                            && itemstack2.isBookEnchantable(itemstack1);

                    if (isSecondItemBook) {
                        itemenchantments = EnchantmentHelper.getEnchantmentsForCrafting(itemstack2);
                    }

                    boolean flag2 = false;
                    boolean flag3 = false;

                    for (Object2IntMap.Entry<Holder<Enchantment>> entry : itemenchantments.entrySet()) {
                        Holder<Enchantment> holder = entry.getKey();
                        int leftLevel = itemenchantments$mutable.getLevel(holder); // Level on base item
                        int bookLevel = entry.getIntValue(); // Level from book

                        Enchantment enchantment = holder.value();
                        boolean isApplicable = itemstack.supportsEnchantment(holder);

                        // Allow creative players to override compatibility
                        if (combinerAccessor.getPlayer().getAbilities().instabuild) {
                            isApplicable = true;
                        }

                        // Check for conflicts
                        for (Holder<Enchantment> other : itemenchantments$mutable.keySet()) {
                            if (!other.equals(holder) && !Enchantment.areCompatible(holder, other)) {
                                isApplicable = false;
                                ++i;
                            }
                        }

                        if (!isApplicable) {
                            flag3 = true;
                            continue;
                        }

                        // Apply if:
                        // - Not on base item (i.e., new enchantment)
                        // - Book enchantment has higher level
                        if (leftLevel == 0 || bookLevel > leftLevel) {
                            itemenchantments$mutable.set(holder, bookLevel);
                            int cost = enchantment.getAnvilCost();
                            if (flag) cost = Math.max(1, cost / 2);
                            i += cost * bookLevel;

                            if (itemstack.getCount() > 1) {
                                i = 40; // Same as vanilla rule
                            }

                            flag2 = true;
                        }
                    }


                    if (flag3 && !flag2) {
                        combinerAccessor.getResultSlots().setItem(0, ItemStack.EMPTY);
                        anvilAccessor.getCost().set(0);
                        return;
                    }
                }
            }

            // Handle item renaming
            if (this.itemName != null && !StringUtil.isBlank(this.itemName)) {
                if (!this.itemName.equals(itemstack.getHoverName().getString())) {
                    k = 1;
                    i += k;
                    itemstack1.set(DataComponents.CUSTOM_NAME, Component.literal(this.itemName));
                }
            } else if (itemstack.has(DataComponents.CUSTOM_NAME)) {
                k = 1;
                i += k;
                itemstack1.remove(DataComponents.CUSTOM_NAME);
            }

            if (flag && !itemstack1.isBookEnchantable(itemstack2)) {
                itemstack1 = ItemStack.EMPTY;
            }

            int k2 = (int)Mth.clamp(j + (long)i, 0L, 2147483647L);
            anvilAccessor.getCost().set(k2);
            if (i <= 0) {
                itemstack1 = ItemStack.EMPTY;
            }

            if (k == i && k > 0 && anvilAccessor.getCost().get() >= 40) {
                anvilAccessor.getCost().set(39);
            }

            if (anvilAccessor.getCost().get() >= 40 && !combinerAccessor.getPlayer().getAbilities().instabuild) {
                itemstack1 = ItemStack.EMPTY;
            }

            if (!itemstack1.isEmpty()) {
                int i3 = (Integer)itemstack1.getOrDefault(DataComponents.REPAIR_COST, 0);
                if (i3 < (Integer)itemstack2.getOrDefault(DataComponents.REPAIR_COST, 0)) {
                    i3 = (Integer)itemstack2.getOrDefault(DataComponents.REPAIR_COST, 0);
                }

                if (k != i || k == 0) {
                    i3 = AnvilMenu.calculateIncreasedRepairCost(i3);
                }

                itemstack1.set(DataComponents.REPAIR_COST, i3);
                EnchantmentHelper.setEnchantments(itemstack1, itemenchantments$mutable.toImmutable());
            }

            combinerAccessor.getResultSlots().setItem(0, itemstack1);
            ((AnvilMenu)(Object)this).broadcastChanges();
        } else {
            combinerAccessor.getResultSlots().setItem(0, ItemStack.EMPTY);
            anvilAccessor.getCost().set(0);
        }
    }
}