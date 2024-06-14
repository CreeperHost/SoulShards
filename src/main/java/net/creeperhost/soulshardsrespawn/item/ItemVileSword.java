package net.creeperhost.soulshardsrespawn.item;

import net.creeperhost.soulshardsrespawn.api.ISoulWeapon;
import net.creeperhost.soulshardsrespawn.core.RegistrarSoulShards;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
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
        super(MATERIAL_VILE, new Properties().component(DataComponents.TOOL, createToolProperties()));
    }

    public static Tool createToolProperties() {
        return new Tool(List.of(Tool.Rule.minesAndDrops(List.of(Blocks.COBWEB), 15.0F), Tool.Rule.overrideSpeed(BlockTags.SWORD_EFFICIENT, 1.5F)), 1.0F, 2);
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
