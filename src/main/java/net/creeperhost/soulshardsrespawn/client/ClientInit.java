package net.creeperhost.soulshardsrespawn.client;

import net.creeperhost.soulshardsrespawn.SoulShards;
import net.creeperhost.soulshardsrespawn.core.RegistrarSoulShards;
import net.creeperhost.soulshardsrespawn.core.data.Binding;
import net.creeperhost.soulshardsrespawn.core.data.Tier;
import net.creeperhost.soulshardsrespawn.item.ItemSoulShard;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static net.creeperhost.soulshardsrespawn.SoulShards.MODID;

/**
 * Created by brandon3055 on 12/01/2024
 */
public class ClientInit {

    public static void init() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(ClientInit::setupClient);
        modBus.addListener(ClientInit::registerRenderers);
    }

    private static void setupClient(FMLClientSetupEvent event) {
        event.enqueueWork(() ->
        {
            ItemProperties.register(RegistrarSoulShards.SOUL_SHARD.get(), new ResourceLocation(MODID, "bound"), (stack, level, living, id) ->
            {
                ItemSoulShard soulShard = (ItemSoulShard) stack.getItem();
                return soulShard.getBinding(stack) != null ? 1.0F : 0.0F;
            });

            ItemProperties.register(RegistrarSoulShards.SOUL_SHARD.get(), new ResourceLocation(MODID, "tier"), (stack, level, living, id) ->
            {
                ItemSoulShard soulShard = (ItemSoulShard) stack.getItem();
                Binding binding = soulShard.getBinding(stack);
                if(binding == null) return 0F;

                return Float.parseFloat("0." + Tier.INDEXED.indexOf(binding.getTier()));
            });


        });
    }

    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(RegistrarSoulShards.SOUL_CAGE_TE.get(), SoulCageTileRenderer::new);
    }
}
