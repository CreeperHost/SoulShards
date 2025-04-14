package net.creeperhost.soulshardsrespawn.datagen;

import net.creeperhost.soulshardsrespawn.SoulShards;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EnchantmentTagsProvider;
import net.minecraft.tags.EnchantmentTags;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class GeneratorEnchantmentTags extends EnchantmentTagsProvider
{
    public GeneratorEnchantmentTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId) {
        super(output, lookupProvider, modId);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider)
    {
        tag(EnchantmentTags.NON_TREASURE).add(SoulShards.SOUL_STEALER);
    }
}
