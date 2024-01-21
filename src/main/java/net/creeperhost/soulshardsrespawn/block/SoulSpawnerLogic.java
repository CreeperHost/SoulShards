package net.creeperhost.soulshardsrespawn.block;

import net.creeperhost.soulshardsrespawn.SoulShards;
import net.creeperhost.soulshardsrespawn.api.CageSpawnEvent;
import net.creeperhost.soulshardsrespawn.api.IShardTier;
import net.creeperhost.soulshardsrespawn.core.data.Binding;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by brandon3055 on 11/01/2024
 */
public class SoulSpawnerLogic extends BaseSpawner {
    private static Map<ResourceLocation, Entity> RENDER_ENTITY_CACHE = new HashMap<>();

    private final TileEntitySoulCage tile;
    private double mobRotation;
    private double prevMobRotation;

    public SoulSpawnerLogic(TileEntitySoulCage tile) {
        this.tile = tile;
    }

    @Override
    public void setEntityId(EntityType<?> p_253682_, @Nullable Level p_254041_, RandomSource p_254221_, BlockPos p_254050_) {
    }

    @Override
    public void clientTick(Level level, BlockPos pos) {
        if (!tile.isActive()) {
            this.prevMobRotation = this.mobRotation;
        } else {
            double d3 = (float) pos.getX() + level.random.nextFloat();
            double d4 = (float) pos.getY() + level.random.nextFloat();
            double d5 = (float) pos.getZ() + level.random.nextFloat();
            level.addParticle(ParticleTypes.SMOKE, d3, d4, d5, 0.0D, 0.0D, 0.0D);
            level.addParticle(ParticleTypes.FLAME, d3, d4, d5, 0.0D, 0.0D, 0.0D);

            if (tile.spawnDelay > 0) {
                tile.spawnDelay--;
            }

            this.prevMobRotation = this.mobRotation;
            Binding binding = tile.getBinding();
            if (binding != null) {
                double progress = 1F - (tile.spawnDelay / (double) binding.getTier().getCooldown());
                this.mobRotation = (this.mobRotation + (progress * 4.5D) + 0.5D) % 360.0D;
            } else {
                this.mobRotation = (this.mobRotation + (double) (1000.0F / ((float) tile.spawnDelay + 200.0F))) % 360.0D;
            }
        }
    }

    @Override
    public void serverTick(ServerLevel level, BlockPos pos) {
        if (!tile.isActive()) {
            return;
        }

        if (tile.spawnDelay == -1) {
            resetTimer();
            return;
        }

        if (tile.spawnDelay > 0) {
            tile.spawnDelay--;
            return;
        }

        Binding binding = tile.getBinding();
        IShardTier tier = binding.getTier();

        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(binding.getBoundEntity());
        if (type == null) {
            resetTimer();
            return;
        }

        boolean spawnedAny = false;
        int successCount = 0;
        for (int i = 0; i < tier.getSpawnAmount() + tier.getIndex() + 3; ++i) {
            LivingEntity entity = (LivingEntity) type.create(level);
            if (entity == null || hasReachedSpawnCap(entity)) {
                this.resetTimer();
                return;
            }

            do {
                double spawnX = pos.getX() + (level.random.nextDouble() - level.random.nextDouble()) * 4.0 + 0.5;
                double spawnY = pos.getY() + level.random.nextInt(3) - 1;
                double spawnZ = pos.getZ() + (level.random.nextDouble() - level.random.nextDouble()) * 4.0 + 0.5;
                entity.absMoveTo(spawnX, spawnY, spawnZ, 0, 0);
            } while (entity.blockPosition().getX() == tile.getBlockPos().getX() && entity.blockPosition().getZ() == tile.getBlockPos().getZ());
            entity.moveTo(entity.getX(), entity.getY(), entity.getZ(), level.random.nextFloat() * 360.0F, 0.0F);

            if (attemptEntitySpawn(binding, type, entity, level, (float) entity.getX(), (float) entity.getY(), (float) entity.getZ())) {
                entity.getPersistentData().putBoolean("cageBorn", true);
                level.levelEvent(2004, pos, 0);

                if (entity instanceof Mob mob) {
                    mob.spawnAnim();
                }

                spawnedAny = true;
                successCount++;
            }
            if (successCount >= tier.getSpawnAmount()) {
                break;
            }
        }

        if (spawnedAny) {
            this.resetTimer();
        }
    }

    public boolean attemptEntitySpawn(Binding binding, EntityType<?> type, LivingEntity entity, ServerLevel level, double x, double y, double z) {
        if (!level.noCollision(type.getAABB(x, y, z))) {
            return false;
        }
        BlockPos blockpos = BlockPos.containing(x, y, z);

        if (binding.getTier().checkLight() && !canSpawnInLight(entity)) {
            return false;
        }

        int tier = binding.getTier().getIndex();
        if (tier < 5 && !SpawnPlacements.checkSpawnRules(type, level, MobSpawnType.SPAWNER, blockpos, level.getRandom())) {
            return false;
        }

        if (!SoulShards.CONFIG.getBalance().allowBossSpawns() && !entity.canChangeDimensions()) {
            return false;
        }

        CageSpawnEvent cageEvent = new CageSpawnEvent(binding, tile.getInventory().getStackInSlot(0), entity);
        if (NeoForge.EVENT_BUS.post(cageEvent).isCanceled()) {
            return false;
        }

        if (entity instanceof Mob mob) {
            if (tier < 5 && (!mob.checkSpawnRules(level, MobSpawnType.SPAWNER) || !mob.checkSpawnObstruction(level))) {
                return false;
            }

            var finalizeEvent = EventHooks.onFinalizeSpawnSpawner(mob, level, level.getCurrentDifficultyAt(entity.blockPosition()), null, null, this);
            if (finalizeEvent != null) {
                mob.finalizeSpawn(level, finalizeEvent.getDifficulty(), finalizeEvent.getSpawnType(), finalizeEvent.getSpawnData(), finalizeEvent.getSpawnTag());
            }
        }

        return level.tryAddFreshEntityWithPassengers(entity);
    }

    private void resetTimer() {
        tile.resetTimer();
        broadcastEvent(tile.getLevel(), tile.getBlockPos(), 1);
    }

    private boolean hasReachedSpawnCap(LivingEntity living) {
        BlockPos pos = tile.getBlockPos();
        AABB box = new AABB(pos.getX() - 16, pos.getY() - 16, pos.getZ() - 16, pos.getX() + 16, pos.getY() + 16, pos.getZ() + 16);
        int mobCount = tile.getLevel().getEntitiesOfClass(living.getClass(), box, e -> e != null && e.getPersistentData().getBoolean("cageBorn")).size();
        return mobCount >= SoulShards.CONFIG.getBalance().getSpawnCap();
    }

    private boolean canSpawnInLight(LivingEntity entityLiving) {
        boolean light = tile.getLevel().getLightEngine().getRawBrightness(tile.getBlockPos(), 15) <= 8;
        return !(entityLiving instanceof Mob) || light;
    }

    @Nullable
    @Override
    public Entity getOrCreateDisplayEntity(Level level, BlockPos pos) {
        if (tile.getBinding() == null) return null;
        ResourceLocation key = tile.getBinding().getBoundEntity();
        if (key == null) return null;
        return RENDER_ENTITY_CACHE.computeIfAbsent(key, name -> {
            EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(name);
            if (type == null) return EntityType.PIG.create(level);
            Entity entity = type.create(level);
            if (entity == null) return EntityType.PIG.create(level);
            return entity;
        });
    }

    @Override
    @OnlyIn (Dist.CLIENT)
    public double getSpin() {
        return mobRotation;
    }

    @Override
    @OnlyIn (Dist.CLIENT)
    public double getoSpin() {
        return prevMobRotation;
    }

    @Override
    public void broadcastEvent(Level level, BlockPos blockPos, int event) {
        level.blockEvent(blockPos, Blocks.SPAWNER, event, 0);
    }
}
