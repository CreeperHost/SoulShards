package net.creeperhost.soulshardsrespawn.datagen;

import net.creeperhost.soulshardsrespawn.SoulShards;
import net.creeperhost.soulshardsrespawn.client.TierSelectProperty;
import net.creeperhost.soulshardsrespawn.core.RegistrarSoulShards;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Stream;

public class GeneratorModels extends ModelProvider {

    public GeneratorModels(PackOutput output) {
        super(output, SoulShards.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {

        itemModels.generateFlatItem(RegistrarSoulShards.VILE_SWORD.get(),          ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(RegistrarSoulShards.VILE_DUST.get(),           ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(RegistrarSoulShards.CORRUPTED_ESSENCE.get(),   ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(RegistrarSoulShards.CORRUPTED_INGOT.get(),     ModelTemplates.FLAT_ITEM);

        Item shardItem = RegistrarSoulShards.SOUL_SHARD.get();
        ItemModel.Unbaked shardModel = ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(shardItem));
        ItemModel.Unbaked shardTier0 = ItemModelUtils.plainModel(itemModels.createFlatItemModel(shardItem, "_tier_0", ModelTemplates.FLAT_ITEM));
        ItemModel.Unbaked shardTier1 = ItemModelUtils.plainModel(itemModels.createFlatItemModel(shardItem, "_tier_1", ModelTemplates.FLAT_ITEM));
        ItemModel.Unbaked shardTier2 = ItemModelUtils.plainModel(itemModels.createFlatItemModel(shardItem, "_tier_2", ModelTemplates.FLAT_ITEM));
        ItemModel.Unbaked shardTier3 = ItemModelUtils.plainModel(itemModels.createFlatItemModel(shardItem, "_tier_3", ModelTemplates.FLAT_ITEM));
        ItemModel.Unbaked shardTier4 = ItemModelUtils.plainModel(itemModels.createFlatItemModel(shardItem, "_tier_4", ModelTemplates.FLAT_ITEM));
        ItemModel.Unbaked shardTier5 = ItemModelUtils.plainModel(itemModels.createFlatItemModel(shardItem, "_tier_5", ModelTemplates.FLAT_ITEM));
        itemModels.itemModelOutput.accept(shardItem,
                ItemModelUtils.select(new TierSelectProperty(), shardModel,
                        new SelectItemModel.SwitchCase<>(List.of(0), shardTier0),
                        new SelectItemModel.SwitchCase<>(List.of(1), shardTier1),
                        new SelectItemModel.SwitchCase<>(List.of(2), shardTier2),
                        new SelectItemModel.SwitchCase<>(List.of(3), shardTier3),
                        new SelectItemModel.SwitchCase<>(List.of(4), shardTier4),
                        new SelectItemModel.SwitchCase<>(List.of(5), shardTier5)
                )
        );

        blockModels.createTrivialBlock(RegistrarSoulShards.SOUL_CAGE.get(), TexturedModel.CUBE_INNER_FACES);

        super.registerModels(blockModels, itemModels);
    }

    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
//        return Stream.empty();
        return super.getKnownBlocks();
    }

    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
//        return Stream.empty();
        return super.getKnownItems();
    }
}
