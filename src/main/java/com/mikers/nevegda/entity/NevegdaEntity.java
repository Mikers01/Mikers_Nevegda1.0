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

    // Флаги для управления анимациями
    private boolean isSwinging = false;
    private int hurtTimeTracker = 0;

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

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // Один контроллер, transitionTicks = 2 для плавности, но не для "съедания" анимаций
        controllers.add(new AnimationController<>("main", 2, state -> {
            // 1. HURT (если недавно получили урон)
            if (this.hurtTime > 0) {
                return state.setAndContinue(RawAnimation.begin().thenPlay("hurt"));
            }

            // 2. SWIPE (если недавно ударили)
            if (this.isSwinging) {
                return state.setAndContinue(RawAnimation.begin().thenPlay("swipe"));
            }

            // 3. SPRINTING (если движемся И есть цель)
            if (this.isMoving()) {
                LivingEntity target = this.getTarget();
                if (target != null && target.isAlive()) {
                    return state.setAndContinue(RawAnimation.begin().thenLoop("sprinting"));
                }
                // 4. RUNNING (если движемся без цели)
                return state.setAndContinue(RawAnimation.begin().thenLoop("running"));
            }

            // 5. IDLE
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }));
    }

    @Override
    public void tick() {
        super.tick();

        // Логика для SWIPE: если моб начал махать рукой, включаем флаг
        if (!this.level().isClientSide && this.swinging) {
            this.isSwinging = true;
        } else {
            this.isSwinging = false;
        }
    }

    private boolean isMoving() {
        return this.getDeltaMovement().horizontalDistanceSqr() > 0.001D;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
