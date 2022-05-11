package com.guflimc.minestom.extensionsetup;

import com.google.gson.*;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GenerateExtensionFileTask extends DefaultTask {

    private final static Gson gson = new Gson();

    public Property<String> filename = getProject().getObjects().property(String.class);

    public DirectoryProperty outputDirectory = getProject().getObjects().directoryProperty();

    public Property<MinestomExtensionConfig> config = getProject().getObjects().property(MinestomExtensionConfig.class);

    @TaskAction
    public void generate() {
        File file = outputDirectory.file(filename).get().getAsFile();
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                FileWriter writer = new FileWriter(file)
        ) {
            gson.toJson(config.get(), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
