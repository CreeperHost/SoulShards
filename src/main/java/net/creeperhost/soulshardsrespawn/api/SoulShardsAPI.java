package net.creeperhost.soulshardsrespawn.api;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

public class SoulShardsAPI
{
    public static final TagKey<EntityType<?>> SOULSHARDS_DENYLIST = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("soulshards", "no_capture"));

    public static boolean isAllowed(ResourceLocation resourceLocation)
    {
        EntityType<?> entityEntry = ForgeRegistries.ENTITY_TYPES.getValue(resourceLocation);
        if(entityEntry == null) return false;
        if(entityEntry.is(SOULSHARDS_DENYLIST)) return false;
        return true;
    }
}
