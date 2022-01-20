package org.cyclops.integratedrest.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import org.cyclops.cyclopscore.helper.TileHelpers;
import org.cyclops.integrateddynamics.block.BlockProxy;
import org.cyclops.integrateddynamics.core.block.BlockTileGuiCabled;
import org.cyclops.integratedrest.tileentity.TileHttp;

import net.minecraft.block.AbstractBlock.Properties;

/**
 * A block that can listen to HTTP PUTs.
 * @author rubensworks
 */
public class BlockHttp extends BlockTileGuiCabled {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public BlockHttp(Properties properties) {
        super(properties, TileHttp::new);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public void setPlacedBy(World world, BlockPos blockPos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (!world.isClientSide()) {
            TileHelpers.getSafeTile(world, blockPos, TileHttp.class)
                    .ifPresent(tile -> {
                        if (itemStack.hasTag() && itemStack.getTag().contains(BlockProxy.NBT_ID, Constants.NBT.TAG_INT)) {
                            tile.setProxyId(itemStack.getTag().getInt(BlockProxy.NBT_ID));
                        } else {
                            tile.generateNewProxyId();
                        }
                        tile.setChanged();
                    });
        }
        super.setPlacedBy(world, blockPos, state, placer, itemStack);
    }

}
