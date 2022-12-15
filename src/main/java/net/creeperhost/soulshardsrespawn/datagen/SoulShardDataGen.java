package net.creeperhost.soulshardsrespawn.datagen;

import net.creeperhost.soulshardsrespawn.SoulShards;
import net.creeperhost.soulshardsrespawn.core.RegistrarSoulShards;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;
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
        if(event.includeClient()) registerClientProviders(event.getGenerator());
    }

    public static void registerServerProviders(DataGenerator generator)
    {

    }

    public static void registerClientProviders(DataGenerator generator)
    {
        generator.addProvider(true, new GeneratorLanguage(generator));
    }

    public static class GeneratorLanguage extends LanguageProvider
    {

        public GeneratorLanguage(DataGenerator gen)
        {
            super(gen, SoulShards.MODID, "en_us");
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

            add("tooltip.soulshards.bound", "Bound to: %s");
            add("tooltip.soulshards.kills", "Souls: %d");
            add("tooltip.soulshards.tier", "Tier: %d");
            add("tooltip.soulshards.owne", "Owner: %s");
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
        }
    }
}