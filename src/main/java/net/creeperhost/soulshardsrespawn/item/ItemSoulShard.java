package net.creeperhost.soulshardsrespawn.item;

import net.creeperhost.polylib.items.helpers.IDamageBarHelper;
import net.creeperhost.soulshardsrespawn.SoulShards;
import net.creeperhost.soulshardsrespawn.api.IShardTier;
import net.creeperhost.soulshardsrespawn.api.ISoulShard;
import net.creeperhost.soulshardsrespawn.block.TileEntitySoulCage;
import net.creeperhost.soulshardsrespawn.core.RegistrarSoulShards;
import net.creeperhost.soulshardsrespawn.core.data.Binding;
import net.creeperhost.soulshardsrespawn.core.data.Tier;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemSoulShard extends Item implements ISoulShard, IDamageBarHelper
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
                context.getPlayer().displayClientMessage(Component.translatable("chat.soulshards.absorb_disabled"), false);
                return InteractionResult.PASS;
            }

            if (binding.getKills() >= Tier.maxKills) return InteractionResult.PASS;
            if(!(context.getLevel().getBlockEntity(context.getClickedPos()) instanceof SpawnerBlockEntity)) return InteractionResult.FAIL;
            SpawnerBlockEntity mobSpawner = (SpawnerBlockEntity) context.getLevel().getBlockEntity(context.getClickedPos());
            if (mobSpawner == null) return InteractionResult.PASS;

            try
            {
                ResourceLocation entityId = Registry.ENTITY_TYPE.getKey(mobSpawner.getSpawner().getOrCreateDisplayEntity(context.getLevel()).getType());
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
        else if (state.getBlock() == RegistrarSoulShards.SOUL_CAGE.get())
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
    public void fillItemCategory(@NotNull CreativeModeTab group, @NotNull NonNullList<ItemStack> items)
    {
        if (!allowedIn(group)) return;

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
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag)
    {
        Binding binding = getBinding(stack);
        if (binding == null) return;

        if (binding.getBoundEntity() != null)
        {
            EntityType<?> entityEntry = ForgeRegistries.ENTITY_TYPES.getValue(binding.getBoundEntity());
            if (entityEntry != null)
            {
                ResourceLocation resourceLocation = Registry.ENTITY_TYPE.getKey(entityEntry);
                tooltip.add(Component.translatable("tooltip.soulshards.bound", resourceLocation));
            }
        }

        tooltip.add(Component.translatable("tooltip.soulshards.tier", binding.getTier().getIndex()));
        tooltip.add(Component.translatable("tooltip.soulshards.kills", binding.getKills()));
        if (flag.isAdvanced() && binding.getOwner() != null)
            tooltip.add(Component.translatable("tooltip.soulshards.owner", binding.getOwner().toString()));
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack)
    {
        Binding binding = getBinding(stack);
        return binding != null && binding.getKills() >= Tier.maxKills;
    }


    @Override
    public int getMaxStackSize(ItemStack stack)
    {
        return getBinding(stack) == null ? 64 : 1;
    }

    @Override
    public boolean isBarVisible(@Nonnull ItemStack itemStack)
    {
        Binding binding = getBinding(itemStack);
        return SoulShards.CONFIG.getClient().displayDurabilityBar() && binding != null && binding.getKills() < Tier.maxKills;
    }

    @Override
    public float getWidthForBar(ItemStack stack)
    {
        Binding binding = getBinding(stack);
        if (binding == null) return 1;

        return 1F - ((float) binding.getKills() / (float) Tier.maxKills);
    }

    @Override
    public int getBarWidth(@Nonnull ItemStack stack)
    {
        return this.getScaledBarWidth(stack);
    }

    @Override
    public int getBarColor(@Nonnull ItemStack stack)
    {
        return this.getColorForBar(stack);
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
