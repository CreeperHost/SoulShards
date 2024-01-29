package net.creeperhost.soulshardsrespawn.compat.jade;

import net.creeperhost.soulshardsrespawn.SoulShards;
import net.creeperhost.soulshardsrespawn.block.BlockSoulCage;
import net.creeperhost.soulshardsrespawn.block.TileEntitySoulCage;
import net.creeperhost.soulshardsrespawn.core.data.Binding;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

/**
 * Created by brandon3055 on 29/01/2024
 */
@WailaPlugin
public class JadePlugin implements IWailaPlugin {
    private static final ResourceLocation ENTITY_DATA_ID = new ResourceLocation(SoulShards.MODID, "entity_data");
    private static final ResourceLocation CAGE_DATA_ID = new ResourceLocation(SoulShards.MODID, "cage_data");

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
                Binding binding = ((TileEntitySoulCage) accessor.getBlockEntity()).getBinding();
                if (binding != null) {
                    tag.put("binding", binding.serializeNBT());
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
                Binding binding = new Binding(accessor.getServerData().getCompound("binding"));
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