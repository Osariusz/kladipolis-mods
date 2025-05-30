package kladipolis.apocalypsemobs.event;

import com.minecolonies.api.colony.IColony;
import kladipolis.apocalypsemobs.apocalypsemobs;
import kladipolis.apocalypsemobs.entity.IColonyBound;
import kladipolis.apocalypsemobs.entity.Pestilence;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ColonySpawner<T extends Entity & IColonyBound> {
    protected IColony colony;
    protected List<EntityType<T>> spawnClasses = new ArrayList<>();

    public void setSpawnClasses(List<EntityType<? extends T>> spawnClasses) {
        this.spawnClasses = new ArrayList<>();
        this.spawnClasses.addAll(spawnClasses.stream().map(entityType -> (EntityType<T>) entityType).toList());
    }

    public void spawn(ServerLevel level) {
        if(colony == null) {
            return;
        }
        for(EntityType<T> entityType : spawnClasses) {
            T mob = entityType.spawn(level, colony.getCenter(), MobSpawnType.EVENT);
            if(mob != null) {
                mob.initialize(colony);
            }
        }
    }
}
