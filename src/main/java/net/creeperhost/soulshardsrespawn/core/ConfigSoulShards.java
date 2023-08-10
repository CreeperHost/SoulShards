package net.creeperhost.soulshardsrespawn.core;

import net.creeperhost.soulshardsrespawn.core.data.MultiblockPattern;

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


    public static void handleMultiblock()
    {
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
        private boolean requireOwnerOnline;
        private boolean requireRedstoneSignal;
        private boolean allowShardCombination;
        private int spawnCap;
        private boolean dropExperience;

        public ConfigBalance(boolean allowSpawnerAbsorption, boolean allowFakePlayers, int absorptionBonus, boolean allowBossSpawns, boolean countCageBornForShard, boolean requireOwnerOnline, boolean requireRedstoneSignal, boolean allowShardCombination, int spawnCap, boolean dropExperience)
        {
            this.allowSpawnerAbsorption = allowSpawnerAbsorption;
            this.allowFakePlayers = allowFakePlayers;
            this.absorptionBonus = absorptionBonus;
            this.allowBossSpawns = allowBossSpawns;
            this.countCageBornForShard = countCageBornForShard;
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
