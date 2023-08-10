package net.creeperhost.soulshardsrespawn.block;

import net.creeperhost.soulshardsrespawn.SoulShards;
import net.creeperhost.soulshardsrespawn.api.CageSpawnEvent;
import net.creeperhost.soulshardsrespawn.api.IShardTier;
import net.creeperhost.soulshardsrespawn.api.SoulShardsAPI;
import net.creeperhost.soulshardsrespawn.core.RegistrarSoulShards;
import net.creeperhost.soulshardsrespawn.core.data.Binding;
import net.creeperhost.soulshardsrespawn.item.ItemSoulShard;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntitySoulCage extends BlockEntity
{
    private ItemStackHandler inventory;
    private int activeTime;
    private boolean active = false;

    public TileEntitySoulCage(BlockPos blockPos, BlockState blockState)
    {
        super(RegistrarSoulShards.SOUL_CAGE_TE.get(), blockPos, blockState);
        this.inventory = new SoulCageInventory()
        {
            @Override
            protected void onContentsChanged(int slot)
            {
                setChanged();
                super.onContentsChanged(slot);
            }
        };
    }

    public void tick()
    {
        if (level.isClientSide) return;

        InteractionResultHolder<Binding> result = canSpawn();
        if (result.getResult() != InteractionResult.SUCCESS)
        {
            if (active)
            {
                setState(false);
                level.neighborChanged(getBlockPos(), getBlockState().getBlock(), getBlockPos());
            }
            return;
        }

        if (!active)
        {
            setState(true);
            level.neighborChanged(getBlockPos(), getBlockState().getBlock(), getBlockPos());
        }
        activeTime++;

        if (activeTime % result.getObject().getTier().getCooldown() == 0) spawnEntities();
    }

    private void spawnEntities()
    {
        Binding binding = getBinding();
        if (binding == null || binding.getBoundEntity() == null) return;

        EntityType<?> entityEntry = ForgeRegistries.ENTITY_TYPES.getValue(binding.getBoundEntity());
        if (entityEntry == null) return;

        IShardTier tier = binding.getTier();
        for (int i = 0; i < tier.getSpawnAmount(); i++)
        {
            for (int attempts = 0; attempts < 5; attempts++)
            {
                double x = getBlockPos().getX() + (level.random.nextDouble() - level.random.nextDouble()) * 4.0D + 0.5D;
                double y = getBlockPos().getY() + level.random.nextInt(3);
                double z = getBlockPos().getZ() + (level.random.nextDouble() - level.random.nextDouble()) * 4.0D + 0.5D;
                BlockPos spawnAt = new BlockPos((int)x, (int)y, (int)z);

                if (spawnAt.equals(getBlockPos())) spawnAt = new BlockPos((int)x, (int)y + 1, (int)z);

                LivingEntity entityLiving = (LivingEntity) entityEntry.create(level);
                if (entityLiving == null) continue;

                if (binding.getTier().checkLight() && !canSpawnInLight(entityLiving, spawnAt)) continue;

                entityLiving.moveTo(spawnAt, level.random.nextFloat() * 360F, 0F);
                entityLiving.getPersistentData().putBoolean("cageBorn", true);

                if (entityLiving.isAlive() && !hasReachedSpawnCap(entityLiving) && level.noCollision(entityLiving))
                {
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

    private InteractionResultHolder<Binding> canSpawn()
    {
        BlockState state = getBlockState();
        if (state.getBlock() != RegistrarSoulShards.SOUL_CAGE.get())
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

        if (!SoulShardsAPI.isAllowed(binding.getBoundEntity()))
            return new InteractionResultHolder<>(InteractionResult.FAIL, binding);

        if (!SoulShards.CONFIG.getBalance().requireRedstoneSignal())
        {
            if (state.getValue(BlockSoulCage.POWERED) && tier.checkRedstone())
                return new InteractionResultHolder<>(InteractionResult.FAIL, binding);
        }
        else if (!state.getValue(BlockSoulCage.POWERED))
            return new InteractionResultHolder<>(InteractionResult.FAIL, binding);

        if (tier.checkPlayer() && level.getNearestPlayer(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), 16, false) == null)
            return new InteractionResultHolder<>(InteractionResult.FAIL, binding);

        return new InteractionResultHolder<>(InteractionResult.SUCCESS, binding);
    }

    private boolean canSpawnInLight(LivingEntity entityLiving, BlockPos pos)
    {
        boolean light = level.getLightEngine().getRawBrightness(getBlockPos(), 15) <= 8;
        return !(entityLiving instanceof Mob) || light;
    }

    private boolean hasReachedSpawnCap(LivingEntity living)
    {
        AABB box = new AABB(getBlockPos().getX() - 16, getBlockPos().getY() - 16, getBlockPos().getZ() - 16, getBlockPos().getX() + 16, getBlockPos().getY() + 16, getBlockPos().getZ() + 16);

        int mobCount = level.getEntitiesOfClass(living.getClass(), box, e -> e != null && e.getPersistentData().getBoolean("cageBorn")).size();
        return mobCount >= SoulShards.CONFIG.getBalance().getSpawnCap();
    }

    public void setState(boolean active)
    {
        BlockState state = getBlockState();
        if (!(state.getBlock() instanceof BlockSoulCage)) return;

        level.setBlock(getBlockPos(), state.setValue(BlockSoulCage.ACTIVE, active), 3);
        this.active = active;
    }

    @Override
    public void load(CompoundTag tag)
    {
        super.load(tag);
        this.inventory.deserializeNBT(tag.getCompound("inventory"));
        this.activeTime = tag.getInt("activeTime");
        this.active = tag.getBoolean("active");
    }

    @Override
    public void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);
        tag.put("inventory", inventory.serializeNBT());
        tag.putInt("activeTime", activeTime);
        tag.putBoolean("active", active);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
    {
        if (cap != ForgeCapabilities.ITEM_HANDLER) return LazyOptional.empty();

        return LazyOptional.of(() -> inventory).cast();
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap)
    {
        return super.getCapability(cap);
    }

    public ItemStackHandler getInventory()
    {
        return inventory;
    }

    @Nullable
    public Binding getBinding()
    {
        ItemStack stack = inventory.getStackInSlot(0);
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemSoulShard)) return null;

        return ((ItemSoulShard) stack.getItem()).getBinding(stack);
    }

    public boolean ownerOnline()
    {
        Binding binding = getBinding();
        //noinspection ConstantConditions
        return binding != null && binding.getOwner() != null && level.getServer().getPlayerList().getPlayer(binding.getOwner()) == null;
    }

    public static class SoulCageInventory extends ItemStackHandler
    {
        public SoulCageInventory()
        {
            super(1);
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
        {
            if (!(stack.getItem() instanceof ItemSoulShard)) return stack;

            Binding binding = ((ItemSoulShard) stack.getItem()).getBinding(stack);
            if (binding == null || binding.getBoundEntity() == null || !SoulShardsAPI.isAllowed(binding.getBoundEntity()))
                return stack;

            return super.insertItem(slot, stack, simulate);
        }
    }
}
