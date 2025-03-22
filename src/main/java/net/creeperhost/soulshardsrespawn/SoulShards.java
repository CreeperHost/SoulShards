package net.creeperhost.soulshardsrespawn;

import com.google.gson.reflect.TypeToken;
import net.creeperhost.soulshardsrespawn.client.ClientInit;
import net.creeperhost.soulshardsrespawn.core.ConfigSoulShards;
import net.creeperhost.soulshardsrespawn.core.RegistrarSoulShards;
import net.creeperhost.soulshardsrespawn.core.data.Tier;
import net.creeperhost.soulshardsrespawn.core.util.JsonUtil;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.DistExecutor;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod (SoulShards.MODID)
public class SoulShards
{
    public static Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "soulshards";
    public static final File CONFIG_DIR = new File(FMLPaths.CONFIGDIR.get().toFile(), MODID);
    public static final ConfigSoulShards CONFIG = JsonUtil.fromJson(TypeToken.get(ConfigSoulShards.class), new File(CONFIG_DIR, MODID + ".json"), new ConfigSoulShards());

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
        ConfigSoulShards.handleMultiblock();//Ensure multiblock config is loaded on startup
    }
}
