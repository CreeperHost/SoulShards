package net.creeperhost.soulshardsrespawn.datagen;

import net.creeperhost.soulshardsrespawn.SoulShards;
import net.creeperhost.soulshardsrespawn.core.RegistrarSoulShards;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;

import java.util.concurrent.CompletableFuture;

public class GeneratorRecipes extends RecipeProvider
{
    public GeneratorRecipes(HolderLookup.Provider provider, RecipeOutput output) {
        super(provider, output);
    }

    @Override
    protected void buildRecipes() {
        oreCooking(Items.SOUL_SAND, RecipeCategory.MISC, RegistrarSoulShards.VILE_DUST.get(), 1.0f, 200, SoulShards.MODID, "_from_soul_sand");
        oreCooking(Items.SOUL_SOIL, RecipeCategory.MISC, RegistrarSoulShards.VILE_DUST.get(), 1.0f, 200, SoulShards.MODID, "_from_soul_soil");

        shaped(RecipeCategory.MISC, RegistrarSoulShards.VILE_SWORD.get())
                .pattern(" I ")
                .pattern(" I ")
                .pattern(" S ")
                .define('I', RegistrarSoulShards.CORRUPTED_INGOT.get())
                .define('S', Items.STICK)
                .group("soulshards")
                .unlockedBy("has_vile_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(RegistrarSoulShards.CORRUPTED_INGOT.get()))
                .save(output);

        shaped(RecipeCategory.MISC, RegistrarSoulShards.SOUL_CAGE.get())
                .pattern("IBI")
                .pattern("B B")
                .pattern("IBI")
                .define('I', RegistrarSoulShards.CORRUPTED_INGOT.get())
                .define('B', Items.IRON_BARS)
                .group("soulshards")
                .unlockedBy("has_vile_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(RegistrarSoulShards.CORRUPTED_INGOT.get()))
                .save(output);

        shapeless(RecipeCategory.MISC, RegistrarSoulShards.CORRUPTED_ESSENCE.get())
                .requires(Items.LAPIS_LAZULI)
                .requires(Items.REDSTONE)
                .requires(Items.OBSIDIAN)
                .requires(Items.OBSIDIAN)
                .group("soulshards")
                .unlockedBy("has_obsidian", InventoryChangeTrigger.TriggerInstance.hasItems(Items.OBSIDIAN))
                .save(output);

        shaped(RecipeCategory.MISC, RegistrarSoulShards.CORRUPTED_INGOT.get())
                .pattern("CVC")
                .pattern("VIV")
                .pattern("CVC")
                .define('C', RegistrarSoulShards.CORRUPTED_ESSENCE.get())
                .define('V', RegistrarSoulShards.VILE_DUST.get())
                .define('I', Items.IRON_INGOT)
                .group("soulshards")
                .unlockedBy("has_vile_dust", InventoryChangeTrigger.TriggerInstance.hasItems(RegistrarSoulShards.VILE_DUST.get()))
                .save(output);

        shapeless(RecipeCategory.MISC, RegistrarSoulShards.SOUL_SHARD.get())
                .requires(RegistrarSoulShards.SOUL_SHARD.get())
                .group("soulshards")
                .unlockedBy("has_soulshard", InventoryChangeTrigger.TriggerInstance.hasItems(RegistrarSoulShards.SOUL_SHARD.get()))
                .save(output);
    }

    protected void oreCooking(ItemLike input, RecipeCategory category, ItemLike result, float experience, int cookingTime, String group, String suffix) {
        Identifier key = BuiltInRegistries.ITEM.getKey(result.asItem());
        ResourceKey<Recipe<?>> rskey = ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath("soulshards", "smelting/" + key.getPath() + suffix));
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(input), category, CookingBookCategory.MISC, result, experience, cookingTime)
                .group(group)
                .unlockedBy(getHasName(input), this.has(input))
                .save(this.output, rskey);
    }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput p_365442_, CompletableFuture<HolderLookup.Provider> p_362168_) {
            super(p_365442_, p_362168_);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider p_364945_, RecipeOutput p_362956_) {
            return new GeneratorRecipes(p_364945_, p_362956_);
        }

        @Override
        public String getName() {
            return "Soul Shards Recipes";
        }
    }
}
