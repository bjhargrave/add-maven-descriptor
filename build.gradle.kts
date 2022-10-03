import java.util.*

plugins {
	groovy
	`kotlin-dsl`
	id("com.gradle.plugin-publish")
}

interface Injected {
	@get:Inject val fs: FileSystemOperations
}

group = "dev.hargrave"
version = "1.1.0-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
	targetCompatibility = JavaVersion.VERSION_1_8
	withJavadocJar()
	withSourcesJar()
}

val localrepo: String? = System.getProperty("maven.repo.local")
localrepo?.let {
	var rootGradle: Gradle = gradle
	while (rootGradle.parent != null) {
		rootGradle = rootGradle.parent!!
	}
	val maven_repo_local by extra(rootGradle.startParameter.currentDir.resolve(it).normalize().absolutePath)
}
val maven_repo_local: String? by extra

repositories {
	mavenCentral()
}

// SourceSet for Kotlin DSL code so that it can be built after the main SourceSet
val dsl by sourceSets.registering
sourceSets {
	dsl {
		compileClasspath += main.get().output
		runtimeClasspath += main.get().output
	}
	test {
		compileClasspath += dsl.get().output
		runtimeClasspath += dsl.get().output
	}
}

configurations {
	val dslCompileOnly by existing {
		extendsFrom(compileOnly.get())
	}
	val dslImplementation by existing {
		extendsFrom(implementation.get())
	}
	val dslRuntimeOnly by existing {
		extendsFrom(runtimeOnly.get())
	}
}

// Dependencies
dependencies {
	testImplementation("org.spockframework:spock-core:2.3-groovy-4.0")
	testImplementation("biz.aQute.bnd:biz.aQute.bndlib:6.3.1")
}

// Gradle plugin descriptions
gradlePlugin {
	plugins {
		create("AddMavenDescriptor") {
			id = "dev.hargrave.addmavendescriptor"
			implementationClass = "dev.hargrave.gradle.addmavendescriptor.AddMavenDescriptorPlugin"
			displayName = "Add Maven Descriptor Plugin"
			description = "Gradle Plugin to add Maven descriptor information to built jars."
		}
	}
}

// Gradle plugin bundle description
pluginBundle {
	website = "https://github.com/bjhargrave/add-maven-descriptor"
	vcsUrl = "https://github.com/bjhargrave/add-maven-descriptor.git"
	description = "Gradle Plugin to add Maven descriptor information to built jars."
	tags = listOf("maven", "pom")
}

publishing {
	publications {
		// Main plugin publication
		create<MavenPublication>("pluginMaven") {
			pom {
				name.set(artifactId)
				description.set("Add Maven Descriptor")
			}
			val publication = this
			tasks.register<WriteProperties>("generatePomPropertiesFor${publication.name.capitalize()}Publication") {
				description = "Generates the Maven pom.properties file for publication '${publication.name}'."
				group = PublishingPlugin.PUBLISH_TASK_GROUP
				setOutputFile(layout.buildDirectory.file("publications/${publication.name}/pom-default.properties"))
				property("groupId", provider { publication.groupId })
				property("artifactId", provider { publication.artifactId })
				property("version", provider { publication.version })
			}
		}
		// Configure pom metadata
		withType<MavenPublication> {
			pom {
				url.set("https://github.com/bjhargrave/add-maven-descriptor")
				organization {
					name.set("BJ Hargrave")
					url.set("https://github.com/bjhargrave")
				}
				licenses {
					license {
						name.set("Apache-2.0")
						url.set("https://opensource.org/licenses/Apache-2.0")
						distribution.set("repo")
						comments.set("This program and the accompanying materials are made available under the terms of the Apache License, Version 2.0.")
					}
				}
				scm {
					url.set("https://github.com/bjhargrave/add-maven-descriptor")
					connection.set("scm:git:https://github.com/bjhargrave/add-maven-descriptor.git")
					developerConnection.set("scm:git:git@github.com:bjhargrave/add-maven-descriptor.git")
					tag.set(version.toString())
				}
				developers {
					developer {
						id.set("bjhargrave")
						name.set("BJ Hargrave")
						email.set("bj@hargrave.dev")
						url.set("https://github.com/bjhargrave")
						organization.set("IBM")
						organizationUrl.set("https://developer.ibm.com")
						roles.set(setOf("developer"))
						timezone.set("America/New_York")
					}
				}
			}
		}
	}
}

// Disable gradle module metadata
tasks.withType<GenerateModuleMetadata>().configureEach {
	enabled = false
}

// Reproducible jars
tasks.withType<AbstractArchiveTask>().configureEach {
	isPreserveFileTimestamps = false
	isReproducibleFileOrder = true
}

// Reproducible javadoc
tasks.withType<Javadoc>().configureEach {
	options {
		this as StandardJavadocDocletOptions // unsafe cast
		isNoTimestamp = true
	}
}

tasks.pluginUnderTestMetadata {
	// Include dsl SourceSet
	pluginClasspath.from(dsl.get().output)
}

tasks.jar {
	// Include dsl SourceSet
	from(dsl.get().output)
	// META-INF/maven folder
	val metaInfMaven = publishing.publications.named<MavenPublication>("pluginMaven").map {
		"META-INF/maven/${it.groupId}/${it.artifactId}"
	}
	// Include generated pom.xml file
	into(metaInfMaven) {
		from(tasks.named("generatePomFileForPluginMavenPublication"))
		rename { "pom.xml" }
	}
	// Include generated pom.properties file
	into(metaInfMaven) {
		from(tasks.named("generatePomPropertiesForPluginMavenPublication"))
		rename { "pom.properties" }
	}
}

tasks.named<Jar>("sourcesJar") {
	// Include dsl SourceSet
	from(dsl.get().allSource)
}

val testresourcesOutput = layout.buildDirectory.dir("testresources")

// Configure test
tasks.test {
	useJUnitPlatform()
	reports {
		junitXml.apply {
			isOutputPerTestCase = true
			mergeReruns.set(true)
		}
	}
	testLogging {
		setExceptionFormat("full")
		info {
			events("STANDARD_OUT", "STANDARD_ERROR", "STARTED", "FAILED", "PASSED", "SKIPPED")
		}
	}
	val testresourcesSource = layout.projectDirectory.dir("testresources")
	inputs.files(testresourcesSource).withPathSensitivity(PathSensitivity.RELATIVE).withPropertyName("testresources")
	systemProperty("org.gradle.warning.mode", gradle.startParameter.warningMode.name.toLowerCase(Locale.ROOT))
	maven_repo_local?.let {
		systemProperty("maven.repo.local", it)
	}
	val injected = objects.newInstance<Injected>()
	doFirst {
		// copy test resources into build dir
		injected.fs.delete {
			delete(testresourcesOutput)
		}
		injected.fs.copy {
			from(testresourcesSource)
			into(testresourcesOutput)
		}
	}
}

tasks.named<Delete>("cleanTest") {
	delete(testresourcesOutput)
}
