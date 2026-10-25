package com.mikers.nevegda.entity;

import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.constant.DefaultAnimations;
import com.geckolib.util.GeckoLibUtil;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class NevegdaEntity extends PathfinderMob implements GeoEntity {
    private static final EntityDataAccessor<Integer> VARIANT =
            SynchedEntityData.defineId(NevegdaEntity.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public NevegdaEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.setVariant(this.random.nextInt(5) + 1);
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 500.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3F)
                .add(Attributes.ATTACK_DAMAGE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D); 
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(VARIANT, 1);
    }

    public int getVariant() { return this.entityData.get(VARIANT); }
    public void setVariant(int variant) { this.entityData.set(VARIANT, variant); }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("Variant", getVariant());
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.setVariant(input.getInt("Variant").orElse(1));
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new FollowGroupGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<LivingEntity>(
                this, LivingEntity.class, 10, true, false,
                (entity, level) -> !(entity instanceof NevegdaEntity)
        ));
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        if (!this.level().isClientSide()) {
            this.triggerAnim("Actions", "swipe");
        }
        return super.doHurtTarget(level, target);
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        if (!this.level().isClientSide() && this.hurtTime == 0) {
            this.triggerAnim("Actions", "hurt");
        }

        Entity attacker = source.getEntity();
        if (attacker instanceof LivingEntity livingAttacker && !this.level().isClientSide()) {
            AABB area = this.getBoundingBox().inflate(30.0D);
            List<NevegdaEntity> allies = this.level().getEntitiesOfClass(NevegdaEntity.class, area);
            for (NevegdaEntity ally : allies) {
                if (ally.getTarget() == null || ally.getTarget() != livingAttacker) {
                    ally.setTarget(livingAttacker);
                }
            }
        }
        return super.hurtServer(level, source, amount);
    }

    @Override
    public void die(DamageSource source) {
        if (!this.level().isClientSide()) {
            this.triggerAnim("Actions", "death");
        }
        super.die(source);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("movement", 3, state -> {
            if (this.isAggressive()) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("sprinting"));
            }
            if (this.isMoving()) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("running"));
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }));

        controllers.add(DefaultAnimations.triggerOnlyController()
                .triggerableAnim("swipe", RawAnimation.begin().thenPlay("swipe"))
                .triggerableAnim("hurt", RawAnimation.begin().thenPlay("hurt"))
                .triggerableAnim("death", RawAnimation.begin().thenPlay("death"))
                .triggerableAnim("look_around1", RawAnimation.begin().thenPlay("look_around1"))
                .triggerableAnim("look_around2", RawAnimation.begin().thenPlay("look_around2"))
                .triggerableAnim("look_around3", RawAnimation.begin().thenPlay("look_around3"))
                .triggerableAnim("look_around4", RawAnimation.begin().thenPlay("look_around4"))
                .triggerableAnim("look_around5", RawAnimation.begin().thenPlay("look_around5")));
    }

    private int lookTimer = 0;

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide() && !this.isAggressive() && !this.isMoving() && !this.isDeadOrDying()) {
            lookTimer++;
            if (lookTimer >= 100) {
                int choice = this.random.nextInt(5);
                switch (choice) {
                    case 0: this.triggerAnim("Actions", "look_around1"); break;
                    case 1: this.triggerAnim("Actions", "look_around2"); break;
                    case 2: this.triggerAnim("Actions", "look_around3"); break;
                    case 3: this.triggerAnim("Actions", "look_around4"); break;
                    case 4: this.triggerAnim("Actions", "look_around5"); break;
                }
                lookTimer = 0;
            }
        } else {
            lookTimer = 0;
        }
    }

    private boolean isMoving() {
        return this.getDeltaMovement().horizontalDistanceSqr() > 0.00001D;
    }

    // --- СТАЯ: Держаться на расстоянии 15 блоков ---
    public static class FollowGroupGoal extends Goal {
        private final NevegdaEntity mob;
        private final double speedModifier;
        private NevegdaEntity leader;

        public FollowGroupGoal(NevegdaEntity mob, double speedModifier) {
            this.mob = mob;
            this.speedModifier = speedModifier;
        }

        @Override
        public boolean canUse() {
            if (this.mob.getTarget() != null) return false;
            
            // Ищем союзников в радиусе 30 блоков (чтобы было кого искать)
            List<NevegdaEntity> allies = this.mob.level().getEntitiesOfClass(
                    NevegdaEntity.class,
                    this.mob.getBoundingBox().inflate(30.0D),
                    (entity) -> entity != this.mob && entity.isAlive()
            );
            if (allies.isEmpty()) return false;
            
            // Ищем ближайшего (или первого) союзника
            this.leader = allies.get(0);
            
            // Начинаем двигаться ТОЛЬКО если расстояние больше 15 блоков (225 квадратов)
            return this.mob.distanceToSqr(this.leader) > 225.0D;
        }

        @Override
        public void tick() {
            if (this.leader != null && this.leader.isAlive()) {
                this.mob.getNavigation().moveTo(this.leader, this.speedModifier);
            }
        }

        @Override
        public boolean canContinueToUse() {
            // Продолжаем идти, пока не подойдем на 15 блоков (или цель не появится)
            return this.leader != null && this.leader.isAlive() 
                    && this.mob.getTarget() == null 
                    && this.mob.distanceToSqr(this.leader) > 225.0D;
        }

        @Override
        public void stop() {
            this.leader = null;
            this.mob.getNavigation().stop();
        }
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public boolean isPersistenceRequired() {
        return true;
    }

    @Override
protected void tickDeath() {
    if (this.deathTime == 0) {
        this.deathTime = 1;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
