package kladipolis.apocalypsemobs.event;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.core.entity.citizen.EntityCitizen;
import kladipolis.apocalypsemobs.MinecoloniesAPIHandler;
import kladipolis.apocalypsemobs.apocalypsemobs;
import kladipolis.apocalypsemobs.entity.ApocalypseHorseman;
import kladipolis.apocalypsemobs.entity.IColonyBound;
import kladipolis.apocalypsemobs.entity.Pestilence;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static kladipolis.apocalypsemobs.apocalypsemobs.RED_SKELETON;

public class InhabitantsDamageSpawner<T extends Entity & IColonyBound> extends ColonySpawner<T> {

    public InhabitantsDamageSpawner() {
        List<EntityType<? extends T>> t = new ArrayList<>();
        t.add((EntityType<? extends T>) RED_SKELETON.get());
        this.setSpawnClasses(t);
    }

    public static final List<Class<? extends LivingEntity>> INHABITANTS = Arrays.asList(
            Player.class,
            EntityCitizen.class,
            Pig.class,
            Cow.class,
            Sheep.class,
            Chicken.class
    );

    public void spawnEvent(LivingDamageEvent.Post abstractEvent) {
        if(abstractEvent instanceof LivingDamageEvent.Post event) {
            for(Class<? extends LivingEntity> mobClass : INHABITANTS) {
                if(event.getEntity().getClass().equals(mobClass)) {
                    colony = MinecoloniesAPIHandler.getEntityColony(event.getEntity());
                    if (colony == null) {
                        return;
                    }
                    if (event.getEntity().level() instanceof ServerLevel serverLevel) {
                        super.spawn(serverLevel);
                    }
                }
            }
        }
    }
}
