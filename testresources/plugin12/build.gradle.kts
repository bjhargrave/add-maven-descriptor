/*
 *
 */

plugins {
	id("java")
	id("dev.hargrave.addmavendescriptor")
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

publishing {
	publications {
		create<MavenPublication>("maven") {
			pom {
				name.set(artifactId)
				description.set("Test Description.")
			}
		}
	}
}

addMavenDescriptor {
	publicationName.set("maven")
}
