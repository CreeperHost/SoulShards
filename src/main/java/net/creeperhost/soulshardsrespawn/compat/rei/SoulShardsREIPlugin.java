package net.creeperhost.soulshardsrespawn.compat.rei;

import com.google.common.collect.Lists;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.forge.REIPluginClient;
import net.creeperhost.soulshardsrespawn.SoulShards;
import net.creeperhost.soulshardsrespawn.core.RegistrarSoulShards;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.Collections;

/**
 * Created by brandon3055 on 19/01/2024
 */
@REIPluginClient
public class SoulShardsREIPlugin implements REIClientPlugin {
    public static final CategoryIdentifier<MultiblockDisplay> MULTIBLOCK = CategoryIdentifier.of(SoulShards.MODID, "multiblock");

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.configure(MULTIBLOCK, config -> config.setQuickCraftingEnabledByDefault(false));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        CategoryRegistry.getInstance().add(new MultiblockCategory());
        DisplayRegistry.getInstance().add(new MultiblockDisplay(
                Lists.newArrayList(EntryIngredients.of(Items.DIAMOND), EntryIngredients.of(Blocks.OBSIDIAN), EntryIngredients.of(Blocks.QUARTZ_BLOCK), EntryIngredients.of(Blocks.GLOWSTONE)),
                Collections.singletonList(EntryIngredients.of(RegistrarSoulShards.SOUL_SHARD.get()))
        ));

    }
}
