package net.creeperhost.soulshardsrespawn.client;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.creeperhost.soulshardsrespawn.core.data.Binding;
import net.creeperhost.soulshardsrespawn.item.ItemSoulShard;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Created by brandon3055 on 13/01/2025
 */
public record TierSelectProperty() implements SelectItemModelProperty<Integer> {
    public static final SelectItemModelProperty.Type<TierSelectProperty, Integer> TYPE;

    @Nullable
    @Override
    public Integer get(ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity, int i, ItemDisplayContext itemDisplayContext) {
        if (itemStack.getItem() instanceof ItemSoulShard soulShard) {
            Binding binding = soulShard.getBinding(itemStack);
            return binding == null ? null : binding.getTier().getIndex();
        }
        return null;
    }

    @Override
    public Type<? extends SelectItemModelProperty<Integer>, Integer> type() {
        return TYPE;
    }

    static {
        TYPE = Type.create(MapCodec.unit(new TierSelectProperty()), Codec.INT);
    }
}
