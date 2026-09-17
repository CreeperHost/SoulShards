package net.creeperhost.soulshardsrespawn.block;

import net.creeperhost.soulshardsrespawn.SoulShards;
import net.creeperhost.soulshardsrespawn.api.IShardTier;
import net.creeperhost.soulshardsrespawn.api.SoulShardsAPI;
import net.creeperhost.soulshardsrespawn.core.RegistrarSoulShards;
import net.creeperhost.soulshardsrespawn.core.data.Binding;
import net.creeperhost.soulshardsrespawn.item.ItemSoulShard;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

import javax.annotation.Nullable;

public class TileEntitySoulCage extends BlockEntity {
    private final SoulCageInventory inventory;
    private boolean active = false;
    public int spawnDelay = -1;

    public SoulSpawnerLogic spawnerLogic = new SoulSpawnerLogic(this);
    private Binding binding = null;

    public TileEntitySoulCage(BlockPos blockPos, BlockState blockState) {
        super(RegistrarSoulShards.SOUL_CAGE_TE.get(), blockPos, blockState);
        this.inventory = new SoulCageInventory() {
            @Override
            protected void onContentsChanged(int slot, ItemStack previousContents) {
                updateBinding();
                setChanged();
                syncToClient();
                super.onContentsChanged(slot, previousContents);
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

        if (!SoulShardsAPI.isAllowed(level, binding.getBoundEntity())) return false;

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
        updateBinding();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        inventory.serialize(output.child("inventory"));
        output.putInt("spawnDelay", spawnDelay);
        output.putBoolean("active", active);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveCustomOnly(registries);
    }

    private void syncToClient() {
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.getChunkSource().blockChanged(worldPosition);
        }
    }

    public SoulCageInventory getInventory() {
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
        return binding != null && binding.getOwner() != null && level.getServer().getPlayerList().getPlayer(binding.getOwner()) != null;
    }

    public static class SoulCageInventory extends ItemStacksResourceHandler {
        public SoulCageInventory() {
            super(1);
        }

        @Override
        public boolean isValid(int slot, ItemResource resource) {
            if (!(resource.getItem() instanceof ItemSoulShard shard)) return false;
            Binding binding = shard.getBinding(resource.toStack());
            return binding != null && binding.getBoundEntity() != null;
        }

        @Override
        protected int getCapacity(int slot, ItemResource resource) {
            return 1;
        }

        public ItemStack getStackInSlot(int slot) {
            return getResource(slot).toStack(getAmountAsInt(slot));
        }

        // Keep the existing save and update-packet format so installed shards survive upgrades.
        @Override
        public void serialize(ValueOutput output) {
            var items = output.list("Items", ItemStackWithSlot.CODEC);
            ItemStack stack = getStackInSlot(0);
            if (!stack.isEmpty()) items.add(new ItemStackWithSlot(0, stack));
            output.putInt("Size", 1);
        }

        @Override
        public void deserialize(ValueInput input) {
            NonNullList<ItemStack> loaded = NonNullList.withSize(1, ItemStack.EMPTY);
            input.listOrEmpty("Items", ItemStackWithSlot.CODEC).forEach(slot -> {
                if (slot.isValidInContainer(1)) loaded.set(slot.slot(), slot.stack());
            });
            setStacks(loaded);
        }
    }
}
