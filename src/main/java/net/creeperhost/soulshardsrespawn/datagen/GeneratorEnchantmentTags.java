package net.creeperhost.soulshardsrespawn.datagen;

import net.creeperhost.soulshardsrespawn.SoulShards;
import net.creeperhost.soulshardsrespawn.api.SoulShardsAPI;
import net.creeperhost.soulshardsrespawn.core.RegistrarSoulShards;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EnchantmentTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class GeneratorEnchantmentTags extends EnchantmentTagsProvider
{
    public GeneratorEnchantmentTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider)
    {
        tag(EnchantmentTags.NON_TREASURE).add(SoulShards.SOUL_STEALER);
    }
}
