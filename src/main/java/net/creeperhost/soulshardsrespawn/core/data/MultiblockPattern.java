package net.creeperhost.soulshardsrespawn.core.data;

import com.google.common.collect.Sets;
import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;

import javax.annotation.Nullable;
import java.awt.*;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

@JsonAdapter(MultiblockPattern.Serializer.class)
public class MultiblockPattern
{

    public static final MultiblockPattern DEFAULT = new MultiblockPattern(new ItemStack(Items.DIAMOND), new String[]{"OQO", "QGQ", "OQO"}, new Point(1, 1), new HashMap<>() {{
        put('O', new Slot(Blocks.OBSIDIAN));
        put('Q', new Slot(Blocks.QUARTZ_BLOCK));
        put('G', new Slot(Blocks.GLOWSTONE));
    }});

    private final ItemStack catalyst;
    private final String[] shape;
    private final Point origin;
    private final Map<Character, Slot> definition;

    public MultiblockPattern(ItemStack catalyst, String[] shape, Point origin, Map<Character, Slot> definition)
    {
        this.catalyst = catalyst;
        this.shape = shape;
        this.origin = origin;
        this.definition = definition;
        this.definition.put(' ', new Slot(Blocks.AIR.defaultBlockState()));

        char originChar = shape[origin.y].charAt(origin.x);
        if (originChar == ' ' || definition.get(originChar).test(Blocks.AIR.defaultBlockState()))
            throw new IllegalStateException("Origin point cannot be blank space.");

        int lineLength = shape[0].length();
        for (String line : shape)
        {
            if (line.length() != lineLength)
                throw new IllegalStateException("All lines in the shape must be the same size.");

            for (char letter : line.toCharArray())
                if (definition.get(letter) == null) throw new IllegalStateException(letter + " is not defined.");
        }
    }

    public ItemStack getCatalyst()
    {
        return catalyst;
    }

    @Nullable
    public Set<BlockPos> match(Level world, BlockPos originBlock)
    {
        Set<BlockPos> matched = Sets.newHashSet();
        for (int y = 0; y < shape.length; y++)
        {
            String line = shape[y];
            for (int x = 0; x < line.length(); x++)
            {
                BlockPos offset = originBlock.offset(x - origin.x, 0, y - origin.y);
                BlockState state = world.getBlockState(offset);
                if (!definition.get(line.charAt(x)).test(state)) {
                    return null;
                }

                matched.add(offset);
            }
        }

        return matched;
    }

    public boolean isOriginBlock(BlockState state)
    {
        Slot slot = definition.get(shape[origin.y].charAt(origin.x));
        return slot.test(state);
    }

    public static class Slot implements Predicate<BlockState>
    {

        @JsonAdapter(SerializerBlockState.class)
        private final Set<BlockState> blocks;

        public Slot(BlockState... states)
        {
            this.blocks = Sets.newHashSet(states);
        }

        public Slot(Block block)
        {
            this(block.getStateDefinition().getPossibleStates().toArray(new BlockState[0]));
        }

        @Override
        public boolean test(BlockState state)
        {
            return blocks.contains(state);
        }
    }

    public static class Serializer implements JsonDeserializer<MultiblockPattern>
    {
        @Override
        public MultiblockPattern deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
        {
            JsonObject json = element.getAsJsonObject();

            ResourceLocation itemId = ResourceLocation.parse(json.getAsJsonObject("catalyst").getAsJsonPrimitive("item").getAsString());
            ItemStack catalyst = new ItemStack(BuiltInRegistries.ITEM.getValue(itemId), 1);

            String[] shape = context.deserialize(json.getAsJsonArray("shape"), String[].class);
            Point origin = context.deserialize(json.getAsJsonObject("origin"), Point.class);
            Map<Character, Slot> definition = context.deserialize(json.getAsJsonObject("definition"), new TypeToken<Map<Character, Slot>>(){}.getType());

            return new MultiblockPattern(catalyst, shape, origin, definition);
        }
    }

    public static class SerializerBlockState implements JsonDeserializer<Set<BlockState>> {
        @Override
        public Set<BlockState> deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Set<BlockState> states = Sets.newHashSet();
            for (JsonElement entry : element.getAsJsonArray()) {
                if (!entry.isJsonPrimitive()) {
                    throw new JsonParseException("Found invalid block when parsing SoulShards multiblock file: " + entry + ", Expected String");
                } else {
                    for (BlockState possible : BuiltInRegistries.BLOCK.getValue(ResourceLocation.parse(entry.getAsString())).getStateDefinition().getPossibleStates()) {
                        if (possible.getBlock() instanceof SlabBlock && possible.getValue(SlabBlock.TYPE) != SlabType.DOUBLE) {
                            continue;
                        }
                        states.add(possible);
                    }
                }
            }

            return states;
        }
    }
}
