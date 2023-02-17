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
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.*;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.server.command.TextComponentHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MultiblockCategory implements IRecipeCategory<MultiblockCategory.Recipe> {
    public static final RecipeType<MultiblockCategory.Recipe> SOUL_SHARD_CRAFTING =
            RecipeType.create(SoulShards.MODID, "soulshardcrafting", MultiblockCategory.Recipe.class);

    private final IGuiHelper iGuiHelper;
    private final IDrawable multiblock;
    private final IDrawable arrowRight;

    public MultiblockCategory(IGuiHelper iGuiHelper) {
        this.iGuiHelper = iGuiHelper;

        IDrawable obsidian = iGuiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Blocks.OBSIDIAN));
        IDrawable quartz = iGuiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Blocks.QUARTZ_BLOCK));
        IDrawable glowstone = iGuiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Blocks.GLOWSTONE));

        multiblock = new MultiBlockDrawable(obsidian, glowstone, quartz);
        arrowRight = iGuiHelper.drawableBuilder(new ResourceLocation(ModIds.JEI_ID, "textures/gui/gui_vanilla.png"), 82, 128, 24, 17).build();
    }

    @Override
    public @NotNull RecipeType<Recipe> getRecipeType() {
        return SOUL_SHARD_CRAFTING;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("jei.soulshards.soul_shard.title");
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return iGuiHelper.createBlankDrawable(114, 131);
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return iGuiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(RegistrarSoulShards.SOUL_SHARD.get()));
    }

    @Override
    public void draw(@NotNull Recipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull PoseStack stack, double mouseX, double mouseY) {
        RenderSystem.enableBlend();

        multiblock.draw(stack, 10, 30);
        arrowRight.draw(stack, 60, 30);

        Font font = Minecraft.getInstance().font;
        drawSplitString(font, Component.translatable("jei.soulshards.soul_shard.creation", ChatFormatting.DARK_PURPLE + recipe.getInput().getHoverName().getString()), stack, 0, 70, 114, 0);

        RenderSystem.disableBlend();
    }

    @Override
    public @NotNull List<Component> getTooltipStrings(@NotNull Recipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        List<Component> components = new ArrayList<>();
        if (isInRect(10, 30, 40, 36, mouseX, mouseY)) {
            Minecraft mc = Minecraft.getInstance();
            splitComponent(mc.font, Component.translatable("jei.soulshards.soul_shard.multiblock"), mc.getWindow().getGuiScaledWidth()).forEach(s -> components.add(Component.literal(s.getString())));
        }

        return components;
    }

    public boolean isInRect(int x, int y, int xSize, int ySize, double mouseX, double mouseY) {
        return ((mouseX >= x && mouseX <= x + xSize) && (mouseY >= y && mouseY <= y + ySize));
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull Recipe recipe, @NotNull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 23, 1)
                .addItemStack(recipe.getInput());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 90, 30)
                .addItemStack(recipe.getOutput());
    }

    private List<FormattedText> splitComponent(Font font, FormattedText text, int width) {
        return font.getSplitter().splitLines(text, width, Style.EMPTY);
    }

    public void drawSplitString(Font font, FormattedText text, PoseStack poseStack, float x, float y, int width, int colour) {
        for (FormattedText s : splitComponent(font, text, width)) {
            font.draw(poseStack, Language.getInstance().getVisualOrder(s), x, y, colour);
            y += font.lineHeight;
        }
    }

    public static class Recipe {
        private final ItemStack input;
        private final ItemStack output;

        public Recipe(ItemStack input, ItemStack output) {
            this.input = input;
            this.output = output;
        }

        public ItemStack getInput() {
            return input;
        }

        public ItemStack getOutput() {
            return output;
        }
    }
}
