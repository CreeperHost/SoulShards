package net.creeperhost.soulshardsrespawn.datagen;

import net.creeperhost.soulshardsrespawn.SoulShards;
import net.creeperhost.soulshardsrespawn.block.BlockSoulCage;
import net.creeperhost.soulshardsrespawn.core.RegistrarSoulShards;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

/**
 * Created by brandon3055 on 16/02/2023
 */
public class BlockStateGenerator extends BlockStateProvider {

    public BlockStateGenerator(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, SoulShards.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        VariantBlockStateBuilder cageBuilder = getVariantBuilder(RegistrarSoulShards.SOUL_CAGE.get());
        cageBuilder.addModels(cageBuilder.partialState().with(BlockSoulCage.ACTIVE, false), ConfiguredModel.builder().modelFile(models().cubeAll("soul_cage_inactive", modLoc("block/soul_cage")).renderType("cutout")).build());
        cageBuilder.addModels(cageBuilder.partialState().with(BlockSoulCage.ACTIVE, true), ConfiguredModel.builder().modelFile(models().cubeAll("soul_cage_active", modLoc("block/soul_cage_active")).renderType("cutout")).build());
    }
}
