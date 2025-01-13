package net.creeperhost.soulshardsrespawn.compat.jade;

import net.creeperhost.soulshardsrespawn.SoulShards;
import net.creeperhost.soulshardsrespawn.block.BlockSoulCage;
import net.creeperhost.soulshardsrespawn.block.TileEntitySoulCage;
import net.creeperhost.soulshardsrespawn.core.data.Binding;
import net.creeperhost.soulshardsrespawn.item.ItemSoulShard;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

/**
 * Created by brandon3055 on 29/01/2024
 */
@WailaPlugin
public class JadePlugin implements IWailaPlugin {

    private static final ResourceLocation ENTITY_DATA_ID = ResourceLocation.fromNamespaceAndPath(SoulShards.MODID, "entity_data");
    private static final ResourceLocation CAGE_DATA_ID = ResourceLocation.fromNamespaceAndPath(SoulShards.MODID, "cage_data");

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerEntityDataProvider(new IServerDataProvider<>() {
            @Override
            public void appendServerData(CompoundTag tag, EntityAccessor accessor) {
                tag.putBoolean("cageBorn", accessor.getEntity().getPersistentData().contains("cageBorn"));
            }

            @Override
            public ResourceLocation getUid() {
                return ENTITY_DATA_ID;
            }
        }, LivingEntity.class);

        registration.registerBlockDataProvider(new IServerDataProvider<>() {
            @Override
            public void appendServerData(CompoundTag tag, BlockAccessor accessor) {
                ItemStack binding = ((TileEntitySoulCage) accessor.getBlockEntity()).getInventory().getStackInSlot(0);
                if (!binding.isEmpty()) {
                    tag.put("binding", binding.save(accessor.getLevel().registryAccess()));
                }
            }

            @Override
            public ResourceLocation getUid() {
                return CAGE_DATA_ID;
            }
        }, TileEntitySoulCage.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerEntityComponent(new IEntityComponentProvider() {
            @Override
            public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig iPluginConfig) {
                if (accessor.getServerData().getBoolean("cageBorn")) {
                    tooltip.add(Component.translatable("tooltip.soulshards.cage_born"));
                }
            }

            @Override
            public ResourceLocation getUid() {
                return ENTITY_DATA_ID;
            }
        }, LivingEntity.class);

        registration.registerBlockComponent(new IBlockComponentProvider() {
            @Override
            public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig iPluginConfig) {
                if (!accessor.getServerData().contains("binding")) {
                    return;
                }
                ItemStack stack = ItemStack.parseOptional(accessor.getLevel().registryAccess(), accessor.getServerData().getCompound("binding"));
                if (stack.isEmpty() || !(stack.getItem() instanceof ItemSoulShard itemSoulShard)) {
                    return;
                }
                Binding binding = itemSoulShard.getBinding(stack);
                if (binding == null) {
                    return;
                }

                if (binding.getBoundEntity() != null) {
                    tooltip.add(Component.translatable("tooltip.soulshards.bound", binding.getBoundEntity().toString()));
                }

                tooltip.add(Component.translatable("tooltip.soulshards.tier", binding.getTier().getIndex()));
            }

            @Override
            public ResourceLocation getUid() {
                return CAGE_DATA_ID;
            }
        }, BlockSoulCage.class);
    }
}
