package net.creeperhost.soulshardsrespawn.block;

import net.creeperhost.soulshardsrespawn.SoulShards;
import net.creeperhost.soulshardsrespawn.api.CageSpawnEvent;
import net.creeperhost.soulshardsrespawn.api.IShardTier;
import net.creeperhost.soulshardsrespawn.core.RegistrarSoulShards;
import net.creeperhost.soulshardsrespawn.core.data.Binding;
import net.creeperhost.soulshardsrespawn.item.ItemSoulShard;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TileEntitySoulCage extends BlockEntity {
    private ItemStackHandler inventory;
    private Entity renderEntity = null;
    private int maxSpawnDelay = 200;
    private int spawnDelay;
    private boolean active = false;

    public float rotation = 0;
    public float lastRotation = 0;

    public TileEntitySoulCage(BlockPos blockPos, BlockState blockState) {
        super(RegistrarSoulShards.SOUL_CAGE_TE, blockPos, blockState);
        this.inventory = new SoulCageInventory() {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
                super.onContentsChanged(slot);
            }
        };
    }

    public void tick() {
        if (level.isClientSide) {
            if (spawnDelay > 0) spawnDelay--;
            float speed = 1F - (spawnDelay / (float) maxSpawnDelay);
            lastRotation = rotation;
            if (active){
                rotation = (rotation + (speed * 5F)) % 360.0F;
            }
            return;
        }

        InteractionResultHolder<Binding> result = canSpawn();
        if (result.getResult() != InteractionResult.SUCCESS) {
            if (active) {
                setState(false);
                level.neighborChanged(getBlockPos(), getBlockState().getBlock(), getBlockPos());
            }
            return;
        }

        if (!active) {
            setState(true);
            level.neighborChanged(getBlockPos(), getBlockState().getBlock(), getBlockPos());
        }
        spawnDelay--;
        if (spawnDelay <= 0) {
            spawnEntities();
            spawnDelay = maxSpawnDelay = Math.max(result.getObject().getTier().getCooldown(), 1);
            level.sendBlockUpdated(getBlockPos(), level.getBlockState(worldPosition), level.getBlockState(worldPosition), 3);
            level.levelEvent(2004, worldPosition, 0); //Spawn Particles
        }
    }

    private void spawnEntities() {
        Binding binding = getBinding();
        if (binding == null || binding.getBoundEntity() == null) return;

        EntityType<?> entityEntry = ForgeRegistries.ENTITIES.getValue(binding.getBoundEntity());
        if (entityEntry == null) return;

        IShardTier tier = binding.getTier();
        for (int i = 0; i < tier.getSpawnAmount(); i++) {
            for (int attempts = 0; attempts < 5; attempts++) {
                double x = getBlockPos().getX() + (level.random.nextDouble() - level.random.nextDouble()) * 4.0D + 0.5D;
                double y = getBlockPos().getY() + level.random.nextInt(3);
                double z = getBlockPos().getZ() + (level.random.nextDouble() - level.random.nextDouble()) * 4.0D + 0.5D;
                BlockPos spawnAt = new BlockPos(x, y, z);

                if (spawnAt.equals(getBlockPos())) spawnAt = new BlockPos(x, y + 1, z);

                LivingEntity entityLiving = (LivingEntity) entityEntry.create(level);
                if (entityLiving == null) continue;

                if (binding.getTier().checkLight() && !canSpawnInLight(entityLiving, spawnAt)) continue;

                entityLiving.moveTo(spawnAt, level.random.nextFloat() * 360F, 0F);
                entityLiving.getPersistentData().putBoolean("cageBorn", true);

                if (entityLiving.isAlive() && !hasReachedSpawnCap(entityLiving) && level.noCollision(entityLiving)) {
                    if (!SoulShards.CONFIG.getBalance().allowBossSpawns() && !entityLiving.canChangeDimensions())
                        continue;

                    CageSpawnEvent event = new CageSpawnEvent(binding, inventory.getStackInSlot(0), entityLiving);
                    if (MinecraftForge.EVENT_BUS.post(event)) continue;

                    level.addFreshEntity(entityLiving);
                    if (entityLiving instanceof Mob)
                        ((Mob) entityLiving).finalizeSpawn((ServerLevelAccessor) level, level.getCurrentDifficultyAt(spawnAt), MobSpawnType.SPAWNER, null, null);
                    break;
                }
            }
        }
    }

    private InteractionResultHolder<Binding> canSpawn() {
        BlockState state = getBlockState();
        if (state.getBlock() != RegistrarSoulShards.SOUL_CAGE)
            return new InteractionResultHolder<>(InteractionResult.FAIL, null);

        ItemStack shardStack = inventory.getStackInSlot(0);
        if (shardStack.isEmpty() || !(shardStack.getItem() instanceof ItemSoulShard))
            return new InteractionResultHolder<>(InteractionResult.FAIL, null);

        Binding binding = getBinding();
        if (binding == null || binding.getBoundEntity() == null)
            return new InteractionResultHolder<>(InteractionResult.FAIL, binding);

        IShardTier tier = binding.getTier();

        if (tier.getSpawnAmount() == 0) return new InteractionResultHolder<>(InteractionResult.FAIL, binding);

        if (SoulShards.CONFIG.getBalance().requireOwnerOnline() && !ownerOnline())
            return new InteractionResultHolder<>(InteractionResult.FAIL, binding);

        if (!SoulShards.CONFIG.getEntityList().isEnabled(binding.getBoundEntity()))
            return new InteractionResultHolder<>(InteractionResult.FAIL, binding);

        if (!SoulShards.CONFIG.getBalance().requireRedstoneSignal()) {
            if (state.getValue(BlockSoulCage.POWERED) && tier.checkRedstone())
                return new InteractionResultHolder<>(InteractionResult.FAIL, binding);
        } else if (!state.getValue(BlockSoulCage.POWERED))
            return new InteractionResultHolder<>(InteractionResult.FAIL, binding);

        if (tier.checkPlayer() && level.getNearestPlayer(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), 16, false) == null)
            return new InteractionResultHolder<>(InteractionResult.FAIL, binding);

        return new InteractionResultHolder<>(InteractionResult.SUCCESS, binding);
    }

    private boolean canSpawnInLight(LivingEntity entityLiving, BlockPos pos) {
        boolean light = level.getLightEngine().getRawBrightness(getBlockPos(), 15) <= 8;
        return !(entityLiving instanceof Mob) || light;
    }

    private boolean hasReachedSpawnCap(LivingEntity living) {
        AABB box = new AABB(getBlockPos().getX() - 16, getBlockPos().getY() - 16, getBlockPos().getZ() - 16, getBlockPos().getX() + 16, getBlockPos().getY() + 16, getBlockPos().getZ() + 16);

        int mobCount = level.getEntitiesOfClass(living.getClass(), box, e -> e != null && e.getPersistentData().getBoolean("cageBorn")).size();
        return mobCount >= SoulShards.CONFIG.getBalance().getSpawnCap();
    }

    public void setState(boolean active) {
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
        this.maxSpawnDelay = tag.getInt("maxSpawnDelay");
        this.active = tag.getBoolean("active");
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        tag.put("inventory", inventory.serializeNBT());
        tag.putInt("spawnDelay", spawnDelay);
        tag.putInt("maxSpawnDelay", maxSpawnDelay);
        tag.putBoolean("active", active);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @NotNull Direction side) {
        if (cap != CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return LazyOptional.empty();

        return LazyOptional.of(() -> inventory).cast();
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        return super.getCapability(cap);
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    @Nullable
    public Binding getBinding() {
        ItemStack stack = inventory.getStackInSlot(0);
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemSoulShard)) return null;

        return ((ItemSoulShard) stack.getItem()).getBinding(stack);
    }

    public boolean ownerOnline() {
        Binding binding = getBinding();
        //noinspection ConstantConditions
        return binding != null && binding.getOwner() != null && level.getServer().getPlayerList().getPlayer(binding.getOwner()) == null;
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag compound = super.getUpdateTag();
        saveAdditional(compound);
        return compound;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        if (pkt.getTag() != null) load(pkt.getTag());
    }

    @Nullable
    @OnlyIn(Dist.CLIENT)
    public Entity getRenderEntity() {
        Binding binding = getBinding();
        if (binding == null || binding.getBoundEntity() == null) {
            renderEntity = null;
            return null;
        }

        if (renderEntity == null) {
            EntityType<?> entityEntry = ForgeRegistries.ENTITIES.getValue(binding.getBoundEntity());
            if (entityEntry == null) return null;
            renderEntity = entityEntry.create(level);
        }
        return renderEntity;
    }

    public static class SoulCageInventory extends ItemStackHandler {

        public SoulCageInventory() {
            super(1);
        }

        @NotNull
        @Override
        public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if (!(stack.getItem() instanceof ItemSoulShard)) return stack;

            Binding binding = ((ItemSoulShard) stack.getItem()).getBinding(stack);
            if (binding == null || binding.getBoundEntity() == null || !SoulShards.CONFIG.getEntityList().isEnabled(binding.getBoundEntity()))
                return stack;

            return super.insertItem(slot, stack, simulate);
        }
    }
}
