package com.mikers.snuffles.registry;

import com.mikers.snuffles.MikersSnufflesMod;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

@EventBusSubscriber(modid = MikersSnufflesMod.MOD_ID)
public class ModSpawns {

    @SubscribeEvent
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(ModEntities.SNUFFLES.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (type, level, spawnType, pos, random) -> {
                    // Проверка ночи через getGameTime()
                    if (level instanceof ServerLevel serverLevel) {
                        long time = serverLevel.getGameTime() % 24000;
                        if (time < 12000) return false;
                    } else {
                        return false;
                    }

                    BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
                    for (int x = -3; x <= 3; x++) {
                        for (int y = -3; y <= 3; y++) {
                            for (int z = -3; z <= 3; z++) {
                                mutable.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                                if (level.getBlockState(mutable).is(Blocks.GLOW_LICHEN)) {
                                    return true;
                                }
                            }
                        }
                    }
                    return false;
                },
                RegisterSpawnPlacementsEvent.Operation.REPLACE
        );
    }
}
