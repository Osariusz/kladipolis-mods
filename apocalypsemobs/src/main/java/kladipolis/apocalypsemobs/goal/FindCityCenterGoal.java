package kladipolis.apocalypsemobs.goal;

import kladipolis.apocalypsemobs.entity.IColonyBound;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class FindCityCenterGoal<T extends PathfinderMob & IColonyBound> extends Goal {
    private final T mob;

    public FindCityCenterGoal(T mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.mob.getRandom().nextInt(reducedTickDelay(100)) != 0) {
            return false;
        }
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    @Override
    public boolean isInterruptable() {
        return true;
    }

    @Override
    public void start() {
        this.goToCityCenter();
    }

    private void goToCityCenter() {
        if (mob.getColony() == null) return;
        BlockPos blockpos = mob.getColony().getCenter();
        if (blockpos == null) return;

        this.mob.getNavigation().moveTo((double)blockpos.getX(), (double)(blockpos.getY() + 1), (double)blockpos.getZ(), 1.0);
    }

    @Override
    public void tick() {
        this.goToCityCenter();
        this.mob.moveRelative(0.02F, new Vec3((double)this.mob.xxa, (double)this.mob.yya, (double)this.mob.zza));
        this.mob.move(MoverType.SELF, this.mob.getDeltaMovement());
    }
}
