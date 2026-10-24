package com.mikers.nevegda.entity;

import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.util.GeckoLibUtil;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
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

public class NevegdaEntity extends PathfinderMob implements GeoEntity {
    private static final EntityDataAccessor<Integer> VARIANT =
            SynchedEntityData.defineId(NevegdaEntity.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // Флаг для атаки (сбрасывается через таймер, чтобы анимация успела проиграться)
    private boolean isAttacking = false;
    private int attackTimer = 0;

    public NevegdaEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.setVariant(this.random.nextInt(5) + 1);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 500.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3F)
                .add(Attributes.ATTACK_DAMAGE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
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
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<LivingEntity>(
                this, LivingEntity.class, 10, true, false,
                (entity, level) -> !(entity instanceof NevegdaEntity)
        ));
    }

    // ПЕРЕОПРЕДЕЛЯЕМ МЕТОД АТАКИ (NeoForge 26.2)
    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        // Включаем флаг атаки и ставим таймер на 10 тиков (0.5 сек — длина swipe)
        this.isAttacking = true;
        this.attackTimer = 10;
        return super.doHurtTarget(level, target);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // --- КОНТРОЛЛЕР 1: ДВИЖЕНИЕ (idle, running, sprinting) ---
        // transitionTicks = 3 для плавного перехода между бегом и ходьбой
        controllers.add(new AnimationController<>("movement", 3, state -> {
            if (this.isMoving()) {
                LivingEntity target = this.getTarget();
                if (target != null && target.isAlive()) {
                    return state.setAndContinue(RawAnimation.begin().thenLoop("sprinting"));
                }
                return state.setAndContinue(RawAnimation.begin().thenLoop("running"));
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }));

        // --- КОНТРОЛЛЕР 2: ДЕЙСТВИЯ (hurt, swipe) ---
        // transitionTicks = 0, чтобы анимация начиналась мгновенно
        // Этот контроллер имеет более высокий приоритет (добавлен позже), 
        // поэтому его анимации будут перекрывать движение рук/тела из первого контроллера.
        controllers.add(new AnimationController<>("actions", 0, state -> {
            // 1. УРОН (высший приоритет)
            if (this.hurtTime > 0) {
                return state.setAndContinue(RawAnimation.begin().thenPlay("hurt"));
            }

            // 2. АТАКА
            if (this.isAttacking) {
                return state.setAndContinue(RawAnimation.begin().thenPlay("swipe"));
            }

            // Если ничего не происходит — останавливаем контроллер, 
            // чтобы он не перебивал движение.
            return state.setAndContinue(RawAnimation.begin());
        }));
    }

    @Override
    public void tick() {
        super.tick();

        // Управляем таймером атаки
        if (this.attackTimer > 0) {
            this.attackTimer--;
            if (this.attackTimer <= 0) {
                this.isAttacking = false;
            }
        }
    }

    private boolean isMoving() {
        return this.getDeltaMovement().horizontalDistanceSqr() > 0.0001D;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
