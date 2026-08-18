package net.creeperhost.soulshardsrespawn.core;

import com.google.gson.reflect.TypeToken;
import net.creeperhost.soulshardsrespawn.SoulShards;
import net.creeperhost.soulshardsrespawn.core.data.MultiblockPattern;
import net.creeperhost.soulshardsrespawn.core.util.JsonUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Objects;

public class ConfigSoulShards
{
    private static MultiblockPattern multiblock;

    private ConfigBalance balance;
    private ConfigClient client;

    private ConfigSoulShards(ConfigBalance balance, ConfigClient client)
    {
        this.balance = balance;
        this.client = client;
    }

    public ConfigSoulShards()
    {
        this(new ConfigBalance(), new ConfigClient());
    }

    public ConfigBalance getBalance()
    {
        return balance;
    }

    public ConfigClient getClient()
    {
        return client;
    }


    public static void handleMultiblock() {
        File multiblockFile = new File(SoulShards.CONFIG_DIR, "multiblock.json");
        if (!multiblockFile.exists()) {
            try {
                FileUtils.copyInputStreamToFile(Objects.requireNonNull(ConfigSoulShards.class.getResourceAsStream("/data/soulshards/multiblock.json")), multiblockFile);
            } catch (Throwable e) {
                SoulShards.LOGGER.error("Failed to load default multiblock config", e);
            }
        }
        multiblock = JsonUtil.fromJson(TypeToken.get(MultiblockPattern.class), multiblockFile);
        if (multiblock == null) multiblock = MultiblockPattern.DEFAULT;
    }

    public static MultiblockPattern getMultiblock()
    {
        if (multiblock == null) handleMultiblock();

        return multiblock;
    }

    public static class ConfigBalance
    {
        private boolean allowSpawnerAbsorption;
        private boolean allowFakePlayers;
        private int absorptionBonus;
        private boolean allowBossSpawns;
        private boolean countCageBornForShard;
        private boolean countVanillaSpawnerBornForShard = true;
        private boolean requireOwnerOnline;
        private boolean requireRedstoneSignal;
        private boolean allowShardCombination;
        private int spawnCap;
        private boolean dropExperience;

        public ConfigBalance(boolean allowSpawnerAbsorption, boolean allowFakePlayers, int absorptionBonus, boolean allowBossSpawns, boolean countCageBornForShard, boolean requireOwnerOnline, boolean requireRedstoneSignal, boolean allowShardCombination, int spawnCap, boolean dropExperience)
        {
            this(allowSpawnerAbsorption, allowFakePlayers, absorptionBonus, allowBossSpawns, countCageBornForShard, true, requireOwnerOnline, requireRedstoneSignal, allowShardCombination, spawnCap, dropExperience);
        }

        public ConfigBalance(boolean allowSpawnerAbsorption, boolean allowFakePlayers, int absorptionBonus, boolean allowBossSpawns, boolean countCageBornForShard, boolean countVanillaSpawnerBornForShard, boolean requireOwnerOnline, boolean requireRedstoneSignal, boolean allowShardCombination, int spawnCap, boolean dropExperience)
        {
            this.allowSpawnerAbsorption = allowSpawnerAbsorption;
            this.allowFakePlayers = allowFakePlayers;
            this.absorptionBonus = absorptionBonus;
            this.allowBossSpawns = allowBossSpawns;
            this.countCageBornForShard = countCageBornForShard;
            this.countVanillaSpawnerBornForShard = countVanillaSpawnerBornForShard;
            this.requireOwnerOnline = requireOwnerOnline;
            this.requireRedstoneSignal = requireRedstoneSignal;
            this.allowShardCombination = allowShardCombination;
            this.spawnCap = spawnCap;
            this.dropExperience = dropExperience;
        }

        public ConfigBalance()
        {
            this(true, false, 200, false, false, false, false, true, 32, false);
        }

        public boolean allowSpawnerAbsorption()
        {
            return allowSpawnerAbsorption;
        }

        public boolean allowFakePlayers()
        {
            return allowFakePlayers;
        }

        public int getAbsorptionBonus()
        {
            return absorptionBonus;
        }

        public boolean allowBossSpawns()
        {
            return allowBossSpawns;
        }

        public boolean countCageBornForShard()
        {
            return countCageBornForShard;
        }

        public boolean countVanillaSpawnerBornForShard()
        {
            return countVanillaSpawnerBornForShard;
        }

        public boolean requireOwnerOnline()
        {
            return requireOwnerOnline;
        }

        public boolean requireRedstoneSignal()
        {
            return requireRedstoneSignal;
        }

        public boolean allowShardCombination()
        {
            return allowShardCombination;
        }

        public int getSpawnCap()
        {
            return spawnCap;
        }

        public boolean shouldDropExperience()
        {
            return dropExperience;
        }
    }

    public static class ConfigClient
    {
        private boolean displayDurabilityBar;

        public ConfigClient(boolean displayDurabilityBar)
        {
            this.displayDurabilityBar = displayDurabilityBar;
        }

        public ConfigClient()
        {
            this(true);
        }

        public boolean displayDurabilityBar()
        {
            return displayDurabilityBar;
        }
    }
}
