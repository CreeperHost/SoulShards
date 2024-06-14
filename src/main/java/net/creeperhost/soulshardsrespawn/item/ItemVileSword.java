package net.creeperhost.soulshardsrespawn.item;

import net.creeperhost.soulshardsrespawn.api.ISoulWeapon;
import net.creeperhost.soulshardsrespawn.core.RegistrarSoulShards;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

public class ItemVileSword extends SwordItem implements ISoulWeapon
{
    public static final Tier MATERIAL_VILE = new MaterialVile();

    public ItemVileSword()
    {
        super(MATERIAL_VILE, new Item.Properties().attributes(SwordItem.createAttributes(MATERIAL_VILE, 3, -2.4F)));
    }

    @Override
    public int getSoulBonus(ItemStack stack, Player player, LivingEntity killedEntity)
    {
        return 1;
    }

    public static class MaterialVile implements Tier
    {
        @Override
        public int getUses()
        {
            return Tiers.IRON.getUses();
        }

        @Override
        public float getSpeed()
        {
            return Tiers.IRON.getSpeed();
        }

        @Override
        public float getAttackDamageBonus()
        {
            return Tiers.IRON.getAttackDamageBonus();
        }

        @Override
        public TagKey<Block> getIncorrectBlocksForDrops() {
            return null;
        }

        @Override
        public int getEnchantmentValue()
        {
            return Tiers.IRON.getEnchantmentValue();
        }

        @Override
        public Ingredient getRepairIngredient()
        {
            return Ingredient.of(RegistrarSoulShards.CORRUPTED_INGOT.get());
        }
    }
}
