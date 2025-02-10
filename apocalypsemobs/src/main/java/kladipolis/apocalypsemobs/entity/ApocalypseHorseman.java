package kladipolis.apocalypsemobs.entity;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.core.entity.citizen.EntityCitizen;
import kladipolis.apocalypsemobs.goal.FindCityCenterGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.Set;

public abstract class ApocalypseHorseman extends PathfinderMob implements IColonyBound {

    protected IColony colony;
    final protected int CRUCIAL_PRIORITY = 2;
    final protected int PROGRESSION_PRIORITY = 3;
    final protected int OPTIONAL_PRIORITY = 64;

    //TODO: disable fall damage
    public ApocalypseHorseman(EntityType<? extends ApocalypseHorseman> entityType, Level level) {
        super(entityType, level);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(CRUCIAL_PRIORITY, new MeleeAttackGoal(this, 1.0F, false));
        this.targetSelector.addGoal(CRUCIAL_PRIORITY, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(CRUCIAL_PRIORITY, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(CRUCIAL_PRIORITY, new NearestAttackableTargetGoal<>(this, EntityCitizen.class, true));
        addBehaviourGoals();
        this.goalSelector.addGoal(OPTIONAL_PRIORITY, new FindCityCenterGoal<>(this));

    }

    protected abstract void addBehaviourGoals();

    public static void spawnEvent(LivingDamageEvent.Post event) {}

    public void initialize(IColony colony) {
        this.colony = colony;
        teleportToColony();
    }

    public void teleportToColony() {
        BlockPos position;
        if (colony.getBuildingManager().getTownHall() != null) {
            position = colony.getBuildingManager().getTownHall().getPosition();
        } else {
            position = colony.getCenter();
        }

        ServerLevel world = getServer().getLevel(colony.getDimension());
        position = BlockPosUtil.findAround(world, position, 5, 5, (predWorld, predPos) -> predWorld.getBlockState(predPos).isAir() && predWorld.getBlockState(predPos.above()).isAir());
        if (position != null) {
            ChunkPos chunkpos = new ChunkPos(position);
            world.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, chunkpos, 1, getId());
            stopRiding();

            teleportTo(world, (double)position.getX(), (double)position.getY(), (double)position.getZ(), Set.of(), getYRot(), getXRot());
        }
    }


    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, (double)0.25F);
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

    public IColony getColony() {
        return colony;
    }

}
