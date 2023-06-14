package net.creeperhost.soulshardsrespawn.compat.jei;

import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.client.gui.GuiGraphics;

public class MultiBlockDrawable implements IDrawable
{
    private final IDrawable cornerBlock;
    private final IDrawable centerBlock;
    private final IDrawable middleBlock;

    public MultiBlockDrawable(IDrawable cornerBlock, IDrawable centerBlock, IDrawable middleBlock)
    {
        this.cornerBlock = cornerBlock;
        this.centerBlock = centerBlock;
        this.middleBlock = middleBlock;
    }

    @Override
    public int getWidth()
    {
        return 43;
    }

    @Override
    public int getHeight()
    {
        return 31;
    }


    @Override
    public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset)
    {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, -50);
        cornerBlock.draw(guiGraphics, xOffset + 13, yOffset + 1);

        guiGraphics.pose().translate(0, 0, 5);
        middleBlock.draw(guiGraphics, xOffset + 20, yOffset + 4);
        middleBlock.draw(guiGraphics, xOffset + 7, yOffset + 4);

        guiGraphics.pose().translate(0, 0, 5);
        centerBlock.draw(guiGraphics, xOffset + 13, yOffset + 8);
        cornerBlock.draw(guiGraphics, xOffset + 27, yOffset + 8);
        cornerBlock.draw(guiGraphics, xOffset, yOffset + 8);

        guiGraphics.pose().translate(0, 0, 5);
        middleBlock.draw(guiGraphics, xOffset + 7, yOffset + 12);
        middleBlock.draw(guiGraphics, xOffset + 20, yOffset + 12);

        guiGraphics.pose().translate(0, 0, 5);
        cornerBlock.draw(guiGraphics, xOffset + 14, yOffset + 15);
        guiGraphics.pose().popPose();
    }
}
