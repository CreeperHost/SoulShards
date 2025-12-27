package net.creeperhost.soulshardsrespawn.api;

import net.minecraft.resources.Identifier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public interface IBinding
{
    @Nullable
    UUID getOwner();

    @Nullable
    Identifier getBoundEntity();

    int getKills();

    @Nonnull
    IBinding addKills(int amount);

    @Nonnull
    IShardTier getTier();
}
