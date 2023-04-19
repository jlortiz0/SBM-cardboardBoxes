package com.builtbroken.cardboardboxes.box;

import com.builtbroken.cardboardboxes.Cardboardboxes;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

/**
 * TileEntity for the box
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 7/28/2015.
 */
public class BoxBlockEntity extends BlockEntity {
    private BlockState placementState;
    private NbtCompound placementData;

    public BoxBlockEntity(BlockPos pos, BlockState state) {
        super(Cardboardboxes.BOX_BLOCK_ENTITY_TYPE.get(), pos, state);
    }


    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        if (tag.contains("storedTile")) {
            setStateForPlacement(Block.getStateFromRawId(tag.getInt("storedTile")));
            if (tag.contains("tileData")) {
                setDataForPlacement(tag.getCompound("tileData"));
            }
        }
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        if (getStateForPlacement() != null) {
            tag.putInt("storedTile", Block.getRawIdFromState(placementState));
            if (getDataForPlacement() != null) {
                tag.put("tileData", getDataForPlacement());
            }
        }
        super.writeNbt(tag);
    }

    public BlockState getStateForPlacement() {
        return placementState;
    }

    public void setStateForPlacement(BlockState state) {
        this.placementState = state;
    }

    public NbtCompound getDataForPlacement() {
        return placementData;
    }

    public void setDataForPlacement(NbtCompound placementData) {
        this.placementData = placementData;
    }
}