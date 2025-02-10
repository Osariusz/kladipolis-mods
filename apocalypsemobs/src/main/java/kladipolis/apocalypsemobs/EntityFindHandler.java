package kladipolis.apocalypsemobs;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import java.util.ArrayList;
import java.util.List;

public class EntityFindHandler {

     LivingEntity entity;
     double range;

     public EntityFindHandler(LivingEntity entity, double range) {
         this.entity = entity;
         this.range = range;
     }

    public <T extends LivingEntity> List<T> getNearbyOfType(Class<T> livingClass) {
        final TargetingConditions t = TargetingConditions.forNonCombat().range(range).ignoreLineOfSight().ignoreInvisibilityTesting();
        return entity.level().getNearbyEntities(
                livingClass,
                t,
                entity,
                entity.getBoundingBox().inflate(range));
    }

    public List<LivingEntity> getNearbyPoisonableEntities(List<Class<? extends LivingEntity>> entityTypes) {
        List<LivingEntity> nearbyInhabitants = new ArrayList<>();
        for(Class<? extends LivingEntity> poisonableClass : entityTypes) {
            nearbyInhabitants.addAll(getNearbyOfType(poisonableClass));
        }
        return nearbyInhabitants;
    }
}
