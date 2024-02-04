package ru.artorium.configs.serialization;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import ru.artorium.configs.Utils;
import ru.artorium.configs.serialization.collections.ArraySerializer;
import ru.artorium.configs.serialization.collections.CollectionSerializer;
import ru.artorium.configs.serialization.collections.MapSerializer;
import ru.artorium.configs.serialization.minecraft.ComponentSerializer;
import ru.artorium.configs.serialization.minecraft.ItemStackSerializer;
import ru.artorium.configs.serialization.minecraft.LocationSerializer;
import ru.artorium.configs.serialization.minecraft.WorldSerializer;
import ru.artorium.configs.serialization.other.EnumSerializer;
import ru.artorium.configs.serialization.other.ObjectSerializer;
import ru.artorium.configs.serialization.primitives.DoubleSerializer;
import ru.artorium.configs.serialization.primitives.FloatSerializer;
import ru.artorium.configs.serialization.primitives.IntegerSerializer;
import ru.artorium.configs.serialization.primitives.LongSerializer;
import ru.artorium.configs.serialization.primitives.StringSerializer;

@Getter
@AllArgsConstructor
public enum SerializerType {
    STRING(String.class, String.class, new StringSerializer()),
    INTEGER(int.class, String.class, new IntegerSerializer()),
    DOUBLE(double.class, String.class, new DoubleSerializer()),
    LONG(long.class, String.class, new LongSerializer()),
    FLOAT(float.class, String.class, new FloatSerializer()),
    ENUM(Enum.class, String.class, new EnumSerializer()),
    ITEMSTACK(ItemStack.class, JSONObject.class, new ItemStackSerializer()),
    LOCATION(Location.class, JSONObject.class, new LocationSerializer()),
    WORLD(World.class, String.class, new WorldSerializer()),
    COMPONENT(Component.class, String.class, new ComponentSerializer()),
    MAP(Map.class, JSONObject.class, new MapSerializer()),
    COLLECTION(Collection.class, JSONArray.class, new CollectionSerializer()),
    ARRAY(Object[].class, JSONArray.class, new ArraySerializer()),
    OBJECT(Object.class, JSONObject.class, new ObjectSerializer());

    private final Class<?> from;
    private final Class<?> to;
    private final Serializer serializer;

    public static SerializerType getByClass(Class<?> fieldClass) {
        final Class<?> finalFieldClass = Utils.getActualClass(fieldClass);
        return Arrays.stream(SerializerType.values())
            .filter(type -> type.getFrom().equals(finalFieldClass))
            .findFirst()
            .orElse(SerializerType.OBJECT);
    }

}