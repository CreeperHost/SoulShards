package net.creeperhost.soulshardsrespawn.datagen;

import net.creeperhost.soulshardsrespawn.api.SoulShardsAPI;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class GeneratorEntityTags extends EntityTypeTagsProvider
{
    public GeneratorEntityTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper)
    {
        super(output, lookupProvider, "soulshards", existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider)
    {
        this.tag(SoulShardsAPI.SOULSHARDS_DENYLIST)
                .add(EntityType.ARMOR_STAND)
                .add(EntityType.ELDER_GUARDIAN)
                .add(EntityType.ENDER_DRAGON)
                .add(EntityType.WITHER)
                .add(EntityType.PLAYER);
    }
}
