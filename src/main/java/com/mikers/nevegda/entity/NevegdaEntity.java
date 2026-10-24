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

    private int currentHurtVariant = 0;
    private int lastHurtTime = 0;
    private boolean attackTriggered = false;
    private int attackCounter = 0;
    // Тики с момента последней атаки — чтобы sprinting держался ещё немного после удара
    private int ticksSinceLastAttack = 100;

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
        // Контроллер 1: движение и покой
        controllers.add(new AnimationController<>("main", 5, state -> {
            if (this.isMoving()) {
                // Если моб преследует цель или недавно атаковал — sprinting
                if (this.isChasingTarget() || this.ticksSinceLastAttack < 40) {
                    return state.setAndContinue(RawAnimation.begin().thenLoop("sprinting"));
                }
                // Иначе — обычный running
                return state.setAndContinue(RawAnimation.begin().thenLoop("running"));
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }));

        // Контроллер 2: получение урона (hurt1-3)
        controllers.add(new AnimationController<>("action", 0, state -> {
            if (this.hurtTime > 0 && this.hurtTime > lastHurtTime) {
                if (currentHurtVariant == 0) {
                    currentHurtVariant = this.random.nextInt(3) + 1;
                }
                return state.setAndContinue(RawAnimation.begin().thenPlay("hurt" + currentHurtVariant));
            }
            if (this.hurtTime == 0) {
                currentHurtVariant = 0;
            }
            return state.setAndContinue(RawAnimation.begin());
        }));

        // Контроллер 3: атака (attack1-3)
        controllers.add(new AnimationController<>("attack", 0, state -> state.setAndContinue(RawAnimation.begin()))
                .triggerableAnim("attack1_a", RawAnimation.begin().thenPlay("attack1"))
                .triggerableAnim("attack1_b", RawAnimation.begin().thenPlay("attack1"))
                .triggerableAnim("attack2_a", RawAnimation.begin().thenPlay("attack2"))
                .triggerableAnim("attack2_b", RawAnimation.begin().thenPlay("attack2"))
                .triggerableAnim("attack3_a", RawAnimation.begin().thenPlay("attack3"))
                .triggerableAnim("attack3_b", RawAnimation.begin().thenPlay("attack3")));
    }

    @Override
    public void tick() {
        super.tick();

        // Тики с последней атаки
        if (this.ticksSinceLastAttack < 1000) {
            this.ticksSinceLastAttack++;
        }

        if (this.hurtTime > 0) {
            lastHurtTime = this.hurtTime;
        } else {
            lastHurtTime = 0;
        }

        // Запуск атаки
        if (!this.level().isClientSide() && this.swinging) {
            if (!attackTriggered) {
                int variant = this.random.nextInt(3) + 1;
                String suffix = (attackCounter % 2 == 0) ? "_a" : "_b";
                this.triggerAnim("attack", "attack" + variant + suffix);
                attackCounter++;
                attackTriggered = true;
                this.ticksSinceLastAttack = 0; // Сбрасываем таймер после удара
            }
        } else {
            attackTriggered = false;
        }
    }

    /**
     * Проверяет, преследует ли моб цель (или недавно её видел).
     * Если да — используется sprinting вместо running.
     */
    private boolean isChasingTarget() {
        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }
        // Преследует только если цель дальше 2 блоков (вблизи уже идёт атака)
        double distanceSq = this.distanceToSqr(target);
        return distanceSq > 4.0D;
    }

    private boolean isMoving() {
        return this.getDeltaMovement().horizontalDistanceSqr() > 0.001D;
    }

    private boolean isMovingFast() {
        return this.getDeltaMovement().horizontalDistanceSqr() > 0.05D;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
