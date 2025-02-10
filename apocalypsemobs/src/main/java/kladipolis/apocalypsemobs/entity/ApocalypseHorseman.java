package kladipolis.apocalypsemobs.entity;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.core.entity.citizen.EntityCitizen;
import kladipolis.apocalypsemobs.goal.FindCityCenterGoal;
import kladipolis.apocalypsemobs.goal.FindEntityGroupGoal;
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
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

//TODO: test spider AI
public abstract class ApocalypseHorseman extends PathfinderMob implements IColonyBound {

    protected IColony colony;
    final protected int CRUCIAL_PRIORITY = 2;
    final protected int PROGRESSION_PRIORITY = 3;
    final protected int OPTIONAL_PRIORITY = 64;

    final protected static double MAX_HEALTH = 100.0D;
    final protected static double ATTACK_DAMAGE = 5.0D;
    final protected static double ATTACK_SPEED = 1.5D;
    final protected static double ATTACK_KNOCKBACK = 1.5D;
    final protected static double MOVEMENT_SPEED = 0.25D;
    final protected static double KNOCKBACK_RESISTANCE = 0.9;
    final protected static double JUMP_STRENGTH = 0.7;
    final protected static double STEP_HEIGHT = 1.0F;
    final protected static double SAFE_FALL_DISTANCE = 300;

    public ApocalypseHorseman(EntityType<? extends ApocalypseHorseman> entityType, Level level) {
        super(entityType, level);
    }

    protected void registerGoals() {
        //TODO: add swimming
        this.goalSelector.addGoal(CRUCIAL_PRIORITY, new MeleeAttackGoal(this, 1.0F, false));
        this.targetSelector.addGoal(CRUCIAL_PRIORITY, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(CRUCIAL_PRIORITY, new NearestAttackableTargetGoal<>(this, Player.class, true));
        addBehaviourGoals();
        this.goalSelector.addGoal(OPTIONAL_PRIORITY, new FindCityCenterGoal<>(this));

    }

    protected abstract void addBehaviourGoals();

    public static AttributeSupplier.@NotNull Builder createMobAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE)
                .add(Attributes.ATTACK_SPEED, ATTACK_SPEED)
                .add(Attributes.ATTACK_KNOCKBACK, ATTACK_KNOCKBACK)
                .add(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED)
                .add(Attributes.KNOCKBACK_RESISTANCE, KNOCKBACK_RESISTANCE)
                .add(Attributes.JUMP_STRENGTH, JUMP_STRENGTH)
                .add(Attributes.STEP_HEIGHT, STEP_HEIGHT)
                .add(Attributes.SAFE_FALL_DISTANCE, SAFE_FALL_DISTANCE);
    }

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
