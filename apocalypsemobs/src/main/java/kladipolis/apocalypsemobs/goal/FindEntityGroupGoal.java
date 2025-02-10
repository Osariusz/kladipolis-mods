package kladipolis.apocalypsemobs.goal;

import kladipolis.apocalypsemobs.EntityFindHandler;
import kladipolis.apocalypsemobs.entity.IColonyBound;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class FindEntityGroupGoal<T extends LivingEntity> extends TargetGoal {
    private final float SEARCH_RADIUS = 16;
    private static final int DEFAULT_RANDOM_INTERVAL = 10;
    protected final List<Class<? extends LivingEntity>> targetTypes;
    protected final int randomInterval;
    @Nullable
    protected List<LivingEntity> targets;
    protected TargetingConditions targetConditions;

    public FindEntityGroupGoal(Mob mob, List<Class<? extends LivingEntity>> targetType, boolean mustSee) {
        this(mob, targetType, 10, mustSee, false, null);
    }

    public FindEntityGroupGoal(Mob mob, List<Class<? extends LivingEntity>> targetTypes, int randomInterval, boolean mustSee, boolean mustReach, @Nullable Predicate<LivingEntity> targetPredicate) {
        super(mob, mustSee, mustReach);
        this.targetTypes = targetTypes;
        this.randomInterval = reducedTickDelay(randomInterval);
        this.setFlags(EnumSet.of(Flag.TARGET));
        this.targetConditions = TargetingConditions.forCombat().range(this.getFollowDistance()).selector(targetPredicate);
    }

    public boolean canUse() {
        if (this.randomInterval > 0 && this.mob.getRandom().nextInt(this.randomInterval) != 0) {
            return false;
        } else {
            this.findTarget();
            return this.targets != null  && !this.targets.isEmpty();
        }
    }

    protected void findTarget() {
        EntityFindHandler entityFindHandler = new EntityFindHandler(mob, SEARCH_RADIUS);
        targets = entityFindHandler.getNearbyPoisonableEntities(targetTypes);
        System.out.println(targets.size());
    }

    public BlockPos getTargetsAveragePosition() {
        int sumX = 0, sumY = 0, sumZ = 0;
        assert targets != null;
        int count = targets.size();

        for (LivingEntity target : targets) {
            sumX += target.blockPosition().getX();
            sumY += target.blockPosition().getY();
            sumZ += target.blockPosition().getZ();
        }

        return new BlockPos(sumX / count, sumY / count, sumZ / count);
    }

    public void start() {
        BlockPos blockpos = getTargetsAveragePosition();
        this.mob.getNavigation().moveTo((double)blockpos.getX(), (double)(blockpos.getY() + 1), (double)blockpos.getZ(), 1.0);
        super.start();
    }

}
