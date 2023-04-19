package com.builtbroken.cardboardboxes.box;

import static com.builtbroken.cardboardboxes.box.BoxBlock.BLOCK_ENTITY_DATA_TAG;
import static com.builtbroken.cardboardboxes.box.BoxBlock.STORE_ITEM_TAG;

import java.util.List;

import com.builtbroken.cardboardboxes.Cardboardboxes;
import com.builtbroken.cardboardboxes.handler.CanPickUpResult;
import com.builtbroken.cardboardboxes.handler.Handler;
import com.builtbroken.cardboardboxes.handler.HandlerManager;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * ItemBlock for the box
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/28/2015.
 */
public class BoxBlockItem extends BlockItem {
    public BoxBlockItem(Block block) {
        super(block, new Item.Settings().group(ItemGroup.DECORATIONS));
    }

    //TODO add property to change render if contains item
    //TODO add property to change render based on content (e.g. show chest on box)
    //TODO add property to change render color, label, etc

    @Override
    public ActionResult useOn(ItemUsageContext context) {
        //Run all logic server side
        World level = context.getWorld();
        if (level.isClient) {
            return ActionResult.SUCCESS;
        }

        PlayerEntity player = context.getPlayer();
        Hand hand = context.getHand();
        final ItemStack heldItemStack = context.getStack();
        if (!heldItemStack.isEmpty()) {
            final BlockState storeBlock = getStoredBlock(heldItemStack);
            if (storeBlock.getBlock() != Blocks.AIR) {
                return tryToPlaceBlock(new ItemPlacementContext(context));
            } else {
                return tryToPickupBlock(player, level, context.getBlockPos(), hand, context.getSide());
            }
        }
        return ActionResult.FAIL;
    }

    protected ActionResult tryToPickupBlock(PlayerEntity player, World level, BlockPos pos, Hand hand, Direction direction) {
        //Check that we can pick up block
        CanPickUpResult result = HandlerManager.INSTANCE.canPickUp(level, pos);

        if (result == CanPickUpResult.CAN_PICK_UP) {
            //Get tile, ignore anything without a block entity
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity != null) {
                //Get stack
                final BlockState state = level.getBlockState(pos);
                //Copy block entity data
                NbtCompound tag = blockEntity.createNbtWithId();

                //Remove block entity
                level.removeBlockEntity(pos);

                //Replace block with our block
                level.setBlockState(pos, Cardboardboxes.BOX_BLOCK.get().defaultBlockState(), 2);

                //Get our block entity
                if (level.getBlockEntity(pos) instanceof BoxBlockEntity boxBlockEntity) {
                    //Move data into block entity
                    boxBlockEntity.setStateForPlacement(state);
                    boxBlockEntity.setDataForPlacement(tag);

                    //Consume item
                    player.getStackInHand(hand).decrement(1);

                    //Done
                    return ActionResult.SUCCESS;
                }
            } else {
                player.sendMessage(new TranslatableText(getTranslationKey() + ".noData"), true);
            }
        } else if (result == CanPickUpResult.BANNED_BLOCK_ENTITY) {
            player.sendMessage(new TranslatableText(getTranslationKey() + ".banned.tile"), true);
        } else if (result == CanPickUpResult.BANNED_BLOCK) {
            player.sendMessage(new TranslatableText(getTranslationKey() + ".banned.block"), true);
        } else {
            player.sendMessage(new TranslatableText(getTranslationKey() + ".noData"), true);
        }
        return ActionResult.SUCCESS;
    }

    protected ActionResult tryToPlaceBlock(ItemPlacementContext context) {
        BlockPos pos = context.getBlockPos();
        Hand hand = context.getHand();
        //Move up one if not replaceable
        float hitX = (float) context.getHitPos().getX(), hitY = (float) context.getHitPos().getY(), hitZ = (float) context.getHitPos().getZ();
        if (!context.canPlace()) {
            pos = pos.offset(context.getSide());
        }

        final ItemStack heldItemStack = context.getStack();
        final BlockState storedBlockState = getStoredBlock(heldItemStack);
        final NbtCompound storedBlockEntityData = getStoredBlockEntityData(heldItemStack);
        //Check if we can place the given block
        if (storedBlockState != null && context.getPlayer().canPlaceOn(pos, context.getSide(), heldItemStack) && context.getWorld().getBlockState(pos).getMaterial().isReplaceable()) {
            Handler handler = HandlerManager.INSTANCE.getHandler(storedBlockState.getBlock());
            BlockState blockstate = storedBlockState.getBlock().getPlacementState(context);
            //Allow handler to control placement
            if (handler != null && handler.placeBlock(context.getPlayer(), context.getWorld(), pos, hand, context.getSide(), hitX, hitY, hitZ, storedBlockState, storedBlockEntityData)
                    //Run normal placement if we don't have a handler or it didn't do anything
                    || place(context, blockstate)) {
                //Get placed block
                blockstate = context.getWorld().getBlockState(pos);

                //Allow handle to do post placement modification (e.g. fix rotation)
                if (handler != null) {
                    handler.postPlaceBlock(context.getPlayer(), context.getWorld(), pos, hand, context.getSide(), hitX, hitY, hitZ, storedBlockState, storedBlockEntityData);
                }

                //Set tile entity data
                if (storedBlockEntityData != null) {
                    BlockEntity blockEntity = context.getWorld().getBlockEntity(pos);
                    if (blockEntity != null) {
                        if (handler != null) {
                            handler.loadData(blockEntity, storedBlockEntityData);
                        } else {
                            blockEntity.readNbt(storedBlockEntityData);
                        }
                    }
                }


                //Place audio
                BlockSoundGroup soundtype = blockstate.getBlock().getSoundGroup(blockstate);
                context.getWorld().playSound(null, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);

                //Consume item
                heldItemStack.decrement(1);

                //Return empty box
                if (!context.getPlayer().isCreative() && !context.getPlayer().getInventory().insertStack(new ItemStack(Cardboardboxes.BOX_BLOCK.get()))) {
                    Vec3d p = context.getPlayer().getPos();
                    context.getWorld().spawnEntity(new ItemEntity(context.getWorld(), p.x, p.y, p.z, new ItemStack(Cardboardboxes.BOX_BLOCK.get())));
                }
            }

            return ActionResult.SUCCESS;
        } else {
            return ActionResult.FAIL;
        }
    }

//    @Override
//    public int getItemStackLimit(ItemStack stack) {
//        return stack.hasNbt() ? 1 : 64;
//    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (stack.hasNbt() && stack.getNbt().contains(STORE_ITEM_TAG)) {
            BlockState state = Block.getStateFromRawId(stack.getNbt().getInt(STORE_ITEM_TAG));
            tooltip.add(new TranslatableText(state.getBlock().getTranslationKey()));
        }
    }

    public BlockState getStoredBlock(ItemStack stack) {
        return stack.hasNbt() && stack.getNbt().contains(STORE_ITEM_TAG) ? Block.getStateFromRawId(stack.getNbt().getInt(STORE_ITEM_TAG)) : Blocks.AIR.getDefaultState();
    }

    public NbtCompound getStoredBlockEntityData(ItemStack stack) {
        return stack.hasNbt() && stack.getNbt().contains(BLOCK_ENTITY_DATA_TAG) ? stack.getNbt().getCompound(BLOCK_ENTITY_DATA_TAG) : null;
    }
}