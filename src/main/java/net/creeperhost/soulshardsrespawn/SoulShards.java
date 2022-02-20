package net.creeperhost.soulshardsrespawn;

import com.google.gson.reflect.TypeToken;
import net.creeperhost.soulshardsrespawn.core.ConfigSoulShards;
import net.creeperhost.soulshardsrespawn.core.RegistrarSoulShards;
import net.creeperhost.soulshardsrespawn.core.data.Binding;
import net.creeperhost.soulshardsrespawn.core.data.Tier;
import net.creeperhost.soulshardsrespawn.core.util.JsonUtil;
import net.creeperhost.soulshardsrespawn.item.ItemSoulShard;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(SoulShards.MODID)
public class SoulShards
{
    public static final String MODID = "soulshards";
    public static final String NAME = "Soul Shards";
    public static final Logger LOGGER = LogManager.getLogger(NAME);
    public static final ConfigSoulShards CONFIG = JsonUtil.fromJson(TypeToken.get(ConfigSoulShards.class), new File(FMLPaths.CONFIGDIR.get().toFile(), MODID + "/" + MODID + ".json"), new ConfigSoulShards());
    public static final CreativeModeTab TAB_SS = new CreativeModeTab(MODID)
    {
        @Override
        public ItemStack makeIcon()
        {
            ItemStack shard = new ItemStack(RegistrarSoulShards.SOUL_SHARD);
            Binding binding = new Binding(null, Tier.maxKills);
            ((ItemSoulShard) RegistrarSoulShards.SOUL_SHARD).updateBinding(shard, binding);
            return shard;
        }
    };

    public SoulShards()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    @SubscribeEvent
    public void setupClient(FMLClientSetupEvent event)
    {
        SoulShardsClient.initClient();
    }

    public void setup(FMLClientSetupEvent event)
    {
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
                if(binding == null) return 0F;

                return Float.parseFloat("0." + Tier.INDEXED.indexOf(binding.getTier()));
            });
        });
    }
}
