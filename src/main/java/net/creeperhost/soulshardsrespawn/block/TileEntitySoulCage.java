package net.creeperhost.soulshardsrespawn.block;

import net.creeperhost.soulshardsrespawn.SoulShards;
import net.creeperhost.soulshardsrespawn.api.IShardTier;
import net.creeperhost.soulshardsrespawn.api.SoulShardsAPI;
import net.creeperhost.soulshardsrespawn.core.RegistrarSoulShards;
import net.creeperhost.soulshardsrespawn.core.data.Binding;
import net.creeperhost.soulshardsrespawn.item.ItemSoulShard;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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
                level.neighborChanged(getBlockPos(), getBlockState().getBlock(), getBlockPos());
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
    public void load(CompoundTag tag) {
        super.load(tag);
        this.inventory.deserializeNBT(tag.getCompound("inventory"));
        this.spawnDelay = tag.getInt("spawnDelay");
        this.active = tag.getBoolean("active");
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("inventory", inventory.serializeNBT());
        tag.putInt("spawnDelay", spawnDelay);
        tag.putBoolean("active", active);
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.put("inventory", inventory.serializeNBT());
        tag.putShort("spawnDelay", (short) spawnDelay);
        tag.putBoolean("active", active);
        return tag;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        if (tag == null) return;
        this.inventory.deserializeNBT(tag.getCompound("inventory"));
        this.spawnDelay = tag.getShort("spawnDelay");
        this.active = tag.getBoolean("active");
    }

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
