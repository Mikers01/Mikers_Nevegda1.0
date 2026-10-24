package com.mikers.snuffles.entity.ai;

import com.mikers.snuffles.entity.SnufflesEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumSet;

public class SmartDigGoal extends Goal {
    private final SnufflesEntity mob;
    private int breakProgress;

    public SmartDigGoal(SnufflesEntity mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        // Работаем всегда, когда не спим. Логика внутри tick()
        return !this.mob.getState().equals("sleep");
    }

    @Override
    public void start() {
        this.breakProgress = 0;
    }

    @Override
    public void tick() {
        Level level = this.mob.level();
        
        // Если есть цель - копаем к ней
        if (this.mob.getTarget() != null) {
            // Проверяем, не слишком ли далеко цель (если далеко - пусть идет пешком)
            if (this.mob.distanceToSqr(this.mob.getTarget()) > 4.0D) {
                return; 
            }
            
            BlockPos front = this.mob.blockPosition().relative(this.mob.getDirection());
            BlockPos frontUp = front.above();
            
            // Сначала ломаем верхний блок (чтобы пролезть), потом нижний
            if (isValidBlock(level.getBlockState(frontUp))) {
                tryBreak(frontUp);
            } else if (isValidBlock(level.getBlockState(front))) {
                tryBreak(front);
            }
        } 
        // Если цели нет и день - копаем вниз
        else {
            long time = level.getGameTime() % 24000;
            if (time < 12000 && level.canSeeSky(this.mob.blockPosition())) {
                BlockPos below = this.mob.blockPosition().below();
                if (isValidBlock(level.getBlockState(below))) {
                    tryBreak(below);
                }
            }
        }
    }

    private void tryBreak(BlockPos pos) {
        this.breakProgress++;
        if (this.breakProgress >= 10) {
            this.mob.level().destroyBlock(pos, true);
            this.breakProgress = 0;
        }
    }

    private boolean isValidBlock(BlockState state) {
        if (state.isAir()) return false;
        return state.is(BlockTags.BASE_STONE_OVERWORLD) || state.is(BlockTags.DIRT) ||
               state.is(BlockTags.LOGS) || state.is(BlockTags.SAND) ||
               state.is(Blocks.GRAVEL) || state.is(Blocks.CLAY) ||
               state.is(Blocks.COAL_ORE) || state.is(Blocks.IRON_ORE) || state.is(Blocks.DIAMOND_ORE);
    }
}
