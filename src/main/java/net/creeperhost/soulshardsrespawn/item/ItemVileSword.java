package net.creeperhost.soulshardsrespawn.item;

import net.creeperhost.soulshardsrespawn.SoulShards;
import net.creeperhost.soulshardsrespawn.api.ISoulWeapon;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;

public class ItemVileSword extends Item implements ISoulWeapon
{
    public static final ToolMaterial MATERIAL_VILE = new ToolMaterial(BlockTags.INCORRECT_FOR_IRON_TOOL, 250, 6.0F, 2.0F, 14, SoulShards.CORRUPTED_INGOT);

    public ItemVileSword(Properties properties) {
        super(properties.sword(MATERIAL_VILE, 3, -2.4F));
    }

    @Override
    public int getSoulBonus(ItemStack stack, Player player, LivingEntity killedEntity) {
        return 1;
    }
}
