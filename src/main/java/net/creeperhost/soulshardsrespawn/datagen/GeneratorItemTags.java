package net.creeperhost.soulshardsrespawn.datagen;

import net.creeperhost.soulshardsrespawn.api.SoulShardsAPI;
import net.creeperhost.soulshardsrespawn.core.RegistrarSoulShards;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

public class GeneratorItemTags extends ItemTagsProvider
{
    public GeneratorItemTags(PackOutput p_275343_, CompletableFuture<HolderLookup.Provider> p_275729_, CompletableFuture<TagLookup<Block>> p_275322_)
    {
        super(p_275343_, p_275729_, p_275322_);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        tag(ItemTags.SWORD_ENCHANTABLE).add(RegistrarSoulShards.VILE_SWORD.get());
        tag(ItemTags.SHARP_WEAPON_ENCHANTABLE).add(RegistrarSoulShards.VILE_SWORD.get());
        tag(ItemTags.FIRE_ASPECT_ENCHANTABLE).add(RegistrarSoulShards.VILE_SWORD.get());
        tag(SoulShardsAPI.SOUL_STEALER_ENCHANTABLE).add(RegistrarSoulShards.VILE_SWORD.get());
    }
}
