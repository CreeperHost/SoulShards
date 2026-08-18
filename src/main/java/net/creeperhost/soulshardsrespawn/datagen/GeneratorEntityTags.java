package net.creeperhost.soulshardsrespawn.datagen;

import net.creeperhost.soulshardsrespawn.api.SoulShardsAPI;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityTypeIds;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class GeneratorEntityTags extends EntityTypeTagsProvider
{
    public GeneratorEntityTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider)
    {
        super(output, lookupProvider, "soulshards");
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider)
    {
        this.tag(SoulShardsAPI.SOULSHARDS_DENYLIST)
                .add(EntityTypeIds.ARMOR_STAND)
                .add(EntityTypeIds.ELDER_GUARDIAN)
                .add(EntityTypeIds.ENDER_DRAGON)
                .add(EntityTypeIds.WITHER)
                .add(EntityTypeIds.PLAYER);
    }
}
