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
                // ИММУНИТЕТ К ОТБРАСЫВАНИЮ: 1.0 = 100% сопротивление
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
        // Добавляем цель "Держаться вместе" (приоритет ниже, чем у атаки, но выше чем у брождения)
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

    // --- СТАЙНЫЙ ИНСТИНКТ И УРОН ---
    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        if (!this.level().isClientSide() && this.hurtTime == 0) {
            this.triggerAnim("Actions", "hurt");
        }

        // СТАЙНЫЙ ИНСТИНКТ: Если атаковал игрок или другое существо, зовем на помощь
        Entity attacker = source.getEntity();
        if (attacker instanceof LivingEntity livingAttacker && !this.level().isClientSide()) {
            // Ищем всех Невежд в радиусе 30 блоков
            AABB area = this.getBoundingBox().inflate(30.0D);
            List<NevegdaEntity> allies = this.level().getEntitiesOfClass(NevegdaEntity.class, area);
            for (NevegdaEntity ally : allies) {
                // Если у союзника нет цели или он атакует кого-то другого — переключаем на обидчика
                if (ally.getTarget() == null || ally.getTarget() != livingAttacker) {
                    ally.setTarget(livingAttacker);
                }
            }
        }

        return super.hurtServer(level, source, amount);
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
                .triggerableAnim("hurt", RawAnimation.begin().thenPlay("hurt")));
    }

    private boolean isMoving() {
        return this.getDeltaMovement().horizontalDistanceSqr() > 0.00001D;
    }

    // --- КАСТОМНАЯ ЦЕЛЬ: ДЕРЖАТЬСЯ ВМЕСТЕ ---
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
            // Не работаем, если у моба уже есть цель для атаки
            if (this.mob.getTarget() != null) return false;

            // Ищем ближайшего сородича в радиусе 10 блоков
            List<NevegdaEntity> allies = this.mob.level().getEntitiesOfClass(
                    NevegdaEntity.class,
                    this.mob.getBoundingBox().inflate(10.0D),
                    (entity) -> entity != this.mob && entity.isAlive()
            );

            if (allies.isEmpty()) return false;

            // Выбираем первого (или ближайшего)
            this.leader = allies.get(0);
            // Начинаем следовать, только если мы находимся дальше 3 блоков от лидера
            return this.mob.distanceToSqr(this.leader) > 9.0D;
        }

        @Override
        public void start() {
            // Запускаем навигацию к лидеру
        }

        @Override
        public void tick() {
            if (this.leader != null && this.leader.isAlive()) {
                // Постоянно обновляем путь к лидеру
                this.mob.getNavigation().moveTo(this.leader, this.speedModifier);
            }
        }

        @Override
        public boolean canContinueToUse() {
            // Продолжаем, пока есть лидер, он жив, и мы не начали атаку
            return this.leader != null && this.leader.isAlive() 
                    && this.mob.getTarget() == null 
                    && this.mob.distanceToSqr(this.leader) > 4.0D;
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
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
