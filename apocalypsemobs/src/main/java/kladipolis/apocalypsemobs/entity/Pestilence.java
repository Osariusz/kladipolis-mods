package kladipolis.apocalypsemobs.entity;

import com.minecolonies.core.entity.citizen.EntityCitizen;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Pestilence extends ApocalypseHorseman {
    //TODO: Add farmland replacement and progress bars for goals
    public final List<Class<? extends LivingEntity>> POISONABLE_MOBS = Arrays.asList(
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
        //TODO: Find farmland and poison goals
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, (double)0.25F);
    }

    public List<LivingEntity> getNearbyPoisonableEntities() {
        List<LivingEntity> nearbyInhabitants = new ArrayList<>();
        for(Class<? extends LivingEntity> poisonableClass : POISONABLE_MOBS) {
            nearbyInhabitants.addAll(getNearbyOfType(poisonableClass));
        }
        return nearbyInhabitants;
    }

    public <T extends LivingEntity> List<T> getNearbyOfType(Class<T> livingClass) {
        final TargetingConditions t = TargetingConditions.forNonCombat().range(16.0).ignoreLineOfSight().ignoreInvisibilityTesting();
        return Pestilence.this.level().getNearbyEntities(
                        livingClass,
                        t,
                        Pestilence.this,
                        Pestilence.this.getBoundingBox().inflate(16.0));
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
