package net.creeperhost.soulshardsrespawn.datagen;

import net.creeperhost.soulshardsrespawn.SoulShards;
import net.creeperhost.soulshardsrespawn.core.RegistrarSoulShards;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class GeneratorRecipes extends RecipeProvider
{
    public GeneratorRecipes(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> providerCompletableFuture)
    {
        super(packOutput, providerCompletableFuture);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput consumer)
    {
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(RegistrarSoulShards.VILE_DUST.get()), RecipeCategory.MISC,
                        Items.SOUL_SAND, 1.0f, 200)
                .unlockedBy("has_soul_sand", inventoryTrigger(ItemPredicate.Builder.item().of(Items.SOUL_SAND).build()))
                .save(consumer, ResourceLocation.fromNamespaceAndPath(SoulShards.MODID, "vile_dust_from_soul_sand"));

        SimpleCookingRecipeBuilder.smelting(Ingredient.of(RegistrarSoulShards.VILE_DUST.get()), RecipeCategory.MISC,
                        Items.SOUL_SOIL, 1.0f, 200)
                .unlockedBy("has_soul_soil", inventoryTrigger(ItemPredicate.Builder.item().of(Items.SOUL_SOIL).build()))
                .save(consumer, ResourceLocation.fromNamespaceAndPath(SoulShards.MODID, "vile_dust_from_soul_soil"));


        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistrarSoulShards.VILE_SWORD.get())
                .pattern(" I ")
                .pattern(" I ")
                .pattern(" S ")
                .define('I', RegistrarSoulShards.CORRUPTED_INGOT.get())
                .define('S', Items.STICK)
                .group("soulshards")
                .unlockedBy("has_vile_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(RegistrarSoulShards.CORRUPTED_INGOT.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistrarSoulShards.SOUL_CAGE.get())
                .pattern("IBI")
                .pattern("B B")
                .pattern("IBI")
                .define('I', RegistrarSoulShards.CORRUPTED_INGOT.get())
                .define('B', Items.IRON_BARS)
                .group("soulshards")
                .unlockedBy("has_vile_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(RegistrarSoulShards.CORRUPTED_INGOT.get()))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, RegistrarSoulShards.CORRUPTED_ESSENCE.get())
                .requires(Items.LAPIS_LAZULI)
                .requires(Items.REDSTONE)
                .requires(Items.OBSIDIAN)
                .requires(Items.OBSIDIAN)
                .group("soulshards")
                .unlockedBy("has_obsidian", InventoryChangeTrigger.TriggerInstance.hasItems(Items.OBSIDIAN))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RegistrarSoulShards.CORRUPTED_INGOT.get())
                .pattern("CVC")
                .pattern("VIV")
                .pattern("CVC")
                .define('C', RegistrarSoulShards.CORRUPTED_ESSENCE.get())
                .define('V', RegistrarSoulShards.VILE_DUST.get())
                .define('I', Items.IRON_INGOT)
                .group("soulshards")
                .unlockedBy("has_vile_dust", InventoryChangeTrigger.TriggerInstance.hasItems(RegistrarSoulShards.VILE_DUST.get()))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, RegistrarSoulShards.SOUL_SHARD.get())
                .requires(RegistrarSoulShards.SOUL_SHARD.get())
                .group("soulshards")
                .unlockedBy("has_soulshard", InventoryChangeTrigger.TriggerInstance.hasItems(RegistrarSoulShards.SOUL_SHARD.get()))
                .save(consumer);
    }
}
