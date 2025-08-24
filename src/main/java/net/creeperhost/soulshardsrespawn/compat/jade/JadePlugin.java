package net.creeperhost.soulshardsrespawn.compat.jade;

import net.creeperhost.soulshardsrespawn.SoulShards;
import net.creeperhost.soulshardsrespawn.block.BlockSoulCage;
import net.creeperhost.soulshardsrespawn.block.TileEntitySoulCage;
import net.creeperhost.soulshardsrespawn.core.data.Binding;
import net.creeperhost.soulshardsrespawn.item.ItemSoulShard;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

import java.util.Optional;

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
                    RegistryAccess access = accessor.getLevel().registryAccess();
                    Optional<Tag> opTag = ItemStack.OPTIONAL_CODEC.encodeStart(access.createSerializationContext(NbtOps.INSTANCE), binding).result();
                    opTag.ifPresent(e -> tag.put("binding", e));
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
                if (accessor.getServerData().getBooleanOr("cageBorn", false)) {
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

                CompoundTag tag = accessor.getServerData().getCompoundOrEmpty("binding");
                if (tag.isEmpty()) {
                    return;
                }

                RegistryAccess access = accessor.getLevel().registryAccess();

                Optional<ItemStack> result = ItemStack.OPTIONAL_CODEC.parse(access.createSerializationContext(NbtOps.INSTANCE), tag).result();
                if (result.isEmpty()) {
                    return;
                }
                ItemStack stack = result.orElse(ItemStack.EMPTY);

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
