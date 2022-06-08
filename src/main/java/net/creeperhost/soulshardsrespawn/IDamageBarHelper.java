package net.creeperhost.soulshardsrespawn;

import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

//TODO this should be part of the lib
public interface IDamageBarHelper
{
    float getWidthForBar(ItemStack var1);

    default int getScaledBarWidth(ItemStack stack)
    {
        return Math.round(13.0F - 13.0F * this.getWidthForBar(stack));
    }

    default int getColorForBar(ItemStack stack)
    {
        return Mth.hsvToRgb(Math.max(0.0F, 1.0F - this.getWidthForBar(stack)) / 3.0F, 1.0F, 1.0F);
    }
}
