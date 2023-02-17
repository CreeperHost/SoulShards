package net.creeperhost.soulshardsrespawn.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.creeperhost.soulshardsrespawn.block.TileEntitySoulCage;
import net.creeperhost.soulshardsrespawn.core.data.Binding;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Created by brandon3055 on 16/02/2023
 */
public class RenderTileSoulCage implements BlockEntityRenderer<TileEntitySoulCage> {

    public RenderTileSoulCage(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(TileEntitySoulCage tile, float partialTicks, PoseStack mStack, MultiBufferSource getter, int packedLight, int packedOverlay) {
        Entity entity = tile.getRenderEntity();
        if (entity == null) return;

        mStack.pushPose();
        mStack.translate(0.5D, 0.0D, 0.5D);

        float f = 0.53125F;
        float f1 = Math.max(entity.getBbWidth(), entity.getBbHeight());
        if ((double) f1 > 1.0D) {
            f /= f1;
        }

        mStack.translate(0.0D, (double) 0.4F, 0.0D);
        mStack.mulPose(Vector3f.YP.rotationDegrees((float) Mth.lerp((double) partialTicks, tile.lastRotation, tile.rotation) * 10.0F));
        mStack.translate(0.0D, (double) -0.2F, 0.0D);
        mStack.mulPose(Vector3f.XP.rotationDegrees(-30.0F));
        mStack.scale(f, f, f);
        Minecraft.getInstance().getEntityRenderDispatcher().render(entity, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks, mStack, getter, packedLight);
        mStack.popPose();
    }
}
