package net.creeperhost.soulshardsrespawn.core;

import net.creeperhost.soulshardsrespawn.SoulShards;
import net.creeperhost.soulshardsrespawn.block.BlockSoulCage;
import net.creeperhost.soulshardsrespawn.block.TileEntitySoulCage;
import net.creeperhost.soulshardsrespawn.core.data.Tier;
import net.creeperhost.soulshardsrespawn.core.util.EnchantmentSoulStealer;
import net.creeperhost.soulshardsrespawn.item.ItemSoulShard;
import net.creeperhost.soulshardsrespawn.item.ItemVileSword;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = SoulShards.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(SoulShards.MODID)
public class RegistrarSoulShards
{
    @ObjectHolder("soul_cage")
    public static Block SOUL_CAGE;

    @ObjectHolder("soul_cage")
    public static BlockEntityType<TileEntitySoulCage> SOUL_CAGE_TE;

    @ObjectHolder("soul_shard")
    public static Item SOUL_SHARD;
    @ObjectHolder("vile_sword")
    public static Item VILE_SWORD;
    @ObjectHolder("corrupted_essence")
    public static Item CORRUPTED_ESSENCE;
    @ObjectHolder("corrupted_ingot")
    public static Item CORRUPTED_INGOT;
    @ObjectHolder("vile_dust")
    public static Item VILE_DUST;

    public static final Enchantment SOUL_STEALER = Enchantments.INFINITY_ARROWS;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().registerAll(new BlockSoulCage().setRegistryName("soul_cage"));
    }

    @SubscribeEvent
    public static void registerTileEntities(RegistryEvent.Register<BlockEntityType<?>> event)
    {
        event.getRegistry().register(BlockEntityType.Builder.of(TileEntitySoulCage::new, SOUL_CAGE).build(null).setRegistryName(SoulShards.MODID, "soul_cage"));
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        Tier.readTiers();
        event.getRegistry().register(new BlockItem(SOUL_CAGE, new Item.Properties().tab(SoulShards.TAB_SS)).setRegistryName("soul_cage"));
        event.getRegistry().register(new ItemVileSword().setRegistryName("vile_sword"));
        event.getRegistry().register(new ItemSoulShard().setRegistryName("soul_shard"));
        event.getRegistry().register(new Item(new Item.Properties().tab(SoulShards.TAB_SS)).setRegistryName("corrupted_essence"));
        event.getRegistry().register(new Item(new Item.Properties().tab(SoulShards.TAB_SS)).setRegistryName("corrupted_ingot"));
        event.getRegistry().register(new Item(new Item.Properties().tab(SoulShards.TAB_SS)).setRegistryName("vile_dust"));
    }

    @SubscribeEvent
    public static void registerEnchantments(RegistryEvent.Register<Enchantment> event)
    {
        event.getRegistry().registerAll(new EnchantmentSoulStealer().setRegistryName("soul_stealer"));
    }
}
