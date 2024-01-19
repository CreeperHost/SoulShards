package net.creeperhost.soulshardsrespawn.compat.rei;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;

import java.util.List;

/**
 * Created by brandon3055 on 19/01/2024
 */
public class MultiblockDisplay extends BasicDisplay {

    public MultiblockDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs) {
        super(inputs, outputs);
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return SoulShardsREIPlugin.MULTIBLOCK;
    }

}
