package net.creeperhost.soulshardsrespawn.api;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

public class SoulShardsAPI
{
    public static final TagKey<EntityType<?>> SOULSHARDS_DENYLIST = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("soulshards", "no_capture"));
    public static final TagKey<Item> SOUL_STEALER_ENCHANTABLE = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("soulshards", "enchantable/soul_stealer"));
    public static boolean isAllowed(ResourceLocation resourceLocation)
    {
        EntityType<?> entityEntry = BuiltInRegistries.ENTITY_TYPE.get(resourceLocation);
        if(entityEntry == null) return false;
        if(entityEntry.is(SOULSHARDS_DENYLIST)) return false;
        return true;
    }
}
