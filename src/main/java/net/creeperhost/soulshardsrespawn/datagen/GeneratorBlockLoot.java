package net.creeperhost.soulshardsrespawn.datagen;

import net.creeperhost.soulshardsrespawn.core.RegistrarSoulShards;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class GeneratorBlockLoot implements LootTableSubProvider
{
    @Override
    public void generate(@NotNull BiConsumer<ResourceLocation, LootTable.Builder> builderBiConsumer)
    {
        LootTable.Builder ret = LootTable.lootTable().withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .add(LootItem.lootTableItem(RegistrarSoulShards.SOUL_CAGE.get())));

        register(RegistrarSoulShards.SOUL_CAGE, ret, builderBiConsumer);
    }

    private void register(Supplier<? extends Block> b, LootTable.Builder table, BiConsumer<ResourceLocation, LootTable.Builder> builderBiConsumer)
    {
        register(BuiltInRegistries.BLOCK.getKey(b.get()), table, builderBiConsumer);
    }

    private void register(ResourceLocation name, LootTable.Builder table, BiConsumer<ResourceLocation, LootTable.Builder> builderBiConsumer)
    {
        ResourceLocation loc = toTableLoc(name);
        builderBiConsumer.accept(loc, table.setParamSet(LootContextParamSets.BLOCK));
    }

    private ResourceLocation toTableLoc(ResourceLocation in)
    {
        return new ResourceLocation(in.getNamespace(), "blocks/"+in.getPath());
    }
}
