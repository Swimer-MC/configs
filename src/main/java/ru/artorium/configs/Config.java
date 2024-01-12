package ru.artorium.configs;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.artorium.configs.annotations.Ignore;
import ru.artorium.configs.formats.Format;
import ru.artorium.configs.formats.JSON;
import ru.artorium.configs.formats.YAML;
import ru.artorium.configs.serialization.Serializer;

@Getter
@NoArgsConstructor
public abstract class Config {

    private Map<String, Object> map;
    private File file;
    private Format format;

    public Config(String fileName, String path) {
        this.file = new File(path, fileName);
        this.format = fileName.endsWith(".yaml") || fileName.endsWith(".yml") ? new YAML() : new JSON();
        this.map = this.format.readFile(this.file);

        this.reload();
    }

    public void reload() {
        if (!this.file.exists()) {
            this.createFile();
        }

        this.fillMissingFields();

        this.getFields(this).forEach(field -> {
            try {
                field.set(this, Serializer.deserialize(field.getType(), field.getType(), this.map.get(field.getName())));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void createFile() {
        if (!this.file.getParentFile().exists()) {
            this.file.getParentFile().mkdirs();
        }

        try {
            this.file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void fillMissingFields() {
        final Config defaultConfig = this.getTemplate();

        this.getFields(this).forEach(field -> {
            this.map.computeIfAbsent(field.getName(), $ -> {
                try {
                    return Serializer.serialize(field.getType(), field.getType(), field.get(defaultConfig));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });

        });

        this.format.writeFile(this.file, this.map);
    }

    private Config getTemplate() {
        try {
            return this.getClass().getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            System.err.println("Cannot create an instance of " + this.getClass().getName() + " class, please check if it has a default no args constructor");
            throw new RuntimeException(e);
        }
    }

    private List<Field> getFields(Object object) {
        return Arrays.stream(object.getClass().getDeclaredFields())
            .filter(field -> !field.isAnnotationPresent(Ignore.class))
            .peek(field -> field.setAccessible(true))
            .toList();
    }

}
