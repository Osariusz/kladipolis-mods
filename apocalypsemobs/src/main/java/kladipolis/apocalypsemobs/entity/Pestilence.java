package kladipolis.apocalypsemobs.entity;

import com.minecolonies.api.IMinecoloniesAPI;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.core.entity.citizen.EntityCitizen;
import com.sun.jna.platform.win32.COM.util.Factory;
import kladipolis.apocalypsemobs.*;
import kladipolis.apocalypsemobs.goal.FindEntityGroupGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static kladipolis.apocalypsemobs.apocalypsemobs.TIME_SINCE_APOCALYPSE_DEATH_STRING;

public class Pestilence extends ApocalypseHorseman {

    final int EFFECT_DURATION = 30;
    final int EFFECT_AMPLIFIER = 0;

    //TODO: progress bars for goals
    public static final List<Class<? extends LivingEntity>> POISONABLE_MOBS = Arrays.asList(
            Player.class,
            EntityCitizen.class,
            Pig.class,
            Cow.class,
            Sheep.class,
            Chicken.class
    );

    public Pestilence(EntityType<? extends Pestilence> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void addBehaviourGoals() {
        this.targetSelector.addGoal(CRUCIAL_PRIORITY, new FindEntityGroupGoal<>(this, POISONABLE_MOBS, true));
    }

    public static void spawnEvent(LivingDamageEvent.Post event) {
        WorldData data = WorldDataManager.get(event.getEntity().level().getServer().overworld());
        CompoundTag myData = data.getData();
        int counter = myData.getInt(TIME_SINCE_APOCALYPSE_DEATH_STRING);
        if(counter < TICKS_FOR_RESPAWN) {
            return;
        }
        for(Class<? extends LivingEntity> mobClass : POISONABLE_MOBS) {
            if(event.getEntity().getClass().equals(mobClass)) {
                IColony entityColony = MinecoloniesAPIHandler.getEntityColony(event.getEntity());
                if(entityColony == null) {
                    return;
                }
                BlockPos colonyPosition = entityColony.getCenter();
                Random random = new Random();
                int r = random.nextInt(5);
                if(r == 4) {
                    Pestilence pestilence = apocalypsemobs.RED_SKELETON.get().spawn((ServerLevel)event.getEntity().level(), colonyPosition, MobSpawnType.EVENT);
                    if(pestilence != null) {
                        pestilence.initialize(entityColony);
                    }
                }
            }
        }
    }

    public static AttributeSupplier.@NotNull Builder createMobAttributes() {
        AttributeSupplier.Builder builder = ApocalypseHorseman.createMobAttributes();
        return builder;
    }

    public int getEffectRange() {
        return 16;
    }

    public void applyPoisonNearby() {
        EntityFindHandler entityFindHandler = new EntityFindHandler(this, getEffectRange());
        for(LivingEntity player : entityFindHandler.getNearbyPoisonableEntities(POISONABLE_MOBS)) {
            if (!player.hasEffect(MobEffects.POISON)) {
                player.addEffect(new MobEffectInstance(MobEffects.POISON, EFFECT_DURATION, EFFECT_AMPLIFIER));
            }
        }
    }

    public void zeroDeathCounter() {
        //TODO: add only your colony handling
        WorldData data = WorldDataManager.get(this.level().getServer().overworld());
        CompoundTag myData = data.getData();
        CompoundTag newData = myData.copy();
        newData.putInt(TIME_SINCE_APOCALYPSE_DEATH_STRING, 0);
        data.updateData(newData);
    }

    public void tick() {
        if (!this.level().isClientSide && this.isAlive() && !this.isNoAi()) {
            applyPoisonNearby();
            zeroDeathCounter();
        }
        super.tick();
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.SKELETON_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.SKELETON_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.SKELETON_DEATH;
    }

    protected SoundEvent getStepSound() {
        return SoundEvents.SKELETON_STEP;
    }
}
