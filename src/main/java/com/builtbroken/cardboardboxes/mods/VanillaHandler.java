package com.builtbroken.cardboardboxes.mods;

import com.builtbroken.cardboardboxes.handler.Handler;
import com.builtbroken.cardboardboxes.handler.HandlerManager;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class VanillaHandler{

    public static void register(Configuration cfg) {
        if (cfg.allowSpawners) {
            HandlerManager.INSTANCE.banBlock(Blocks.SPAWNER);

            HandlerManager.INSTANCE.banBlockEntity(BlockEntityType.MOB_SPAWNER);
        }

        //Fix for chests being rotated in opposite direction
        HandlerManager.INSTANCE.registerHandler(Blocks.CHEST, new Handler() {
            @Override
            public void postPlaceBlock(PlayerEntity player, World level, BlockPos pos, Hand hand, Direction direction, float hitX, float hitY, float hitZ, BlockState state, NbtCompound saveData) {
                BlockState blockstate = level.getBlockState(pos);
                if (blockstate.getBlock() == Blocks.CHEST && blockstate.get(ChestBlock.FACING) != player.getHorizontalFacing().getOpposite()) {
                    while (blockstate.get(ChestBlock.FACING) != player.getHorizontalFacing())
                        blockstate = blockstate.cycle(ChestBlock.FACING);
                    level.setBlockState(pos, blockstate, 2);
                }
            }
        });

        //Remove unwanted interaction
        HandlerManager.INSTANCE.banBlock(Blocks.BEACON);
        HandlerManager.INSTANCE.banBlockEntity(BlockEntityType.BEACON);
        HandlerManager.INSTANCE.banBlock(Blocks.PISTON);
        HandlerManager.INSTANCE.banBlock(Blocks.MOVING_PISTON);
        HandlerManager.INSTANCE.banBlock(Blocks.PISTON_HEAD);
        HandlerManager.INSTANCE.banBlock(Blocks.STICKY_PISTON);
        HandlerManager.INSTANCE.banBlockEntity(BlockEntityType.PISTON);
        HandlerManager.INSTANCE.banBlock(Blocks.DAYLIGHT_DETECTOR);
        HandlerManager.INSTANCE.banBlockEntity(BlockEntityType.DAYLIGHT_DETECTOR);
        HandlerManager.INSTANCE.banBlock(Blocks.ENDER_CHEST);
        HandlerManager.INSTANCE.banBlockEntity(BlockEntityType.ENDER_CHEST);
        HandlerManager.INSTANCE.banBlock(Blocks.COMPARATOR);
        HandlerManager.INSTANCE.banBlockEntity(BlockEntityType.COMPARATOR);
        HandlerManager.INSTANCE.banBlock(Blocks.COMMAND_BLOCK);
        HandlerManager.INSTANCE.banBlockEntity(BlockEntityType.COMMAND_BLOCK);
        HandlerManager.INSTANCE.banBlock(Blocks.END_PORTAL);
        HandlerManager.INSTANCE.banBlock(Blocks.END_PORTAL_FRAME);
        HandlerManager.INSTANCE.banBlockEntity(BlockEntityType.END_PORTAL);
        HandlerManager.INSTANCE.banBlock(Blocks.NOTE_BLOCK);
        HandlerManager.INSTANCE.banBlock(Blocks.ENCHANTING_TABLE);
        HandlerManager.INSTANCE.banBlockEntity(BlockEntityType.ENCHANTING_TABLE);
        HandlerManager.INSTANCE.banBlockEntity(BlockEntityType.SIGN);
        HandlerManager.INSTANCE.banBlockEntity(BlockEntityType.SIGN);
        HandlerManager.INSTANCE.banBlock(Blocks.SKELETON_SKULL);
        HandlerManager.INSTANCE.banBlock(Blocks.SKELETON_WALL_SKULL);
        HandlerManager.INSTANCE.banBlock(Blocks.WITHER_SKELETON_SKULL);
        HandlerManager.INSTANCE.banBlock(Blocks.WITHER_SKELETON_WALL_SKULL);
        HandlerManager.INSTANCE.banBlock(Blocks.CREEPER_HEAD);
        HandlerManager.INSTANCE.banBlock(Blocks.CREEPER_WALL_HEAD);
        HandlerManager.INSTANCE.banBlock(Blocks.DRAGON_HEAD);
        HandlerManager.INSTANCE.banBlock(Blocks.DRAGON_WALL_HEAD);
        HandlerManager.INSTANCE.banBlock(Blocks.PLAYER_HEAD);
        HandlerManager.INSTANCE.banBlock(Blocks.PLAYER_WALL_HEAD);
        HandlerManager.INSTANCE.banBlock(Blocks.ZOMBIE_HEAD);
        HandlerManager.INSTANCE.banBlock(Blocks.ZOMBIE_WALL_HEAD);
        HandlerManager.INSTANCE.banBlockEntity(BlockEntityType.SKULL);
        HandlerManager.INSTANCE.banBlock(Blocks.CAULDRON);
        HandlerManager.INSTANCE.banBlock(Blocks.FLOWER_POT);

        //Black listed because (A can already be moved, B duplicaiton issue
        HandlerManager.INSTANCE.banBlock(Blocks.SHULKER_BOX);
        HandlerManager.INSTANCE.banBlock(Blocks.WHITE_SHULKER_BOX);
        HandlerManager.INSTANCE.banBlock(Blocks.ORANGE_SHULKER_BOX);
        HandlerManager.INSTANCE.banBlock(Blocks.MAGENTA_SHULKER_BOX);
        HandlerManager.INSTANCE.banBlock(Blocks.LIGHT_BLUE_SHULKER_BOX);
        HandlerManager.INSTANCE.banBlock(Blocks.YELLOW_SHULKER_BOX);
        HandlerManager.INSTANCE.banBlock(Blocks.LIME_SHULKER_BOX);
        HandlerManager.INSTANCE.banBlock(Blocks.LIGHT_GRAY_SHULKER_BOX);
        HandlerManager.INSTANCE.banBlock(Blocks.PINK_SHULKER_BOX);
        HandlerManager.INSTANCE.banBlock(Blocks.GRAY_SHULKER_BOX);
        HandlerManager.INSTANCE.banBlock(Blocks.CYAN_SHULKER_BOX);
        HandlerManager.INSTANCE.banBlock(Blocks.PURPLE_SHULKER_BOX);
        HandlerManager.INSTANCE.banBlock(Blocks.BLUE_SHULKER_BOX);
        HandlerManager.INSTANCE.banBlock(Blocks.BROWN_SHULKER_BOX);
        HandlerManager.INSTANCE.banBlock(Blocks.GREEN_SHULKER_BOX);
        HandlerManager.INSTANCE.banBlock(Blocks.RED_SHULKER_BOX);
        HandlerManager.INSTANCE.banBlock(Blocks.BLACK_SHULKER_BOX);
        HandlerManager.INSTANCE.banBlockEntity(BlockEntityType.SHULKER_BOX);
    }
}
