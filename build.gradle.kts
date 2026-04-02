import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import java.util.Locale

plugins {
	groovy
	`kotlin-dsl`
	id("com.gradle.plugin-publish")
}

interface Injected {
	@get:Inject
	val fs: FileSystemOperations
}

group = "dev.hargrave"
version = "1.2.0-SNAPSHOT"
val javaTarget = JavaLanguageVersion.of(17)
val testTarget = findProperty("test.target")?.let {
	JavaLanguageVersion.of(it.toString())
} ?: JavaLanguageVersion.current()

java {
	withJavadocJar()
	withSourcesJar()
}

val maven_repo_local = System.getProperty("maven.repo.local")?.let {
	var rootGradle: Gradle = gradle
	while (rootGradle.parent != null) {
		rootGradle = rootGradle.parent!!
	}
	rootGradle.startParameter.currentDir.resolve(it).normalize().absolutePath
}

repositories {
	mavenCentral()
}

// SourceSet for Kotlin DSL code so that it can be built after the main SourceSet
val dsl: SourceSet by sourceSets.creating
sourceSets {
	dsl.apply {
		compileClasspath += main.get().output
		runtimeClasspath += main.get().output
	}
	test {
		compileClasspath += dsl.output
		runtimeClasspath += dsl.output
	}
}

configurations {
	dsl.compileOnlyConfigurationName {
		extendsFrom(compileOnly.get())
	}
	dsl.implementationConfigurationName {
		extendsFrom(implementation.get())
	}
	dsl.runtimeOnlyConfigurationName {
		extendsFrom(runtimeOnly.get())
	}
}

// Dependencies
dependencies {
	testImplementation("org.junit.jupiter:junit-jupiter:6.0.3")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testImplementation("org.spockframework:spock-core:2.4-groovy-4.0")
	testImplementation("biz.aQute.bnd:biz.aQute.bndlib:7.2.3")
}

// Gradle plugin descriptions
gradlePlugin {
	website = "https://github.com/bjhargrave/add-maven-descriptor"
	vcsUrl = "https://github.com/bjhargrave/add-maven-descriptor.git"
	plugins {
		register("AddMavenDescriptor") {
			id = "dev.hargrave.addmavendescriptor"
			displayName = "Add Maven Descriptor Plugin"
			description = "Gradle Plugin to add Maven descriptor information to built jars."
			tags = listOf("maven", "pom")
			implementationClass = "dev.hargrave.gradle.addmavendescriptor.AddMavenDescriptorPlugin"
		}
	}
}

publishing {
	publications {
		// Main plugin publication
		create<MavenPublication>("pluginMaven") {
			pom {
				name = artifactId
				description = "Add Maven Descriptor"
			}
			val publication = this
			tasks.register<WriteProperties>(
				"generatePomPropertiesFor${
					publication.name.replaceFirstChar {
						it.titlecase(Locale.ROOT)
					}
				}Publication"
			) {
				description = "Generates the Maven pom.properties file for publication '${publication.name}'."
				group = PublishingPlugin.PUBLISH_TASK_GROUP
				destinationFile.value(layout.buildDirectory.file("publications/${publication.name}/pom-default.properties"))
				property("groupId", provider { publication.groupId })
				property("artifactId", provider { publication.artifactId })
				property("version", provider { publication.version })
			}
		}
		// Configure pom metadata
		withType<MavenPublication> {
			pom {
				url = "https://github.com/bjhargrave/add-maven-descriptor"
				organization {
					name = "BJ Hargrave"
					url = "https://github.com/bjhargrave"
				}
				licenses {
					license {
						name = "Apache-2.0"
						url = "https://opensource.org/licenses/Apache-2.0"
						distribution = "repo"
						comments = "This program and the accompanying materials are made available under the terms of the Apache License, Version 2.0."
					}
				}
				scm {
					url = "https://github.com/bjhargrave/add-maven-descriptor"
					connection = "scm:git:https://github.com/bjhargrave/add-maven-descriptor.git"
					developerConnection = "scm:git:git@github.com:bjhargrave/add-maven-descriptor.git"
					tag = version
				}
				developers {
					developer {
						id = "bjhargrave"
						name = "BJ Hargrave"
						email = "bj@hargrave.dev"
						url = "https://github.com/bjhargrave"
						organization = "IBM"
						organizationUrl = "https://developer.ibm.com"
						roles = setOf("developer")
						timezone = "America/New_York"
					}
				}
			}
		}
	}
}

tasks.withType<JavaCompile>().configureEach {
	options.release = javaTarget.asInt()
}

// Use same jvm target for kotlin code as for java code
tasks.withType<KotlinCompilationTask<KotlinJvmCompilerOptions>>().configureEach {
	compilerOptions {
		jvmTarget = JvmTarget.fromTarget(javaTarget.toString())
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
	pluginClasspath.from(dsl.output)
}

tasks.jar {
	// Include dsl SourceSet
	from(dsl.output)
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
	from(dsl.allSource)
}

val testresourcesOutput = layout.buildDirectory.dir("testresources")

// Use same jvm target for test groovy code as for test execution
tasks.compileTestGroovy {
	targetCompatibility = testTarget.toString()
}

// Configure test
tasks.test {
	javaLauncher = javaToolchains.launcherFor {
		languageVersion = testTarget
	}
	useJUnitPlatform()
	reports {
		junitXml.apply {
			isOutputPerTestCase = true
			mergeReruns = true
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
	systemProperty("org.gradle.warning.mode", gradle.startParameter.warningMode.name.lowercase(Locale.ROOT))
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

tasks.withType<ValidatePlugins>().configureEach {
	failOnWarning = true
	enableStricterValidation = true
}
