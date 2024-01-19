//package net.creeperhost.soulshardsrespawn.compat.jei;
//
//import mezz.jei.api.IModPlugin;
//import mezz.jei.api.JeiPlugin;
//import mezz.jei.api.helpers.IJeiHelpers;
//import mezz.jei.api.registration.IRecipeCatalystRegistration;
//import mezz.jei.api.registration.IRecipeCategoryRegistration;
//import mezz.jei.api.registration.IRecipeRegistration;
//import net.creeperhost.soulshardsrespawn.SoulShards;
//import net.creeperhost.soulshardsrespawn.core.RegistrarSoulShards;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.Items;
//import org.jetbrains.annotations.NotNull;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@JeiPlugin
//public class SoulShardsJeiPlugin implements IModPlugin
//{
//    private static final ResourceLocation ID = new ResourceLocation(SoulShards.MODID, "jei_plugin");
//
//    @Override
//    public void registerCategories(@NotNull IRecipeCategoryRegistration registration)
//    {
//        IJeiHelpers iJeiHelpers = registration.getJeiHelpers();
//        registration.addRecipeCategories(new MultiblockCategory(iJeiHelpers.getGuiHelper()));
//    }
//
//    @Override
//    public void registerRecipeCatalysts(@NotNull IRecipeCatalystRegistration registration)
//    {
//        registration.addRecipeCatalyst(new ItemStack(RegistrarSoulShards.SOUL_SHARD.get()), MultiblockCategory.SOUL_SHARD_CRAFTING);
//    }
//
//    @Override
//    public void registerRecipes(@NotNull IRecipeRegistration registration)
//    {
//        List<MultiblockCategory.Recipe> list = new ArrayList<>();
//        list.add(new MultiblockCategory.Recipe(new ItemStack(Items.DIAMOND), new ItemStack(RegistrarSoulShards.SOUL_SHARD.get())));
//
//        registration.addRecipes(MultiblockCategory.SOUL_SHARD_CRAFTING, list);
//    }
//
//    @Override
//    public @NotNull ResourceLocation getPluginUid()
//    {
//        return ID;
//    }
//}
