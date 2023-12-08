package net.creeperhost.soulshardsrespawn.compat.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.drawable.IDrawable;

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
    public void draw(PoseStack ms, int xOffset, int yOffset)
    {
        ms.pushPose();
        ms.translate(0, 0, 100);
        cornerBlock.draw(ms, xOffset + 13, yOffset + 1);

        ms.translate(0, 0, 5);
        middleBlock.draw(ms, xOffset + 20, yOffset + 4);
        middleBlock.draw(ms, xOffset + 7, yOffset + 4);

        ms.translate(0, 0, 5);
        centerBlock.draw(ms, xOffset + 13, yOffset + 8);
        cornerBlock.draw(ms, xOffset + 27, yOffset + 8);
        cornerBlock.draw(ms, xOffset, yOffset + 8);

        ms.translate(0, 0, 5);
        middleBlock.draw(ms, xOffset + 7, yOffset + 12);
        middleBlock.draw(ms, xOffset + 20, yOffset + 12);

        ms.translate(0, 0, 5);
        cornerBlock.draw(ms, xOffset + 14, yOffset + 15);
        ms.popPose();
    }
}
