package net.creeperhost.soulshardsrespawn.datagen;

import net.creeperhost.soulshardsrespawn.SoulShards;
import net.creeperhost.soulshardsrespawn.core.RegistrarSoulShards;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = SoulShards.MODID, bus = EventBusSubscriber.Bus.MOD)
public class SoulShardDataGen
{
    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        registerProviders(event.getGenerator(), event.getLookupProvider());
    }

    public static void registerProviders(DataGenerator generator, CompletableFuture<HolderLookup.Provider> lookupProvider)
    {
        generator.addProvider(true, new GeneratorRecipes.Runner(generator.getPackOutput(), lookupProvider));

        GeneratorBlockTags blockTags = new GeneratorBlockTags(generator.getPackOutput(), lookupProvider, SoulShards.MODID);
        generator.addProvider(true, blockTags);

        GeneratorItemTags itemTags = new GeneratorItemTags(generator.getPackOutput(), lookupProvider, blockTags.contentsGetter());
        generator.addProvider(true, itemTags);

        //TODO, not sure why this is broken, so for now the tag has been added manually.
//        generator.addProvider(true, new GeneratorEnchantmentTags(generator.getPackOutput(), lookupProvider, SoulShards.MODID));

        generator.addProvider(true, new GeneratorEntityTags(generator.getPackOutput(), lookupProvider));
        generator.addProvider(true, new LootTableProvider(generator.getPackOutput(), Collections.emptySet(),
                List.of(new LootTableProvider.SubProviderEntry(GeneratorBlockLoot::new, LootContextParamSets.BLOCK)), lookupProvider));

        generator.addProvider(true, new GeneratorLanguage(generator));
        //TODO, not sure whats up with this either. Seems to be exploding due to the vanilla models being registered in the background, and I cant seem to prevent that.
//        generator.addProvider(true, new GeneratorModels(generator.getPackOutput()));
    }



    public static class GeneratorLanguage extends LanguageProvider
    {
        public GeneratorLanguage(DataGenerator gen)
        {
            super(gen.getPackOutput(), SoulShards.MODID, "en_us");
        }

        @Override
        protected void addTranslations()
        {
            add("itemGroup.soulshards", "Soul Shards Respawn");

            add(RegistrarSoulShards.SOUL_SHARD.get(), "Soul Shard");
            add("item.soulshards.soul_shard_unbound", "Unbound Soul Shard");
            add(RegistrarSoulShards.CORRUPTED_INGOT.get(), "Corrupted Ingot");
            add(RegistrarSoulShards.CORRUPTED_ESSENCE.get(), "Corrupted Essence");
            add(RegistrarSoulShards.VILE_DUST.get(), "Vile Dust");
            add(RegistrarSoulShards.VILE_SWORD.get(), "Vile Sword");

            add(RegistrarSoulShards.SOUL_CAGE.get(), "Soul Cage");
            add(RegistrarSoulShards.SOUL_CAGE_ITEM.get(), "Soul Cage");

            add("tooltip.soulshards.bound", "Bound to: %s");
            add("tooltip.soulshards.kills", "Souls: %d");
            add("tooltip.soulshards.tier", "Tier: %d");
            add("tooltip.soulshards.owner", "Owner: %s");
            add("tooltip.soulshards.cage_born", "Cage Born");

            add("enchantment.soulshards.soul_stealer", "Soul Stealer");

            add("chat.soulshards.absorb_disabled", "§cThe shard seems to refuse the trapped soul...");
            add("chat.soulshards.command_killed", "§2Killed §2%d §2entities");
            add("chat.soulshards.command_not_found", "§cDid not find any spawned entities");

            add("commands.soulshards.usage", "/soulshards [setEnt|setKills|setOwner|killall]");
            add("commands.soulshards.set_ent.usage", "/soulshards setEnt <entityId>");
            add("commands.soulshards.set_ent.success", "Set bound entity of held shard to %s.");
            add("commands.soulshards.set_kills.usage", "/soulshards setKills <killCount>");
            add("commands.soulshards.set_kills.success", "Set kill count of held shard to %d.");
            add("commands.soulshards.set_owner.usage", "/soulshards setOwner [owner]");
            add("commands.soulshards.set_owner.success", "Set owner of held shard to %s.");
            add("commands.soulshards.kill_all.usage", "/soulshards killall");
            add("commands.soulshards.kill_all.success", "Killed %d cage born entities.");
            add("commands.soulshards.error.not_a_shard", "Held item is not a Soul Shard.");
            add("commands.soulshards.error.not_a_entity", "%s is invalid or not registered.");

            add("enchantment.soulshards.soul_stealer.desc", "Increases the souls collected per kill by one per level.");
            add("jei.soulshards.soul_shard.desc", "A vessel for the Souls of the slain.\\n\\nObtained by creating a structure in the world and using a %s on the origin block.");
            add("jei.soulshards.soul_shard.title", "Soul Shard Crafting");
            add("jei.soulshards.soul_shard.creation", "Right-click on the top\nof the §5Glowstone Block§r\nwith a %s in hand");
            add("jei.soulshards.soul_shard.multiblock", "4x Obsidian\n4x Block of Quartz\n1x Glowstone");

            add("config.jade.plugin_soulshards.cage_data", "Soul Cage Data");
            add("config.jade.plugin_soulshards.entity_data", "Soul Cage Entity");
        }
    }
}
