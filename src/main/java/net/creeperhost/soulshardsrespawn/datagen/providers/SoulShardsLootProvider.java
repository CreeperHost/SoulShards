package net.creeperhost.soulshardsrespawn.datagen.providers;

import com.google.common.collect.ImmutableList;
import net.creeperhost.soulshardsrespawn.datagen.GeneratorBlockLoot;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class SoulShardsLootProvider extends LootTableProvider
{

    public SoulShardsLootProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> providerCompletableFuture)
    {
        super(output, Set.of(), List.of(), providerCompletableFuture);
    }

    //TODO
//    @Override
//    public List<SubProviderEntry> getTables()
//    {
//        return ImmutableList.of(new SubProviderEntry(GeneratorBlockLoot::new, LootContextParamSets.BLOCK));
//    }
//
//    @Override
//    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationcontext)
//    {
//        map.forEach((p_278897_, p_278898_) -> {
//            p_278898_.validate(validationcontext.setParams(p_278898_.getParamSet()).enterElement("{" + p_278897_ + "}", new LootDataId<>(LootDataType.TABLE, p_278897_)));
//        });
////        map.forEach((p_218436_2_, p_218436_3_) -> {
////            LootTables.validate(validationcontext, p_218436_2_, p_218436_3_);
////        });
//    }
}
