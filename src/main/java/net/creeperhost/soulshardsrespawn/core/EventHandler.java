package net.creeperhost.soulshardsrespawn.core;

import net.creeperhost.soulshardsrespawn.SoulShards;
import net.creeperhost.soulshardsrespawn.api.BindingEvent;
import net.creeperhost.soulshardsrespawn.api.ISoulWeapon;
import net.creeperhost.soulshardsrespawn.api.SoulShardsAPI;
import net.creeperhost.soulshardsrespawn.core.data.Binding;
import net.creeperhost.soulshardsrespawn.core.data.MultiblockPattern;
import net.creeperhost.soulshardsrespawn.core.data.Tier;
import net.creeperhost.soulshardsrespawn.item.ItemSoulShard;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.minecart.MinecartSpawner;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.entity.TrialSpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import javax.annotation.Nonnull;
import java.util.Set;

@EventBusSubscriber(modid = SoulShards.MODID)
public class EventHandler
{
    private static final String VANILLA_SPAWNER_BORN = "soulshards:vanilla_spawner_born";

    @SubscribeEvent
    public static void onFinalizeSpawn(FinalizeSpawnEvent event)
    {
        if (!EntitySpawnReason.isSpawner(event.getSpawnType())) return;

        var spawner = event.getSpawner();
        if (spawner == null) return;

        boolean vanillaSpawner = spawner.map(
                blockEntity -> blockEntity instanceof SpawnerBlockEntity || blockEntity instanceof TrialSpawnerBlockEntity,
                entity -> entity instanceof MinecartSpawner
        );
        if (vanillaSpawner)
            event.getEntity().getSelfAndPassengers().forEach(entity -> entity.getPersistentData().putBoolean(VANILLA_SPAWNER_BORN, true));
    }

    @SubscribeEvent
    public static void onEntityKill(LivingDeathEvent event)
    {
        if (event.getEntity() instanceof Player) return;

        if (!SoulShards.CONFIG.getBalance().allowFakePlayers() && event.getSource().getEntity() instanceof FakePlayer)
            return;

        Identifier resourceLocation = BuiltInRegistries.ENTITY_TYPE.getKey(event.getEntity().getType());
        if (!SoulShardsAPI.isAllowed(event.getEntity().level(), resourceLocation)) return;

        if (!SoulShards.CONFIG.getBalance().allowBossSpawns() && event.getEntity().is(Tags.EntityTypes.BOSSES)) return;

        boolean cageBorn = event.getEntity().getPersistentData().getBooleanOr("cageBorn", false);
        if (!SoulShards.CONFIG.getBalance().countCageBornForShard() && cageBorn)
            return;

        if (!cageBorn && !SoulShards.CONFIG.getBalance().countVanillaSpawnerBornForShard() && event.getEntity().getPersistentData().getBooleanOr(VANILLA_SPAWNER_BORN, false))
            return;

        if (event.getSource().getEntity() instanceof Player player)
        {

            BindingEvent.GetEntityName getEntityName = new BindingEvent.GetEntityName(event.getEntity());
            NeoForge.EVENT_BUS.post(getEntityName);
            Identifier entityId = getEntityName.getEntityId() == null ? BuiltInRegistries.ENTITY_TYPE.getKey(event.getEntity().getType()) : getEntityName.getEntityId();

            ItemStack shardItem = getFirstShard(player, entityId);
            if (shardItem.isEmpty()) return;
            ItemSoulShard soulShard = (ItemSoulShard) shardItem.getItem();

            boolean newItem = false;
            Binding binding = soulShard.getBinding(shardItem);
            if (binding == null)
            {
                BindingEvent.NewBinding newBinding = new BindingEvent.NewBinding(event.getEntity(), new Binding(null, 0));

                if (shardItem.getCount() > 1)
                { // Peel off one blank shard from a stack of them
                    shardItem = shardItem.split(1);
                    newItem = true;
                }

                binding = (Binding) newBinding.getBinding();
            }

            ItemStack mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);

            // Base of 1 plus enchantment bonus
            int soulsGained = 1 + EnchantmentHelper.getItemEnchantmentLevel(getEnchantment(event.getEntity().level()), mainHand);
            if (mainHand.getItem() instanceof ISoulWeapon)
                soulsGained += ((ISoulWeapon) mainHand.getItem()).getSoulBonus(mainHand, player, event.getEntity());

            BindingEvent.GainSouls gainSouls = new BindingEvent.GainSouls(event.getEntity(), binding, soulsGained);
            NeoForge.EVENT_BUS.post(gainSouls);

            if (binding.getBoundEntity() == null) binding.setBoundEntity(entityId);

            if (binding.getOwner() == null) binding.setOwner(player.getUUID());

            soulShard.updateBinding(shardItem, binding.addKills(gainSouls.getAmount()));
            if (newItem) // Give the player the peeled off stack
                player.getInventory().placeItemBackInInventory(shardItem);
        }
    }

    public static Holder<Enchantment> getEnchantment(Level level)
    {
        ResourceKey<Enchantment> SOUL_STEALER = ResourceKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath(SoulShards.MODID, "soul_stealer"));

        return level.registryAccess().lookup(Registries.ENCHANTMENT).get().get(SOUL_STEALER).get();
    }

    @SubscribeEvent
    public static void onBlockInteract(PlayerInteractEvent.RightClickBlock event)
    {
        MultiblockPattern pattern = ConfigSoulShards.getMultiblock();
        ItemStack held = event.getEntity().getItemInHand(event.getHand());
        if (!ItemStack.isSameItem(held, pattern.getCatalyst())) return;

        BlockState state = event.getLevel().getBlockState(event.getPos());
        if (!pattern.isOriginBlock(state)) return;

        Set<BlockPos> matched = pattern.match(event.getLevel(), event.getPos());
        if (matched == null) return;

        for (BlockPos pos : matched){
            event.getLevel().destroyBlock(pos, false);
        }

        held.shrink(1);
        event.getEntity().getInventory().placeItemBackInInventory(new ItemStack(RegistrarSoulShards.SOUL_SHARD.get()));
    }

    @SubscribeEvent
    public static void onAnvil(AnvilUpdateEvent event)
    {
        if (!SoulShards.CONFIG.getBalance().allowShardCombination()) return;

        if (event.getLeft().getItem() instanceof ItemSoulShard && event.getRight().getItem() instanceof ItemSoulShard)
        {
            Binding left = ((ItemSoulShard) event.getLeft().getItem()).getBinding(event.getLeft());
            Binding right = ((ItemSoulShard) event.getRight().getItem()).getBinding(event.getRight());

            if (left == null || right == null) return;

            if (left.getBoundEntity() != null && left.getBoundEntity().equals(right.getBoundEntity()))
            {
                ItemStack output = new ItemStack(RegistrarSoulShards.SOUL_SHARD.get());
                ((ItemSoulShard) output.getItem()).updateBinding(output, left.addKills(right.getKills()));
                event.setOutput(output);
                event.setXpCost(left.getTier().getIndex() * 6);
            }
        }
    }

    @SubscribeEvent
    public static void dropExperience(LivingExperienceDropEvent event)
    {
        if (!SoulShards.CONFIG.getBalance().shouldDropExperience() && event.getEntity().getPersistentData().getBooleanOr("cageBorn", false))
            event.setCanceled(true);
    }

    @Nonnull
    public static ItemStack getFirstShard(Player player, Identifier entityId)
    {
        // Checks the offhand first
        ItemStack shardItem = player.getItemInHand(InteractionHand.OFF_HAND);
        // If offhand isn't a shard, loop through the hotbar
        if (shardItem.isEmpty() || !(shardItem.getItem() instanceof ItemSoulShard))
        {
            for (int i = 0; i < 9; i++)
            {
                shardItem = player.getInventory().getItem(i);
                if (!shardItem.isEmpty() && shardItem.getItem() instanceof ItemSoulShard)
                {
                    Binding binding = ((ItemSoulShard) shardItem.getItem()).getBinding(shardItem);

                    // If there's no binding or no bound entity, this is a valid shard
                    if (binding == null || binding.getBoundEntity() == null) return shardItem;

                    // If there is a bound entity and we're less than the max kills, this is a valid shard
                    if (binding.getBoundEntity().equals(entityId) && binding.getKills() < Tier.maxKills)
                        return shardItem;
                }
            }
        }
        else
        { // If offhand is a shard, check it it
            Binding binding = ((ItemSoulShard) shardItem.getItem()).getBinding(shardItem);

            // If there's no binding or no bound entity, this is a valid shard
            if (binding == null || binding.getBoundEntity() == null) return shardItem;

            // If there is a bound entity and we're less than the max kills, this is a valid shard
            if (binding.getBoundEntity().equals(entityId) && binding.getKills() < Tier.maxKills) return shardItem;
        }

        return ItemStack.EMPTY; // No shard found
    }
}
