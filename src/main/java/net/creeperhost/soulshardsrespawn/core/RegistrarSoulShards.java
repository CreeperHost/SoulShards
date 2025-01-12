package net.creeperhost.soulshardsrespawn.core;

import net.creeperhost.soulshardsrespawn.SoulShards;
import net.creeperhost.soulshardsrespawn.block.BlockSoulCage;
import net.creeperhost.soulshardsrespawn.block.TileEntitySoulCage;
import net.creeperhost.soulshardsrespawn.item.ItemSoulShard;
import net.creeperhost.soulshardsrespawn.item.ItemVileSword;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RegistrarSoulShards
{
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SoulShards.MODID);
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SoulShards.MODID);

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SoulShards.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = CREATIVE_TAB.register(SoulShards.MODID, () ->
            CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0).displayItems((itemDisplayParameters, output) -> {
                ITEMS.getEntries().forEach(e -> output.accept(e.get()));
                ItemSoulShard itemSoulShard = (ItemSoulShard) RegistrarSoulShards.SOUL_SHARD.get();
                itemSoulShard.fillItemCategory().forEach(output::accept);
            }).icon(() ->  new ItemStack(RegistrarSoulShards.SOUL_SHARD.get())).title(Component.translatable("itemGroup.soulshards")).build());

    public static final DeferredRegister<BlockEntityType<?>> TILES_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, SoulShards.MODID);

    public static final DeferredHolder<Item, Item> VILE_DUST = ITEMS.registerItem("vile_dust", Item::new);
    public static final DeferredHolder<Item, Item> VILE_SWORD = ITEMS.registerItem("vile_sword", ItemVileSword::new);
    public static final DeferredHolder<Item, Item> CORRUPTED_ESSENCE = ITEMS.registerItem("corrupted_essence", Item::new);
    public static final DeferredHolder<Item, Item> CORRUPTED_INGOT = ITEMS.registerItem("corrupted_ingot", Item::new);
    public static final DeferredHolder<Item, Item> SOUL_SHARD = ITEMS.registerItem("soul_shard", ItemSoulShard::new);

    public static final DeferredHolder<Block, Block> SOUL_CAGE = BLOCKS.registerBlock("soul_cage", BlockSoulCage::new, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.0F).noOcclusion());
    public static final DeferredHolder<Item, Item> SOUL_CAGE_ITEM = ITEMS.registerItem("soul_cage", (props) -> new BlockItem(SOUL_CAGE.get(), props));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileEntitySoulCage>> SOUL_CAGE_TE = TILES_ENTITIES.register("soul_cage", () -> new BlockEntityType<>(TileEntitySoulCage::new, SOUL_CAGE.get()));

}
