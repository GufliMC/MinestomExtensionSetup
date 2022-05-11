package com.guflimc.minestom.extensionsetup;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;

public class MinestomExtensionSetupPlugin implements Plugin<Project> {

    private MinestomExtensionConfig config;

    private Configuration library;

    @Override
    public void apply(Project project) {

        // create config extension
        config = new MinestomExtensionConfig(project);
        project.getExtensions().add("minestomExtension", config);

        // create library configuration that acts the same as compileOnly but adds the dependencies to the extension.json
        var compileOnly = project.getConfigurations().getByName("compileOnly");
        library = project.getConfigurations().maybeCreate("library");
        compileOnly.extendsFrom(library);

        // generated output directory
        var output = project.getLayout().getBuildDirectory().dir("generated/minestom");

        var generateTask = project.getTasks().register("generateMinestomExtFile", GenerateExtensionFileTask.class, task -> {
            task.setGroup("minecraft");

            // defaults for task
            task.filename.set("extension.json");
            task.outputDirectory.set(output);

            // config defaults
            defaults(project);
            task.config.set(config);

            task.doFirst((t) -> {
                // validate config
                validate();
            });
        });

        // I don't know man, it works
        project.getPlugins().withType(JavaPlugin.class, javaPlugin -> {
            SourceSet main = project.getExtensions()
                    .getByType(SourceSetContainer.class)
                    .named(SourceSet.MAIN_SOURCE_SET_NAME)
                    .get();
            main.getJava().srcDir(generateTask);
            main.getResources().srcDir(output);
        });
    }

    private void defaults(Project project) {
        // set defaults
        if (config.name == null) {
            config.name = project.getName();
        }
        if (config.version == null) {
            config.version = project.getVersion().toString();
        }
        if (config.authors == null && project.hasProperty("author")) {
            config.authors = new String[]{project.findProject("author").toString()};
        }

        // EXTERNAL DEPENDENCIES

        // artifacts
        library.getResolvedConfiguration().getFirstLevelModuleDependencies().stream()
                .map(dep -> dep.getModule().getId().toString())
                .forEach(id -> config.externalDependencies.artifacts.add(id));

        // repositories
        // TODO, if only there was a way to only add the repositories that are used for the defined artifacts
        project.getRepositories().forEach(repo -> {
            if (repo instanceof MavenArtifactRepository mar) {
                config.externalDependencies.repositories.add(new MinestomExtensionConfig.ExternalDependencies
                        .Repository(mar.getName(), mar.getUrl().toString()));
            }
        });
    }

    private void validate() {
        if (config.name == null) {
            throw new IllegalStateException("Name is not set");
        }
        if (config.version == null) {
            throw new IllegalStateException("Version is not set");
        }
        if (config.entrypoint == null) {
            throw new IllegalStateException("Entrypoint is not set");
        }
    }

}
