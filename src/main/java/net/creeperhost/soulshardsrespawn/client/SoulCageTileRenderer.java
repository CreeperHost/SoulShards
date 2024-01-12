package net.creeperhost.soulshardsrespawn.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.creeperhost.soulshardsrespawn.block.SoulSpawnerLogic;
import net.creeperhost.soulshardsrespawn.block.TileEntitySoulCage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

/**
 * Created by brandon3055 on 12/01/2024
 */
public class SoulCageTileRenderer implements BlockEntityRenderer<TileEntitySoulCage> {

    public SoulCageTileRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(TileEntitySoulCage tile, float partialTicks, PoseStack stack, MultiBufferSource buffers, int packedLight, int packedOverlay) {
        SoulSpawnerLogic spawnerLogic = tile.spawnerLogic;
        stack.pushPose();
        stack.translate(0.5D, 0.0D, 0.5D);
        Entity entity = spawnerLogic.getOrCreateDisplayEntity(tile.getLevel(), tile.getLevel().random, tile.getBlockPos());
        if (entity != null) {
            float f = 0.53125F;
            float f1 = Math.max(entity.getBbWidth(), entity.getBbHeight());
            if ((double) f1 > 1.0D) {
                f /= f1;
            }

            stack.translate(0.0D, 0.4F, 0.0D);
            stack.mulPose(Axis.YP.rotationDegrees((float) Mth.lerp(partialTicks, spawnerLogic.getoSpin(), spawnerLogic.getSpin()) * 10.0F));
            stack.translate(0.0D, -0.2F, 0.0D);
            stack.mulPose(Axis.XP.rotationDegrees(-30.0F));
            stack.scale(f, f, f);
            Minecraft.getInstance().getEntityRenderDispatcher().render(entity, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks, stack, buffers, packedLight);
        }
        stack.popPose();

        stack.translate(0.5, 1F-(1F/32F), 0.5);
        stack.scale(0.75F, 0.75F, 0.75F);
        stack.mulPose(Axis.XP.rotationDegrees(90.0F));
    }
}
