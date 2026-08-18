package net.creeperhost.soulshardsrespawn.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.creeperhost.soulshardsrespawn.block.SoulSpawnerLogic;
import net.creeperhost.soulshardsrespawn.block.TileEntitySoulCage;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.SpawnerRenderer;
import net.minecraft.client.renderer.blockentity.TrialSpawnerRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * Created by brandon3055 on 12/01/2024
 */
public class SoulCageTileRenderer implements BlockEntityRenderer<TileEntitySoulCage, SoulCageRenderState> {
    private final EntityRenderDispatcher entityRenderer;

    public SoulCageTileRenderer(BlockEntityRendererProvider.Context context) {
        this.entityRenderer = context.entityRenderer();
    }

    @Override
    public SoulCageRenderState createRenderState()
    {
        return new SoulCageRenderState();
    }

    @Override
    public void extractRenderState(
            TileEntitySoulCage tile,
            SoulCageRenderState state,
            float partialTicks,
            Vec3 cameraPosition,
            ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress
    ) {
        BlockEntityRenderer.super.extractRenderState(tile, state, partialTicks, cameraPosition, breakProgress);
        if (tile.getLevel() != null) {
            SoulSpawnerLogic spawnerLogic = tile.spawnerLogic;
            Entity displayEntity = spawnerLogic.getOrCreateDisplayEntity(tile.getLevel(), tile.getBlockPos());
            TrialSpawnerRenderer.extractSpawnerData(
                    state,
                    partialTicks,
                    displayEntity,
                    this.entityRenderer,
                    spawnerLogic.getOSpin(),
                    spawnerLogic.getSpin()
            );
        }
    }

    @Override
    public void submit(SoulCageRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        if (state.displayEntity != null) {
            SpawnerRenderer.submitEntityInSpawner(
                    poseStack,
                    submitNodeCollector,
                    state.displayEntity,
                    this.entityRenderer,
                    state.spin,
                    state.scale,
                    cameraRenderState
            );
        }
    }

    @Override
    public AABB getRenderBoundingBox(TileEntitySoulCage tile) {
        BlockPos pos = tile.getBlockPos();
        return new AABB(pos.getX() - 1.0, pos.getY() - 1.0, pos.getZ() - 1.0, pos.getX() + 2.0, pos.getY() + 2.0, pos.getZ() + 2.0);
    }
}
