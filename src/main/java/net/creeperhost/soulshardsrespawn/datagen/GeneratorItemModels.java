package net.creeperhost.soulshardsrespawn.datagen;

import net.creeperhost.soulshardsrespawn.SoulShards;
import net.creeperhost.soulshardsrespawn.core.RegistrarSoulShards;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class GeneratorItemModels extends ItemModelProvider
{
    public GeneratorItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper)
    {
        super(generator.getPackOutput(), SoulShards.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels()
    {
        singleTexture(getPath(RegistrarSoulShards.VILE_SWORD.get()), mcLoc("item/handheld"), "layer0", modLoc("item/vile_sword"));
        singleTexture(getPath(RegistrarSoulShards.VILE_DUST.get()), mcLoc("item/generated"), "layer0", modLoc("item/vile_dust"));
        singleTexture(getPath(RegistrarSoulShards.CORRUPTED_ESSENCE.get()), mcLoc("item/generated"), "layer0", modLoc("item/corrupted_essence"));
        singleTexture(getPath(RegistrarSoulShards.CORRUPTED_INGOT.get()), mcLoc("item/generated"), "layer0", modLoc("item/corrupted_ingot"));

        singleTexture("soul_shard_tier_0", mcLoc("item/generated"), "layer0", modLoc("item/soul_shard_tier_0"));
        singleTexture("soul_shard_tier_1", mcLoc("item/generated"), "layer0", modLoc("item/soul_shard_tier_1"));
        singleTexture("soul_shard_tier_2", mcLoc("item/generated"), "layer0", modLoc("item/soul_shard_tier_2"));
        singleTexture("soul_shard_tier_3", mcLoc("item/generated"), "layer0", modLoc("item/soul_shard_tier_3"));
        singleTexture("soul_shard_tier_4", mcLoc("item/generated"), "layer0", modLoc("item/soul_shard_tier_4"));
        singleTexture("soul_shard_tier_5", mcLoc("item/generated"), "layer0", modLoc("item/soul_shard_tier_5"));


        registerBlockModel(RegistrarSoulShards.SOUL_CAGE.get());
    }

    public String getPath(Item item)
    {
        return ForgeRegistries.ITEMS.getKey(item).getPath();
    }

    private void registerBlockModel(Block block)
    {
        String path = ForgeRegistries.BLOCKS.getKey(block).getPath();
        getBuilder(path).parent(new ModelFile.UncheckedModelFile(modLoc("block/" + path)));
    }

    @Override
    public @NotNull String getName()
    {
        return "Item Models";
    }
}
