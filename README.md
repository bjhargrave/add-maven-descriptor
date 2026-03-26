# Add Maven Descriptor Gradle Plugin

When Maven builds a jar, by default the [Maven Archiver][1] includes the Maven descriptor information.
This is the pom.xml file at `META-INF/maven/${groupId}/${artifactId}/pom.xml` and the pom.properties file at `META-INF/maven/${groupId}/${artifactId}/pom.properties`.

These resources are useful to have in jars when they are standing alone and disconnected from a repository.

Gradle's `Jar` task type does not include Maven descriptor information in built jars.
So this plugin was created to add the Maven descriptor information to built jars.

## Configuring the plugin

The simplest way to use the plugin is to apply to your project.

```groovy
plugins {
  id "dev.hargrave.addmavendescriptor" version "1.1.0"
}
```

The `addmavendescriptor` plugin will apply the `maven-publish` plugin if it is not already applied.
The `maven-publish` plugin is used as the Maven descriptor information source.
A `MavenPublication` must be configured to define the Maven descriptor information which includes the `groupId` and `artifactId`.

```groovy
publishing {
  publications {
    maven(MavenPublication) {
      pom {
        // Configure pom elements
      }
    }
  }
}
```

If no `MavenPublication` is defined, then the `addmavendescriptor` plugin does nothing.
So it is safe to apply to a project which does not define a `MavenPublication`.

When only a single `MavenPublication` is defined, the `addmavendescriptor` plugin will automatically use it to obtain the Maven descriptor information and the pom file generator task defined by the `maven-publish` plugin when the `MavenPublication` is defined.
The `addmavendescriptor` plugin will define a pom properties file generator task for the `MavenPublication` and also configure all the `Jar` tasks to include the generated pom file and the generated pom properties file.

If multiple `MavenPublication`s are defined, then you will need to tell the `addmavendescriptor` plugin which one to use for the Maven descriptor information.
This can be done at the project level and/or at the `Jar` task level.
The `addmavendescriptor` plugin adds an `addMavenDescriptor` extension to the project and to each `Jar` task.
So you can configure the name of the `MavenPublication` to use:

```groovy
addMavenDescriptor {
  publicationName = "maven"
}
```

The configuration at the project level is used by all the `Jar` tasks unless you set the `publicationName` in the `Jar` task.

```groovy
tasks.named("jar") {
  addMavenDescriptor {
    publicationName = "maven"
  }
}
```

Finally, if you have a `Jar` task, or project, in which you don't want Maven descriptor, you can set `publicationName` to `null` to disable adding the maven descriptor.

```groovy
tasks.named("jar") {
  addMavenDescriptor {
    publicationName = null
  }
}
```

## Dependencies

This plugin requires at least Gradle 8.3 and Java 17.


[1]: https://maven.apache.org/shared/maven-archiver/index.html
