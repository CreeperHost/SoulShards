package net.creeperhost.soulshardsrespawn.core;

import dev.architectury.registry.registries.RegistrySupplier;
import net.creeperhost.soulshardsrespawn.SoulShards;
import net.creeperhost.soulshardsrespawn.block.BlockSoulCage;
import net.creeperhost.soulshardsrespawn.block.TileEntitySoulCage;
import net.creeperhost.soulshardsrespawn.core.util.EnchantmentSoulStealer;
import net.creeperhost.soulshardsrespawn.item.ItemSoulShard;
import net.creeperhost.soulshardsrespawn.item.ItemVileSword;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.checkerframework.checker.units.qual.C;

public class RegistrarSoulShards
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SoulShards.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, SoulShards.MODID);

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SoulShards.MODID);

    public static final RegistryObject<CreativeModeTab> TAB = CREATIVE_TAB.register(SoulShards.MODID, () -> CreativeModeTab
            .builder(CreativeModeTab.Row.TOP, 0)
            .displayItems(
            (itemDisplayParameters, output) -> {
                ITEMS.getEntries().forEach(e -> output.accept(e.get()));
            }
    )
            .icon(() ->  new ItemStack(RegistrarSoulShards.SOUL_SHARD.get()))
            .title(Component.translatable("itemGroup.soulshards"))
            .build());
    public static final DeferredRegister<BlockEntityType<?>> TILES_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, SoulShards.MODID);
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, SoulShards.MODID);

    public static final RegistryObject<Item> VILE_DUST = ITEMS.register("vile_dust", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> VILE_SWORD = ITEMS.register("vile_sword", () -> new ItemVileSword());
    public static final RegistryObject<Item> CORRUPTED_ESSENCE = ITEMS.register("corrupted_essence", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CORRUPTED_INGOT = ITEMS.register("corrupted_ingot", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SOUL_SHARD = ITEMS.register("soul_shard", () -> new ItemSoulShard());

    public static final RegistryObject<Block> SOUL_CAGE = BLOCKS.register("soul_cage", () -> new BlockSoulCage());
    public static final RegistryObject<Item> SOUL_CAGE_ITEM = ITEMS.register("soul_cage", () -> new BlockItem(SOUL_CAGE.get(), new Item.Properties()));

    public static final RegistryObject<BlockEntityType<TileEntitySoulCage>> SOUL_CAGE_TE =
            TILES_ENTITIES.register("soul_cage", () -> BlockEntityType.Builder.of(TileEntitySoulCage::new, SOUL_CAGE.get()).build(null));

    public static final RegistryObject<Enchantment> SOUL_STEALER = ENCHANTMENTS.register("soul_stealer", () -> new EnchantmentSoulStealer());
}
