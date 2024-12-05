package com.starfish_studios.seasons_greetings.mixin;

import com.starfish_studios.seasons_greetings.entity.GingerbreadMan;
import com.starfish_studios.seasons_greetings.registry.SGEntityType;
import com.starfish_studios.seasons_greetings.registry.SGItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFurnaceBlockEntity.class)
public class AbstractFurnaceBlockEntityMixin {
    @Unique
    private static Vec3 rotateVec(Vec3 offset, Direction facing){
        return switch (facing) {
            case DOWN -> offset.xRot((float) (Math.PI / 2F));
            case UP -> offset.xRot(-(float) (Math.PI / 2F));
            case NORTH -> offset;
            case SOUTH -> offset.yRot((float) (Math.PI));
            case WEST -> offset.yRot((float) (Math.PI / 2F));
            case EAST -> offset.yRot(-(float) (Math.PI / 2F));
        };
    }

    @Inject(method = "serverTick", at = @At("HEAD"))
    private static void sg$serverTick(Level level, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity blockEntity, CallbackInfo ci) {
        ServerLevel serverLevel = (ServerLevel) level;
        ItemStack itemStack = blockEntity.getItem(2);
        Item item = itemStack.getItem();
        GingerbreadMan gingerbreadMan = SGEntityType.GINGERBREAD_MAN.create(level);

        Direction facing = state.getValue(FurnaceBlock.FACING);
        Vec3 spawnVec = rotateVec(new Vec3(0, -0.5F, -1F), facing);

        if (item == SGItems.GINGERBREAD_COOKIE && state.hasProperty(FurnaceBlock.FACING)) {
            itemStack.shrink(1);
            assert gingerbreadMan != null;
            gingerbreadMan.setPos(spawnVec.add(pos.getCenter()));
            gingerbreadMan.setDeltaMovement(spawnVec.scale(0.25F).add(0.2, 0.2, 0));
            gingerbreadMan.cantCatchMe(true);
            level.addFreshEntity(gingerbreadMan);
            
            level.playSound(null, pos, SoundEvents.EVOKER_CAST_SPELL, gingerbreadMan.getSoundSource(), 1.0F, 1.0F);
            for(int j = 0; j < 10; ++j) {
                double g = level.random.nextGaussian() * 0.2;
                double h = level.random.nextGaussian() * 0.1;
                double i = level.random.nextGaussian() * 0.2;

                if (!level.isClientSide) {
                    serverLevel.sendParticles(ParticleTypes.POOF, gingerbreadMan.getX(), gingerbreadMan.getY(), gingerbreadMan.getZ(), 1, g, h, i, 0.0D);
                }

            }

        }
    }
}