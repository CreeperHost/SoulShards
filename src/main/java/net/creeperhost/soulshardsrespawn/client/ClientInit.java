package net.creeperhost.soulshardsrespawn.client;

import net.creeperhost.soulshardsrespawn.core.RegistrarSoulShards;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterSelectItemModelPropertyEvent;

import static net.creeperhost.soulshardsrespawn.SoulShards.MODID;

/**
 * Created by brandon3055 on 12/01/2024
 */
public class ClientInit {

    public static void init(IEventBus iEventBus) {
        iEventBus.addListener(ClientInit::registerRenderers);
        iEventBus.addListener(ClientInit::registerSelectProperty);
    }


    private static void registerSelectProperty(RegisterSelectItemModelPropertyEvent event) {
        event.register(Identifier.fromNamespaceAndPath(MODID, "shard_tier"), TierSelectProperty.TYPE);
    }

    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(RegistrarSoulShards.SOUL_CAGE_TE.get(), SoulCageTileRenderer::new);
    }
}
