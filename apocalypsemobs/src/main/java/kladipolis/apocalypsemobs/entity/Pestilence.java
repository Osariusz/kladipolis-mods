package kladipolis.apocalypsemobs.entity;

import com.minecolonies.api.IMinecoloniesAPI;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.core.entity.citizen.EntityCitizen;
import kladipolis.apocalypsemobs.MinecoloniesAPIHandler;
import kladipolis.apocalypsemobs.apocalypsemobs;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

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
        //TODO: poison goals
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, (double)0.25F);
    }

    public static void spawnEvent(LivingDamageEvent.Post event) {
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

    public int getEffectRange() {
        return 16;
    }

    public List<LivingEntity> getNearbyPoisonableEntities() {
        List<LivingEntity> nearbyInhabitants = new ArrayList<>();
        for(Class<? extends LivingEntity> poisonableClass : POISONABLE_MOBS) {
            nearbyInhabitants.addAll(getNearbyOfType(poisonableClass));
        }
        return nearbyInhabitants;
    }

    public <T extends LivingEntity> List<T> getNearbyOfType(Class<T> livingClass) {
        final TargetingConditions t = TargetingConditions.forNonCombat().range(getEffectRange()).ignoreLineOfSight().ignoreInvisibilityTesting();
        return Pestilence.this.level().getNearbyEntities(
                        livingClass,
                        t,
                        Pestilence.this,
                        Pestilence.this.getBoundingBox().inflate(getEffectRange()));
    }

    public void applyPoisonNearby() {
        for(LivingEntity player : getNearbyPoisonableEntities()) {
            if (!player.hasEffect(MobEffects.POISON)) {
                player.addEffect(new MobEffectInstance(MobEffects.POISON, EFFECT_DURATION, EFFECT_AMPLIFIER));
            }
        }
    }

    public void tick() {
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
