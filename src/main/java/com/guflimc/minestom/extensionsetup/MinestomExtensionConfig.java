package com.guflimc.minestom.extensionsetup;

import groovy.lang.Closure;
import kotlin.Unit;
import org.gradle.api.Action;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Project;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;

import java.util.HashSet;
import java.util.Set;

public class MinestomExtensionConfig {

    @Input
    public String name;

    @Input
    public String version;

    @Input
    public String entrypoint;

    @Input @Optional
    public String[] authors;

    @Input @Optional
    public String[] dependencies;

    @Input @Optional
    final ExternalDependencies externalDependencies;

    private transient final Project project;
    public MinestomExtensionConfig(Project project) {
        this.project = project;
        this.externalDependencies = new ExternalDependencies(project);
    }

    @Input @Optional
    public void externalDependencies(Closure<Unit> closure) {
        project.configure(externalDependencies, closure);
    }

    @Input @Optional
    public void externalDependencies(Action<ExternalDependencies> action) {
        action.execute(externalDependencies);
    }

    public static final class ExternalDependencies {

        @Input @Optional
        final NamedDomainObjectContainer<Repository> repositories;

        @Input @Optional
        public Set<String> artifacts = new HashSet<>();

        public ExternalDependencies(Project project) {
            this.repositories = project.container(Repository.class);
        }

        @Input @Optional
        public void repositories(Closure<Unit> closure) {
            repositories.configure(closure);
        }

        @Input @Optional
        public void repositories(Action<Repository> action) {
            repositories.configureEach(action);
        }

        public static class Repository {

            @Input
            public String name;

            @Input
            public String url;

            public Repository() {}

            public Repository(String name) {
                this.name = name;
            }

            public Repository(String name, String url) {
                this.name = name;
                this.url = url;
            }
        }

    }

}
