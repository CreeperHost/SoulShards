package net.creeperhost.soulshardsrespawn;

import net.creeperhost.soulshardsrespawn.client.render.RenderTileSoulCage;
import net.creeperhost.soulshardsrespawn.core.RegistrarSoulShards;
import net.creeperhost.soulshardsrespawn.core.data.Binding;
import net.creeperhost.soulshardsrespawn.core.data.Tier;
import net.creeperhost.soulshardsrespawn.item.ItemSoulShard;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static net.creeperhost.soulshardsrespawn.SoulShards.MODID;

public class ClientInit {
    public static void init() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(ClientInit::clientSetupEvent);
        modBus.addListener(ClientInit::registerRenderers);
    }

    private static void clientSetupEvent(FMLClientSetupEvent event) {
        ItemBlockRenderTypes.setRenderLayer(RegistrarSoulShards.SOUL_CAGE, RenderType.cutout());
        event.enqueueWork(() ->
        {
            ItemProperties.register(RegistrarSoulShards.SOUL_SHARD, new ResourceLocation(MODID, "bound"), (stack, level, living, id) ->
            {
                ItemSoulShard soulShard = (ItemSoulShard) stack.getItem();
                return soulShard.getBinding(stack) != null ? 1.0F : 0.0F;
            });

            ItemProperties.register(RegistrarSoulShards.SOUL_SHARD, new ResourceLocation(MODID, "tier"), (stack, level, living, id) ->
            {
                ItemSoulShard soulShard = (ItemSoulShard) stack.getItem();
                Binding binding = soulShard.getBinding(stack);
                if (binding == null) return 0F;

                return Float.parseFloat("0." + Tier.INDEXED.indexOf(binding.getTier()));
            });
        });
    }

    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(RegistrarSoulShards.SOUL_CAGE_TE, RenderTileSoulCage::new);
    }
}
