package net.creeperhost.soulshardsrespawn;

import com.google.gson.reflect.TypeToken;
import net.creeperhost.soulshardsrespawn.client.ClientInit;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
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
        RegistrarSoulShards.CREATIVE_TAB.register(eventBus);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientInit::init);
    }
}
