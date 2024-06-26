package com.goldfinch.configs.serialization.minecraft;

import com.goldfinch.configs.serialization.Serializer;
import com.goldfinch.configs.serialization.TypeReference;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class WorldSerializer implements Serializer<World, String> {

    @Override
    public World deserialize(TypeReference typeReference, String serialized) {
        final World world = Bukkit.getWorld(serialized);

        if (world == null) {
            throw new IllegalArgumentException("Failed to deserialize world with name " + serialized + " as it does not exist");
        }

        return world;
    }

    @Override
    public String serialize(TypeReference typeReference, World world) {
        return world.getName();
    }


}
