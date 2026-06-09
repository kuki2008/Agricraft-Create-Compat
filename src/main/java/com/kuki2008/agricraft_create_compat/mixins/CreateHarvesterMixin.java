package com.kuki2008.agricraft_create_compat.mixins;

import com.agricraft.agricraft.api.crop.AgriGrowthStage;
import com.mojang.logging.LogUtils;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;

import com.simibubi.create.content.contraptions.actors.harvester.HarvesterMovementBehaviour;
import com.agricraft.agricraft.common.block.entity.CropBlockEntity;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(HarvesterMovementBehaviour.class)
public class CreateHarvesterMixin {
    private static final Logger LOGGER = LogUtils.getLogger();
//    static {
//        LOGGER.info("CREATE HARVESTER MIXIN LOADED");
//    }
    @Inject(
            //Лучше перебдеть, чем недобдеть
            method = "visitNewPosition(Lcom/simibubi/create/content/contraptions/behaviour/MovementContext;Lnet/minecraft/core/BlockPos;)V",
            at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;",
                ordinal = 0
            ),
            cancellable = true
    )
    private void agcraft$harvest(
            MovementContext context,
            BlockPos pos,
            CallbackInfo ci
    ) {
//        LOGGER.info("HARVESTER VISITED: " + pos);
        Level world = context.world;

        if (world.isClientSide()) return;


//        if (world.getBlockEntity(pos) instanceof CropBlockEntity) {
//            LOGGER.info("FOUND AGRICRAFT CROP");
//        }
        if (!(world.getBlockEntity(pos) instanceof CropBlockEntity crop)) return;

        if (!crop.hasPlant()) return;

        if (crop.harvest(stack -> {
//            LOGGER.info("DROP: {}", stack);
            if (!stack.isEmpty()) {
                ((MovementBehaviour)(Object)this)
                        .collectOrDropItem(context, stack);
            }
        }, null)) {
            if (crop.hasWeeds()) {
                crop.setWeedGrowthStage(crop.getWeed().getInitialGrowthStage());}
        } else {
            crop.setGrowthStage(crop.getGrowthStage().getPrevious(crop, world.random));
            if (crop.hasWeeds()) {
                crop.setWeedGrowthStage(crop.getWeedGrowthStage().getPrevious(crop, world.random));}
        }
//        crop.setChanged();
//        world.sendBlockUpdated(pos, crop.getBlockState(), crop.getBlockState(), 3);
    }
}