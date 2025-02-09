package kladipolis.apocalypsemobs.entity;

import com.minecolonies.core.entity.citizen.EntityCitizen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class RedSkeleton extends PathfinderMob {
    //TODO: Disable fall damage, add farmland replacement and progress bars for goals
    public final List<Class<? extends LivingEntity>> POISONABLE_MOBS = Arrays.asList(
            Player.class,
            EntityCitizen.class,
            Pig.class,
            Cow.class,
            Sheep.class,
            Chicken.class
    );

    public RedSkeleton(EntityType<? extends RedSkeleton> entityType, Level level) {
        super(entityType, level);
        System.out.println(entityType);
        System.out.println("eeelo");
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, (double)1.0F, false));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this, new Class[0]));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, (double)1.0F));
        //TODO: Find farmland and poison goals
        this.goalSelector.getAvailableGoals();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, (double)0.25F);
    }

    public List<? extends LivingEntity> getNearbyPoisonableEntities() {
        List<? extends LivingEntity> nearbyInhabitants = new ArrayList<>();
        for(Class poisonableClass : POISONABLE_MOBS) {
            nearbyInhabitants.addAll(getNearbyOfType(poisonableClass));
        }
        return nearbyInhabitants;
    }

    public <T extends LivingEntity> List<T> getNearbyOfType(Class<T> livingClass) {
        final TargetingConditions t = TargetingConditions.forNonCombat().range(16.0).ignoreLineOfSight().ignoreInvisibilityTesting();
        return RedSkeleton.this.level().getNearbyEntities(
                        livingClass,
                        t,
                        RedSkeleton.this,
                        RedSkeleton.this.getBoundingBox().inflate(16.0));
    }

    public void applyPoisonNearby() {
        for(LivingEntity player : getNearbyPoisonableEntities()) {
            if (!player.hasEffect(MobEffects.POISON)) {
                player.addEffect(new MobEffectInstance(MobEffects.POISON, 30, 0));
            }
        }
    }

    public void tick() {
        System.out.println(this.goalSelector.getAvailableGoals());
        if (!this.level().isClientSide && this.isAlive() && !this.isNoAi()) {
            applyPoisonNearby();
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
