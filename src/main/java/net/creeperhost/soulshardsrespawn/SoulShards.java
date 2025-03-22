package net.creeperhost.soulshardsrespawn;

import com.google.gson.reflect.TypeToken;
import dev.architectury.platform.Platform;
import net.creeperhost.soulshardsrespawn.client.ClientInit;
import net.creeperhost.soulshardsrespawn.core.ConfigSoulShards;
import net.creeperhost.soulshardsrespawn.core.RegistrarSoulShards;
import net.creeperhost.soulshardsrespawn.core.data.Tier;
import net.creeperhost.soulshardsrespawn.core.util.JsonUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(SoulShards.MODID)
public class SoulShards
{
    public static Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "soulshards";
    public static final File CONFIG_DIR = new File(FMLPaths.CONFIGDIR.get().toFile(), MODID);
    public static final ConfigSoulShards CONFIG = JsonUtil.fromJson(TypeToken.get(ConfigSoulShards.class), new File(CONFIG_DIR, MODID + ".json"), new ConfigSoulShards());
    public static final ResourceKey<Enchantment> SOUL_STEALER = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(MODID, "soul_stealer"));

    public SoulShards(IEventBus eventBus)
    {
        Tier.readTiers();
        RegistrarSoulShards.ITEMS.register(eventBus);
        RegistrarSoulShards.BLOCKS.register(eventBus);
        RegistrarSoulShards.TILES_ENTITIES.register(eventBus);
        SSDataComponentType.COMPONENTS.register(eventBus);
        RegistrarSoulShards.CREATIVE_TAB.register(eventBus);
        if(Platform.getEnv().isClient())
        {
            ClientInit.init(eventBus);
        }
        ConfigSoulShards.handleMultiblock();//Ensure multiblock config is loaded on startup
    }
}
