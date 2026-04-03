/*
 *
 */

plugins {
	id("java")
	id("dev.hargrave.addmavendescriptor")
	groovy
	`kotlin-dsl`
	id("com.gradle.plugin-publish")
}

group = "test.addmavendescriptor.gradle"
version = "1.0.0"

repositories {
	mavenCentral()
}

dependencies {
	implementation("commons-codec:commons-codec:1.5")
	implementation("commons-lang:commons-lang:2.6")
	testImplementation("junit:junit:4.9")
}

// Gradle plugin description
gradlePlugin {
	website.set("https://github.com/bjhargrave/add-maven-descriptor")
	vcsUrl.set("https://github.com/bjhargrave/add-maven-descriptor.git")
	plugins {
		create("Test") {
			id = "test.addmavendescriptor.gradle"
			implementationClass = "doubler.impl.DoublerImpl"
			displayName = "Test Gradle Plugin"
			description = "Gradle Plugin for a test."
			tags.set(listOf("gradle", "maven"))
		}
	}
}

publishing {
	publications {
		create<MavenPublication>("pluginMaven") {
			pom {
				name.set(artifactId)
				description.set("Test Description.")
			}
		}
	}
}

// Disable gradle module metadata
tasks.withType<GenerateModuleMetadata>().configureEach {
	enabled = false
}
