# MinestomExtensionSetup

A gradle plugin that generates `extension.json` by a gradle configuration.

## Usage

### Gradle

#### Setup

Replace `+` with a specific version if you don't want to use the latest version.
```
plugins {
    id 'com.guflimc.minestom.extensionsetup' version '+'
}
```

Place this in the `settings.gradle` if you want snapshot versions.
```
pluginManagement {
    repositories {
        gradlePluginPortal()
        maven { url "https://repo.jorisg.com/snapshots" }
    }
}
```

#### Dependencies
`library` behaves the same as `compileOnly` and they will automatically be added to the `extension.json`.
```
dependencies {
    library 'com.guflimc.brick.scheduler:minestom-api:1.0-SNAPSHOT'
}
```

#### Configuration

You can set other values of the `extension.json` here. Only `entrypoint` is required. The rest is optional and will be automatically set based on your project.

```
minestomExtension {
    entrypoint = "com.guflimc.brick.nametags.minestom.MinestomBrickNametags"
    dependencies = ["BrickPlaceholders"]
    externalDependencies {
        repositories {
            jorisg {
                url = 'https://repo.jorisg.com/snapshots'
            }
        }
        artifacts = [
            "com.project.group:artifact:version"
        ]
    }
}
```