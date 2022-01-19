package net.creeperhost.soulshardsrespawn.item;

import net.creeperhost.soulshardsrespawn.SoulShards;
import net.creeperhost.soulshardsrespawn.api.IShardTier;
import net.creeperhost.soulshardsrespawn.api.ISoulShard;
import net.creeperhost.soulshardsrespawn.block.TileEntitySoulCage;
import net.creeperhost.soulshardsrespawn.core.RegistrarSoulShards;
import net.creeperhost.soulshardsrespawn.core.data.Binding;
import net.creeperhost.soulshardsrespawn.core.data.Tier;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SpawnerBlock;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;

public class ItemSoulShard extends Item implements ISoulShard
{
    public ItemSoulShard()
    {
        super(new Properties().tab(SoulShards.TAB_SS));
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack itemStack, UseOnContext context)
    {
        if (context.getPlayer() == null) return InteractionResult.PASS;

        BlockState state = context.getLevel().getBlockState(context.getClickedPos());
        ItemStack stack = context.getPlayer().getItemInHand(context.getHand());
        Binding binding = getBinding(stack);
        if (binding == null) return InteractionResult.PASS;

        if (state.getBlock() instanceof SpawnerBlock)
        {
            if (!SoulShards.CONFIG.getBalance().allowSpawnerAbsorption())
            {
                context.getPlayer().sendMessage(new TranslatableComponent("chat.soulshards.absorb_disabled"), null);
                return InteractionResult.PASS;
            }

            if (binding.getKills() >= Tier.maxKills) return InteractionResult.PASS;

            SpawnerBlockEntity mobSpawner = (SpawnerBlockEntity) context.getLevel().getBlockEntity(context.getClickedPos());
            if (mobSpawner == null) return InteractionResult.PASS;

            try
            {
                ResourceLocation entityId = mobSpawner.getSpawner().getSpawnerEntity().getType().getRegistryName();
                if (!SoulShards.CONFIG.getEntityList().isEnabled(entityId)) return InteractionResult.PASS;

                if (entityId == null || binding.getBoundEntity() == null || !binding.getBoundEntity().equals(entityId))
                    return InteractionResult.FAIL;

                updateBinding(stack, binding.addKills(SoulShards.CONFIG.getBalance().getAbsorptionBonus()));
                context.getLevel().destroyBlock(context.getClickedPos(), false);
                return InteractionResult.SUCCESS;
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else if (state.getBlock() == RegistrarSoulShards.SOUL_CAGE)
        {
            if (binding.getBoundEntity() == null) return InteractionResult.FAIL;

            TileEntitySoulCage cage = (TileEntitySoulCage) context.getLevel().getBlockEntity(context.getClickedPos());
            if (cage == null) return InteractionResult.PASS;

            IItemHandler itemHandler = cage.getInventory();
            if (itemHandler != null && itemHandler.getStackInSlot(0).isEmpty())
            {
                ItemHandlerHelper.insertItem(itemHandler, stack.copy(), false);
                cage.setChanged();//markDirty();
                cage.setState(true);
                context.getPlayer().setItemInHand(context.getHand(), ItemStack.EMPTY);
                return InteractionResult.SUCCESS;
            }
        }

        return super.onItemUseFirst(itemStack, context);
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items)
    {
        if (!allowdedIn(group)) return;

        items.add(new ItemStack(this));
        for (IShardTier tier : Tier.INDEXED)
        {
            ItemStack stack = new ItemStack(this);
            Binding binding = new Binding(null, tier.getKillRequirement());
            updateBinding(stack, binding);
            items.add(stack);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag)
    {
        Binding binding = getBinding(stack);
        if (binding == null) return;

        if (binding.getBoundEntity() != null)
        {
            EntityType<?> entityEntry = ForgeRegistries.ENTITIES.getValue(binding.getBoundEntity());
            if (entityEntry != null)
                tooltip.add(new TranslatableComponent("tooltip.soulshards.bound", entityEntry.getRegistryName()));
        }

        tooltip.add(new TranslatableComponent("tooltip.soulshards.tier", binding.getTier().getIndex()));
        tooltip.add(new TranslatableComponent("tooltip.soulshards.kills", binding.getKills()));
        if (flag.isAdvanced() && binding.getOwner() != null)
            tooltip.add(new TranslatableComponent("tooltip.soulshards.owner", binding.getOwner().toString()));
    }

    @Override
    public boolean isFoil(ItemStack stack)
    {
        Binding binding = getBinding(stack);
        return binding != null && binding.getKills() >= Tier.maxKills;
    }


    @Override
    public boolean isBarVisible(ItemStack itemStack)
    {
        Binding binding = getBinding(itemStack);
        return SoulShards.CONFIG.getClient().displayDurabilityBar() && binding != null && binding.getKills() < Tier.maxKills;
    }


    @Override
    public int getBarWidth(ItemStack stack)
    {
        Binding binding = getBinding(stack);
        if (binding == null) return 1;

        return 1 - (binding.getKills() / Tier.maxKills);
    }

    @Override
    public int getItemStackLimit(ItemStack stack)
    {
        return getBinding(stack) == null ? 64 : 1;
    }

    @Nullable
    @Override
    public Binding getBinding(ItemStack stack)
    {
        return Binding.fromNBT(stack);
    }

    public void updateBinding(ItemStack stack, Binding binding)
    {
        if (!stack.hasTag()) stack.setTag(new CompoundTag());

        stack.getTag().put("binding", binding.serializeNBT());
    }
}
