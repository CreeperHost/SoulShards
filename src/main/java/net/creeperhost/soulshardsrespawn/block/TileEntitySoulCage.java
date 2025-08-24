package net.creeperhost.soulshardsrespawn.block;

import net.creeperhost.soulshardsrespawn.SoulShards;
import net.creeperhost.soulshardsrespawn.api.IShardTier;
import net.creeperhost.soulshardsrespawn.api.SoulShardsAPI;
import net.creeperhost.soulshardsrespawn.core.RegistrarSoulShards;
import net.creeperhost.soulshardsrespawn.core.data.Binding;
import net.creeperhost.soulshardsrespawn.item.ItemSoulShard;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntitySoulCage extends BlockEntity {
    private ItemStackHandler inventory;
    private boolean active = false;
    public int spawnDelay = -1;

    public SoulSpawnerLogic spawnerLogic = new SoulSpawnerLogic(this);
    private Binding binding = null;

    public TileEntitySoulCage(BlockPos blockPos, BlockState blockState) {
        super(RegistrarSoulShards.SOUL_CAGE_TE.get(), blockPos, blockState);
        this.inventory = new SoulCageInventory() {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
                super.onContentsChanged(slot);
            }
        };
    }

    public void tick() {
        updateBinding();
        if (level instanceof ServerLevel) {
            boolean newState = canSpawn();
            if (newState != active) {
                setActive(newState);
                level.neighborChanged(getBlockPos(), getBlockState().getBlock(), null);
            }
            spawnerLogic.serverTick((ServerLevel) level, worldPosition);
        } else {
            spawnerLogic.clientTick(level, worldPosition);
        }
    }

    public boolean isActive() {
        return active && binding != null;
    }

    private boolean canSpawn() {
        BlockState state = getBlockState();
        if (state.getBlock() != RegistrarSoulShards.SOUL_CAGE.get()) return false;

        ItemStack shardStack = inventory.getStackInSlot(0);
        if (shardStack.isEmpty() || !(shardStack.getItem() instanceof ItemSoulShard)) {
            spawnDelay = -1;
            return false;
        }

        if (binding == null || binding.getBoundEntity() == null) return false;

        IShardTier tier = binding.getTier();
        if (tier.getSpawnAmount() == 0) return false;

        if (SoulShards.CONFIG.getBalance().requireOwnerOnline() && !ownerOnline()) return false;

        if (!SoulShardsAPI.isAllowed(binding.getBoundEntity())) return false;

        if (!SoulShards.CONFIG.getBalance().requireRedstoneSignal()) {
            if (state.getValue(BlockSoulCage.POWERED) && tier.checkRedstone()) return false;
        } else if (!state.getValue(BlockSoulCage.POWERED)) return false;

        return !tier.checkPlayer() || level.getNearestPlayer(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), 16, false) != null;
    }

    protected void resetTimer() {
        if (binding == null) spawnDelay = -1;
        else spawnDelay = binding.getTier().getCooldown();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
    }

    public void setActive(boolean active) {
        BlockState state = getBlockState();
        if (!(state.getBlock() instanceof BlockSoulCage)) return;

        level.setBlock(getBlockPos(), state.setValue(BlockSoulCage.ACTIVE, active), 3);
        this.active = active;
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (level != null) {
            ItemStack stack = this.getInventory().getStackInSlot(0);
            Containers.dropContents(level, pos, NonNullList.of(ItemStack.EMPTY, stack));
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.inventory.deserialize(input.childOrEmpty("inventory"));
        this.spawnDelay = input.getIntOr("spawnDelay", -1);
        this.active = input.getBooleanOr("active", false);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        inventory.serialize(output.child("inventory"));
        output.putInt("spawnDelay", spawnDelay);
        output.putBoolean("active", active);
    }

//    @Nullable
//    @Override
//    public ClientboundBlockEntityDataPacket getUpdatePacket() {
//        return ClientboundBlockEntityDataPacket.create(this);
//    }

//    @Override
//    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
//        CompoundTag tag = super.getUpdateTag(provider);
//        tag.put("inventory", inventory.serializeNBT(provider));
//        tag.putShort("spawnDelay", (short) spawnDelay);
//        tag.putBoolean("active", active);
//        return tag;
//    }

//    @Override
//    public void onDataPacket(Connection net, ValueInput valueInput) {
//        super.onDataPacket(net, valueInput);
//    }

//    @Override
//    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
//        CompoundTag tag = pkt.getTag();
//        if (tag == null) return;
//        this.inventory.deserializeNBT(lookupProvider, tag.getCompoundOrEmpty("inventory"));
//        this.spawnDelay = tag.getShortOr("spawnDelay", (short) -1);
//        this.active = tag.getBooleanOr("active", false);
//    }


    public ItemStackHandler getInventory() {
        return inventory;
    }

    private void updateBinding() {
        ItemStack stack = inventory.getStackInSlot(0);
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemSoulShard)) binding = null;
        else binding = ((ItemSoulShard) stack.getItem()).getBinding(stack);
    }

    @Nullable
    public Binding getBinding() {
        return binding;
    }

    public boolean ownerOnline() {
        Binding binding = getBinding();
        //noinspection ConstantConditions
        return binding != null && binding.getOwner() != null && level.getServer().getPlayerList().getPlayer(binding.getOwner()) == null;
    }

    public static class SoulCageInventory extends ItemStackHandler {
        public SoulCageInventory() {
            super(1);
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (!(stack.getItem() instanceof ItemSoulShard)) return stack;

            Binding binding = ((ItemSoulShard) stack.getItem()).getBinding(stack);
            if (binding == null || binding.getBoundEntity() == null || !SoulShardsAPI.isAllowed(binding.getBoundEntity()))
                return stack;

            return super.insertItem(slot, stack, simulate);
        }
    }
}
