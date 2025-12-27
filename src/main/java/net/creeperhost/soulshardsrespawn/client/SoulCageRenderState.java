package net.creeperhost.soulshardsrespawn.client;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.jspecify.annotations.Nullable;

public class SoulCageRenderState extends BlockEntityRenderState
{
    public @Nullable EntityRenderState displayEntity;
    public float spin;
    public float scale;
}
