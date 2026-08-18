package net.creeperhost.soulshardsrespawn.api;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class SoulShardsAPI
{
    public static final TagKey<EntityType<?>> SOULSHARDS_DENYLIST = TagKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("soulshards", "no_capture"));
    public static final TagKey<Item> SOUL_STEALER_ENCHANTABLE = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("soulshards", "enchantable/soul_stealer"));
    public static boolean isAllowed(Level level, Identifier resourceLocation)
    {
        EntityType<?> entityEntry = BuiltInRegistries.ENTITY_TYPE.getValue(resourceLocation);
        return entityEntry != null && !entityEntry.builtInRegistryHolder().is(SOULSHARDS_DENYLIST);
    }
}
