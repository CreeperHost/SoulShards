package net.creeperhost.soulshardsrespawn.api;

import dev.architectury.annotations.ForgeEventCancellable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

import javax.annotation.Nullable;

public class BindingEvent extends Event
{
    private final LivingEntity entity;
    private final IBinding binding;

    public BindingEvent(LivingEntity entity, IBinding binding)
    {
        this.entity = entity;
        this.binding = binding;
    }

    public IBinding getBinding()
    {
        return binding;
    }

    public LivingEntity getEntity()
    {
        return entity;
    }

    @ForgeEventCancellable
    public static class NewBinding extends BindingEvent implements ICancellableEvent
    {
        public NewBinding(LivingEntity entity, IBinding binding)
        {
            super(entity, binding);
        }
    }

    public static class GainSouls extends BindingEvent
    {

        private int amount;

        public GainSouls(LivingEntity entity, IBinding binding, int amount)
        {
            super(entity, binding);

            this.amount = amount;
        }

        public int getAmount()
        {
            return amount;
        }

        public void setAmount(int amount)
        {
            this.amount = amount;
        }
    }

    public static class GetEntityName extends Event
    {

        private final LivingEntity entity;
        @Nullable
        private ResourceLocation entityId;

        public GetEntityName(LivingEntity entity)
        {
            this.entity = entity;
        }

        public LivingEntity getEntity()
        {
            return entity;
        }

        @Nullable
        public ResourceLocation getEntityId()
        {
            return entityId;
        }

        public void setEntityId(@Nullable ResourceLocation entityId)
        {
            this.entityId = entityId;
        }
    }
}
