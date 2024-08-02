package net.creeperhost.soulshardsrespawn.core.data;

import net.creeperhost.soulshardsrespawn.SSDataComponentType;
import net.creeperhost.soulshardsrespawn.api.IBinding;
import net.creeperhost.soulshardsrespawn.api.IShardTier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.UUID;

public class Binding implements IBinding
{
    @Nullable
    private ResourceLocation boundEntity;
    @Nullable
    private UUID owner;
    private int kills;

    public Binding(ResourceLocation boundEntity, UUID owner, int kills)
    {
        this.boundEntity = boundEntity;
        this.owner = owner;
        this.kills = kills;
    }

    public Binding(ResourceLocation boundEntity, int kills)
    {
        this(boundEntity, null, kills);
    }

    @Nullable
    @Override
    public ResourceLocation getBoundEntity()
    {
        return boundEntity;
    }

    public Binding setBoundEntity(@Nullable ResourceLocation boundEntity)
    {
        this.boundEntity = boundEntity;
        return this;
    }

    @Nullable
    @Override
    public UUID getOwner()
    {
        return owner;
    }

    public Binding setOwner(@Nullable UUID owner)
    {
        this.owner = owner;
        return this;
    }

    @Override
    public int getKills()
    {
        return kills;
    }

    public Binding setKills(int kills)
    {
        this.kills = Math.min(Tier.maxKills, kills);
        return this;
    }

    @Override
    public Binding addKills(int kills)
    {
        this.kills = Math.min(Tier.maxKills, this.kills + kills);
        return this;
    }

    @Override
    public IShardTier getTier()
    {
        return Tier.TIERS.floorEntry(kills).getValue();
    }

    @Nullable
    public static Binding fromNBT(ItemStack stack)
    {
        if(stack.has(SSDataComponentType.BOUND_ENTITY) && stack.has(SSDataComponentType.OWNER) && stack.has(SSDataComponentType.KILLS))
        {
            ResourceLocation boundEntity = ResourceLocation.parse(stack.get(SSDataComponentType.BOUND_ENTITY));
            UUID owner = UUID.randomUUID();
            if(stack.get(SSDataComponentType.OWNER) != null && !stack.get(SSDataComponentType.OWNER).isEmpty()) {
                owner = UUID.fromString(stack.get(SSDataComponentType.OWNER));
            }
            else
            {
                stack.set(SSDataComponentType.OWNER, owner.toString());
            }
            int kills = stack.get(SSDataComponentType.KILLS);
            return new Binding(boundEntity, owner, kills);
        }

        return null;
    }
}
