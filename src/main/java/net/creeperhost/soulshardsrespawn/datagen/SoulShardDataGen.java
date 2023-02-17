package net.creeperhost.soulshardsrespawn.datagen;

import net.creeperhost.soulshardsrespawn.SoulShards;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SoulShards.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SoulShardDataGen
{
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event)
    {
        if(event.includeServer()) registerServerProviders(event.getGenerator());
        if(event.includeClient()) registerClientProviders(event.getGenerator(), event.getExistingFileHelper());
    }

    public static void registerServerProviders(DataGenerator generator)
    {
        generator.addProvider(true, new LootTableGenerator(generator));
    }

    public static void registerClientProviders(DataGenerator generator, ExistingFileHelper existingFileHelper)
    {
        generator.addProvider(true, new LanguageGenerator(generator));
        generator.addProvider(true, new ItemModelGenerator(generator, existingFileHelper));
        generator.addProvider(true, new BlockStateGenerator(generator, existingFileHelper));
    }

}
