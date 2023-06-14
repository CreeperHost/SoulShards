package net.creeperhost.soulshardsrespawn.block;

import net.creeperhost.soulshardsrespawn.core.data.Binding;
import net.creeperhost.soulshardsrespawn.core.data.Tier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;

public class BlockSoulCage extends BaseEntityBlock
{
    public static final Property<Boolean> POWERED = BooleanProperty.create("powered");
    public static final Property<Boolean> ACTIVE = BooleanProperty.create("active");

    public BlockSoulCage()
    {
        super(Properties.of().mapColor(MapColor.METAL).strength(3.0F).noOcclusion());
        registerDefaultState(getStateDefinition().any().setValue(POWERED, false).setValue(ACTIVE, false));
    }

    @Override
    public RenderShape getRenderShape(BlockState p_49232_)
    {
        return RenderShape.MODEL;
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
    {
        return (level1, blockPos, blockState, t) ->
        {
            if (t instanceof TileEntitySoulCage tile)
            {
                tile.tick();
            }
        };
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        if (!player.isSteppingCarefully()) return InteractionResult.PASS;

        TileEntitySoulCage cage = (TileEntitySoulCage) world.getBlockEntity(pos);
        if (cage == null) return InteractionResult.PASS;

        ItemStack stack = cage.getInventory().extractItem(0, 1, false);
        if (stack.isEmpty()) return InteractionResult.PASS;

        ItemHandlerHelper.giveItemToPlayer(player, stack);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState state2, boolean someBool)
    {
        handleRedstoneChange(world, state, pos);
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block neighbor, BlockPos neighborPos, boolean someBool)
    {
        handleRedstoneChange(world, state, pos);
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random)
    {
        if (state.getValue(POWERED) && !world.hasNeighborSignal(pos))
        {
            world.setBlock(pos, state.setValue(POWERED, false), 3);
        }
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState blockState2, boolean someBool)
    {
        if (state.getBlock() != blockState2.getBlock())
        {
            TileEntitySoulCage cage = (TileEntitySoulCage) world.getBlockEntity(pos);
            if (cage != null)
            {
                ItemStack stack = cage.getInventory().getStackInSlot(0);
                Containers.dropContents(world, pos, NonNullList.of(ItemStack.EMPTY, stack));
            }
        }
        super.onRemove(state, world, pos, blockState2, someBool);
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, @Nullable Direction side)
    {
        return true;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState p_149740_1_)
    {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level world, BlockPos pos)
    {
        TileEntitySoulCage cage = (TileEntitySoulCage) world.getBlockEntity(pos);
        if (cage == null) return 0;

        Binding binding = cage.getBinding();
        if (binding == null) return 0;

        return (int) (((double) binding.getTier().getIndex() / ((double) Tier.INDEXED.size() - 1)) * 15D);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(POWERED, ACTIVE);
    }

    private void handleRedstoneChange(Level world, BlockState state, BlockPos pos)
    {
        boolean powered = world.hasNeighborSignal(pos);
        if (state.getValue(POWERED) && !powered) world.setBlock(pos, state.setValue(POWERED, false), 2);
        else if (!state.getValue(POWERED) && powered) world.setBlock(pos, state.setValue(POWERED, true), 2);
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState)
    {
        return new TileEntitySoulCage(blockPos, blockState);
    }
}
