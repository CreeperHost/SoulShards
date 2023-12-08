package net.creeperhost.soulshardsrespawn.compat.jei;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.ModIds;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.creeperhost.soulshardsrespawn.SoulShards;
import net.creeperhost.soulshardsrespawn.core.RegistrarSoulShards;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MultiblockCategory implements IRecipeCategory<MultiblockCategory.Recipe>
{
    public static final Component TITLE = Component.literal("Soul Shard Crafting");

    public static final RecipeType<MultiblockCategory.Recipe> SOUL_SHARD_CRAFTING =
            RecipeType.create(SoulShards.MODID, "soulshardcrafting", MultiblockCategory.Recipe.class);

    private final IGuiHelper iGuiHelper;
    private final IDrawable multiblock;
    private final IDrawable arrowRight;

    public MultiblockCategory(IGuiHelper iGuiHelper)
    {
        this.iGuiHelper = iGuiHelper;

        IDrawable obsidian = iGuiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Blocks.OBSIDIAN));
        IDrawable quartz = iGuiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Blocks.QUARTZ_BLOCK));
        IDrawable glowstone = iGuiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Blocks.GLOWSTONE));

        multiblock = new MultiBlockDrawable(obsidian, glowstone, quartz);
        arrowRight = iGuiHelper.drawableBuilder(new ResourceLocation("minecraft", "textures/gui/container/furnace.png"), 176, 14, 24, 17).build();
    }

    @Override
    public @NotNull RecipeType<Recipe> getRecipeType()
    {
        return SOUL_SHARD_CRAFTING;
    }

    @Override
    public @NotNull Component getTitle()
    {
        return TITLE;
    }

    @Override
    public @NotNull IDrawable getBackground()
    {
        return iGuiHelper.createBlankDrawable(114, 131);
    }

    @Override
    public @NotNull IDrawable getIcon()
    {
        return iGuiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(RegistrarSoulShards.SOUL_SHARD.get()));
    }

    @Override
    public void draw(Recipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY)
    {
        RenderSystem.enableBlend();

        multiblock.draw(guiGraphics, 10, 30);

        arrowRight.draw(guiGraphics, 60, 30);
        Font font = Minecraft.getInstance().font;
        guiGraphics.drawString(font, "Right-click on the top", 2, 70, 0, false);
        guiGraphics.drawString(font, "of the " + ChatFormatting.DARK_PURPLE + "Glowstone Block", 2, 80, 0, false);
        guiGraphics.drawString(font, "with a " + ChatFormatting.DARK_PURPLE + recipe.getInput().getHoverName().getString() + ChatFormatting.BLACK + " in hand", 2, 90, 0, false);

        RenderSystem.disableBlend();
    }

    @Override
    public @NotNull List<Component> getTooltipStrings(@NotNull Recipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY)
    {
        List<Component> components = new ArrayList<>();
        if(isInRect(10, 30, 40, 36, mouseX, mouseY))
        {
            components.add(Component.literal("4x " + Blocks.OBSIDIAN.getName().getString()));
            components.add(Component.literal("4x " + Blocks.QUARTZ_BLOCK.getName().getString()));
            components.add(Component.literal("1x " + Blocks.GLOWSTONE.getName().getString()));
        }

        return components;
    }

    public boolean isInRect(int x, int y, int xSize, int ySize, double mouseX, double mouseY)
    {
        return ((mouseX >= x && mouseX <= x + xSize) && (mouseY >= y && mouseY <= y + ySize));
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull Recipe recipe, @NotNull IFocusGroup focuses)
    {
        builder.addSlot(RecipeIngredientRole.INPUT, 23, 1)
                .addItemStack(recipe.getInput());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 90, 30)
                .addItemStack(recipe.getOutput());
    }

    public static class Recipe
    {
        private final ItemStack input;
        private final ItemStack output;

        public Recipe(ItemStack input, ItemStack output)
        {
            this.input = input;
            this.output = output;
        }

        public ItemStack getInput()
        {
            return input;
        }

        public ItemStack getOutput()
        {
            return output;
        }
    }
}
