package net.creeperhost.soulshardsrespawn.compat.rei;

import com.google.common.collect.Lists;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.WidgetWithBounds;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.creeperhost.soulshardsrespawn.core.RegistrarSoulShards;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.world.level.block.Blocks;

import java.util.Collections;
import java.util.List;


/**
 * Created by brandon3055 on 19/01/2024
 */
public class MultiblockCategory implements DisplayCategory<MultiblockDisplay> {

    @Override
    public CategoryIdentifier<? extends MultiblockDisplay> getCategoryIdentifier() {
        return SoulShardsREIPlugin.MULTIBLOCK;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.soulshards.soul_shard.title");
    }

    @Override
    public int getDisplayHeight() {
        return 120;
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(RegistrarSoulShards.SOUL_SHARD.get());
    }

    @Override
    public List<Widget> setupDisplay(MultiblockDisplay display, Rectangle bounds) {
        List<Widget> widgets = Lists.newArrayList();
        widgets.add(Widgets.createRecipeBase(bounds));

        Point pos = new Point(bounds.x + 15, bounds.y + 10);

        widgets.add(Widgets.createSlot(new Point(pos.x + 23, pos.y + 1))
                .disableBackground()
                .markInput()
                .entries(display.getInputEntries().get(0)));

        widgets.add(Widgets.createSlot(new Point(pos.x + 90, pos.y + 30))
                .disableBackground()
                .markOutput()
                .entries(display.getOutputEntries().get(0)));

        widgets.add(Widgets.createArrow(new Point(pos.x + 60, pos.y + 30)));

        List<FormattedText> craftLabel = Minecraft.getInstance().font.getSplitter().splitLines(Component.translatable("jei.soulshards.soul_shard.creation", ChatFormatting.DARK_PURPLE + display.getInputEntries().get(0).get(0).asFormattedText().getString() + ChatFormatting.RESET), bounds.width, Style.EMPTY);

        int y = 70;
        for (FormattedText line : craftLabel) {
            widgets.add(Widgets.createLabel(new Point(bounds.x + (bounds.width / 2), pos.y + y), Component.literal(line.getString()))
                    .noShadow()
                    .color(0)
            );
            y += 10;
        }

        pos.x += 10;
        pos.y += 30;

        MultiblockWidget mbw = new MultiblockWidget(display, new Rectangle(pos.x, pos.y, 40, 30));
        widgets.add(mbw);
        widgets.add(Widgets.createTooltip(mbw.getBounds(),
                Component.literal("4x " + Blocks.OBSIDIAN.getName().getString()),
                Component.literal("4x " + Blocks.QUARTZ_BLOCK.getName().getString()),
                Component.literal("1x " + Blocks.GLOWSTONE.getName().getString())
        ));

        return widgets;
    }

    private static class MultiblockWidget extends WidgetWithBounds {
        private final MultiblockDisplay display;
        private final Rectangle bounds;

        public MultiblockWidget(MultiblockDisplay display, Rectangle bounds) {
            this.display = display;
            this.bounds = bounds;
        }

        @Override
        public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
            int xOffset = bounds.x;
            int yOffset = bounds.y;
            EntryIngredient corner = display.getInputEntries().get(1);
            EntryIngredient middle = display.getInputEntries().get(2);
            EntryIngredient center = display.getInputEntries().get(3);


            graphics.pose().pushPose();
            graphics.pose().translate(0, 0, 100);

            corner.get(0).render(graphics, new Rectangle(xOffset + 13, yOffset + 1, 16, 16), mouseX, mouseY, delta);

            graphics.pose().translate(0, 0, 5);
            middle.get(0).render(graphics, new Rectangle(xOffset + 20, yOffset + 4, 16, 16), mouseX, mouseY, delta);
            middle.get(0).render(graphics, new Rectangle(xOffset + 7, yOffset + 4, 16, 16), mouseX, mouseY, delta);

            graphics.pose().translate(0, 0, 5);
            center.get(0).render(graphics, new Rectangle(xOffset + 13, yOffset + 8, 16, 16), mouseX, mouseY, delta);
            corner.get(0).render(graphics, new Rectangle(xOffset + 27, yOffset + 8, 16, 16), mouseX, mouseY, delta);
            corner.get(0).render(graphics, new Rectangle(xOffset + 0, yOffset + 8, 16, 16), mouseX, mouseY, delta);

            graphics.pose().translate(0, 0, 5);
            middle.get(0).render(graphics, new Rectangle(xOffset + 7, yOffset + 12, 16, 16), mouseX, mouseY, delta);
            middle.get(0).render(graphics, new Rectangle(xOffset + 20, yOffset + 12, 16, 16), mouseX, mouseY, delta);

            graphics.pose().translate(0, 0, 5);
            corner.get(0).render(graphics, new Rectangle(xOffset + 14, yOffset + 15, 16, 16), mouseX, mouseY, delta);
            graphics.pose().popPose();
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return Collections.emptyList();
        }

        @Override
        public Rectangle getBounds() {
            return bounds;
        }
    }
}