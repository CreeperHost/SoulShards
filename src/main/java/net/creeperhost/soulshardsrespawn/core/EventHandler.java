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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.Set;

@Mod.EventBusSubscriber(modid = SoulShards.MODID)
public class EventHandler
{
    @SubscribeEvent
    public static void onEntityKill(LivingDeathEvent event)
    {
        if (event.getEntity() instanceof Player) return;

        if (!SoulShards.CONFIG.getBalance().allowFakePlayers() && event.getSource().getEntity() instanceof FakePlayer)
            return;

        ResourceLocation resourceLocation = ForgeRegistries.ENTITY_TYPES.getKey(event.getEntity().getType());
        if (!SoulShardsAPI.isAllowed(resourceLocation)) return;

        if (!SoulShards.CONFIG.getBalance().allowBossSpawns() && event.getEntity().getType().is(Tags.EntityTypes.BOSSES)) return;

        if (!SoulShards.CONFIG.getBalance().countCageBornForShard() && event.getEntity().getPersistentData().getBoolean("cageBorn"))
            return;

        if (event.getSource().getEntity() instanceof Player player)
        {

            BindingEvent.GetEntityName getEntityName = new BindingEvent.GetEntityName(event.getEntity());
            MinecraftForge.EVENT_BUS.post(getEntityName);
            ResourceLocation entityId = getEntityName.getEntityId() == null ? ForgeRegistries.ENTITY_TYPES.getKey(event.getEntity().getType()) : getEntityName.getEntityId();

            ItemStack shardItem = getFirstShard(player, entityId);
            if (shardItem.isEmpty()) return;
            ItemSoulShard soulShard = (ItemSoulShard) shardItem.getItem();

            boolean newItem = false;
            Binding binding = soulShard.getBinding(shardItem);
            if (binding == null)
            {
                BindingEvent.NewBinding newBinding = new BindingEvent.NewBinding(event.getEntity(), new Binding(null, 0));
                if (MinecraftForge.EVENT_BUS.post(newBinding)) return;

                if (shardItem.getCount() > 1)
                { // Peel off one blank shard from a stack of them
                    shardItem = shardItem.split(1);
                    newItem = true;
                }

                binding = (Binding) newBinding.getBinding();
            }

            ItemStack mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);

            // Base of 1 plus enchantment bonus
            int soulsGained = 1 + EnchantmentHelper.getItemEnchantmentLevel(RegistrarSoulShards.SOUL_STEALER.get(), mainHand);
            if (mainHand.getItem() instanceof ISoulWeapon)
                soulsGained += ((ISoulWeapon) mainHand.getItem()).getSoulBonus(mainHand, player, event.getEntity());

            BindingEvent.GainSouls gainSouls = new BindingEvent.GainSouls(event.getEntity(), binding, soulsGained);
            MinecraftForge.EVENT_BUS.post(gainSouls);

            if (binding.getBoundEntity() == null) binding.setBoundEntity(entityId);

            if (binding.getOwner() == null) binding.setOwner(player.getGameProfile().getId());

            soulShard.updateBinding(shardItem, binding.addKills(gainSouls.getAmount()));
            if (newItem) // Give the player the peeled off stack
                ItemHandlerHelper.giveItemToPlayer(player, shardItem);
        }
    }

    @SubscribeEvent
    public static void onBlockInteract(PlayerInteractEvent.RightClickBlock event)
    {
        MultiblockPattern pattern = ConfigSoulShards.getMultiblock();
        ItemStack held = event.getEntity().getItemInHand(event.getHand());
        if (!ItemStack.isSameItem(held, pattern.getCatalyst())) return;

        BlockState state = event.getLevel().getBlockState(event.getPos());
        if (!pattern.isOriginBlock(state)) return;

        InteractionResultHolder<Set<BlockPos>> matched = pattern.match(event.getLevel(), event.getPos());
        if (matched.getResult() != InteractionResult.SUCCESS) return;

        for (BlockPos pos : matched.getObject())
            event.getLevel().destroyBlock(pos, false);

        held.shrink(1);
        ItemHandlerHelper.giveItemToPlayer(event.getEntity(), new ItemStack(RegistrarSoulShards.SOUL_SHARD.get()));
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
                event.setCost(left.getTier().getIndex() * 6);
            }
        }
    }

    @SubscribeEvent
    public static void dropExperience(LivingExperienceDropEvent event)
    {
        if (!SoulShards.CONFIG.getBalance().shouldDropExperience() && event.getEntity().getPersistentData().getBoolean("cageBorn"))
            event.setCanceled(true);
    }

    @Nonnull
    public static ItemStack getFirstShard(Player player, ResourceLocation entityId)
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
