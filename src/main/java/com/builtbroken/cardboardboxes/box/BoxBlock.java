package com.builtbroken.cardboardboxes.box;

import com.builtbroken.cardboardboxes.Cardboardboxes;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Block for the box
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/28/2015.
 */
public class BoxBlock extends Block implements BlockEntityProvider {
    public static final String STORE_ITEM_TAG = "storedItem";
    public static final String BLOCK_ENTITY_DATA_TAG = "tileData";

    public BoxBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onBlockBreakStart(BlockState state, World level, BlockPos pos, PlayerEntity player) {
        if (!level.isClient) {
            if (level.getBlockEntity(pos) instanceof BoxBlockEntity boxBlockEntity && boxBlockEntity.getStateForPlacement() != null) {
                if (boxBlockEntity.getStateForPlacement() != null && level.setBlockState(pos, boxBlockEntity.getStateForPlacement(), 3)) {
                    NbtCompound compound = boxBlockEntity.getDataForPlacement();
                    if (compound != null) {
                        BlockEntity blockEntity = level.getBlockEntity(pos);
                        if (blockEntity != null) {
                            blockEntity.readNbt(compound);
                        }
                    }
                    if (!player.isCreative()) {
                        ItemStack stack = new ItemStack(this);
                        if (!player.getInventory().insertStack(stack)) {
                            Vec3d p = player.getPos();
                            level.spawnEntity(new ItemEntity(level, p.x, p.y, p.z, stack));
                        }
                    }
                }
            }
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand handIn, BlockHitResult hit) {
        if (level.isClient) {
            return ActionResult.PASS;
        }
        if (player.isSneaking()) {
            ItemStack stack = toItemStack(level, pos);
            if (stack != null) {
                if (player.getInventory().insertStack(stack)) {
                    player.getInventory().markDirty();
                    level.removeBlock(pos, false);
                    return ActionResult.SUCCESS;
                } else {
                    player.sendMessage(new TranslatableText(getTranslationKey() + ".inventoryFull"), true);
                    return ActionResult.PASS;
                }
            } else {
                player.sendMessage(new TranslatableText(getTranslationKey() + ".error.stack.null"), true);
            }
        }
        return ActionResult.PASS;
    }

    public ItemStack toItemStack(BlockView level, BlockPos pos) {
        ItemStack stack = new ItemStack(Cardboardboxes.BOX_BLOCK.get());

        if (level.getBlockEntity(pos) instanceof BoxBlockEntity blockEntity) {
            if (blockEntity.getStateForPlacement() != null) {
                stack.setNbt(new NbtCompound());

                stack.getNbt().putInt(STORE_ITEM_TAG, Block.getRawIdFromState(blockEntity.getStateForPlacement()));
                if (blockEntity.getDataForPlacement() != null) {
                    stack.getNbt().put(BLOCK_ENTITY_DATA_TAG, blockEntity.getDataForPlacement());
                }
            } else {
                System.out.println("Error: block entity does not have an ItemStack");
            }
        }
        return stack;
    }

    @Override
    public ItemStack getPickStack(BlockView level, BlockPos pos, BlockState state) {
        return toItemStack(level, pos);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BoxBlockEntity(pos, state);
    }
}