package net.creeperhost.soulshardsrespawn.datagen;

import net.creeperhost.soulshardsrespawn.core.RegistrarSoulShards;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GeneratorBlockLoot extends VanillaBlockLoot
{
    public GeneratorBlockLoot(HolderLookup.Provider provider)
    {
        super(provider);
    }

    @Override
    protected void generate()
    {
        dropSelf(RegistrarSoulShards.SOUL_CAGE.get());
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        List<Block> knownBlocks = new ArrayList<>();
        knownBlocks.addAll(RegistrarSoulShards.BLOCKS.getEntries().stream().map(DeferredHolder::get).toList());
        return knownBlocks;
    }
}
