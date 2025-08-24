package net.creeperhost.soulshardsrespawn.datagen;

import net.creeperhost.soulshardsrespawn.SoulShards;
import net.creeperhost.soulshardsrespawn.api.SoulShardsAPI;
import net.creeperhost.soulshardsrespawn.core.RegistrarSoulShards;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class GeneratorItemTags extends ItemTagsProvider
{
    public GeneratorItemTags(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> providerCompletableFuture)
    {
        super(packOutput, providerCompletableFuture, SoulShards.MODID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider)
    {
        tag(SoulShards.CORRUPTED_INGOT).add(RegistrarSoulShards.CORRUPTED_INGOT.get());

        tag(ItemTags.SWORD_ENCHANTABLE).add(RegistrarSoulShards.VILE_SWORD.get());
        tag(ItemTags.SHARP_WEAPON_ENCHANTABLE).add(RegistrarSoulShards.VILE_SWORD.get());
        tag(ItemTags.FIRE_ASPECT_ENCHANTABLE).add(RegistrarSoulShards.VILE_SWORD.get());
        tag(ItemTags.DURABILITY_ENCHANTABLE).add(RegistrarSoulShards.VILE_SWORD.get());
        tag(SoulShardsAPI.SOUL_STEALER_ENCHANTABLE).add(RegistrarSoulShards.VILE_SWORD.get());

        tag(ItemTags.SWORDS).add(RegistrarSoulShards.VILE_SWORD.get());
    }
}
