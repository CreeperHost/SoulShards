package net.creeperhost.soulshardsrespawn;

import net.creeperhost.soulshardsrespawn.core.RegistrarSoulShards;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;

public class SoulShardsClient
{
    public static void initClient()
    {
        ItemBlockRenderTypes.setRenderLayer(RegistrarSoulShards.SOUL_CAGE, RenderType.cutout());
    }
}