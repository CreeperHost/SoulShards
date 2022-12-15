package net.creeperhost.soulshardsrespawn;

import com.google.gson.reflect.TypeToken;
import net.creeperhost.soulshardsrespawn.core.ConfigSoulShards;
import net.creeperhost.soulshardsrespawn.core.RegistrarSoulShards;
import net.creeperhost.soulshardsrespawn.core.data.Binding;
import net.creeperhost.soulshardsrespawn.core.data.Tier;
import net.creeperhost.soulshardsrespawn.core.util.JsonUtil;
import net.creeperhost.soulshardsrespawn.item.ItemSoulShard;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;

@Mod(SoulShards.MODID)
public class SoulShards
{
    public static final String MODID = "soulshards";
    public static final String NAME = "Soul Shards";
    public static final ConfigSoulShards CONFIG = JsonUtil.fromJson(TypeToken.get(ConfigSoulShards.class), new File(FMLPaths.CONFIGDIR.get().toFile(), MODID + "/" + MODID + ".json"), new ConfigSoulShards());

    public SoulShards()
    {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        Tier.readTiers();
        RegistrarSoulShards.ITEMS.register(eventBus);
        RegistrarSoulShards.BLOCKS.register(eventBus);
        RegistrarSoulShards.TILES_ENTITIES.register(eventBus);
        RegistrarSoulShards.ENCHANTMENTS.register(eventBus);
        eventBus.addListener(this::registerCreativeTab);
        eventBus.addListener(this::setupClient);
    }

    public void registerCreativeTab(CreativeModeTabEvent.Register event)
    {
        event.registerCreativeModeTab(new ResourceLocation(MODID, "creative_tab"), builder -> builder.icon(() -> new ItemStack(RegistrarSoulShards.SOUL_SHARD.get()))
                .title(Component.translatable("itemGroup.soulshards"))
                .displayItems((features, output, hasPermissions) ->
                {
                    RegistrarSoulShards.ITEMS.getEntries().forEach(itemRegistryObject -> output.accept(new ItemStack(itemRegistryObject.get())));
                    ItemSoulShard itemSoulShard = (ItemSoulShard) RegistrarSoulShards.SOUL_SHARD.get();
                    itemSoulShard.fillItemCategory().forEach(output::accept);
                }));

    }

    @SubscribeEvent
    public void setupClient(FMLClientSetupEvent event)
    {
        SoulShardsClient.initClient();
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
}
