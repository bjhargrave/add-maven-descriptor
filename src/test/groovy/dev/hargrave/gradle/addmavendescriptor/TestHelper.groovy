package dev.hargrave.gradle.addmavendescriptor

import aQute.bnd.version.MavenVersion
import org.gradle.api.JavaVersion
import org.gradle.testkit.runner.GradleRunner

class TestHelper {
	private TestHelper() { }

	public static GradleRunner getGradleRunner() {
		return runner(gradleVersion())
	}

	public static GradleRunner getGradleRunner(String version) {
		String defaultVersion = gradleVersion()
		if (MavenVersion.parseMavenString(defaultVersion) > MavenVersion.parseMavenString(version)) {
			return runner(defaultVersion)
		}
		return runner(version)
	}

	private static GradleRunner runner(String version) {
		GradleRunner runner = GradleRunner.create()
		if (System.getProperty("org.gradle.warning.mode") == "fail") {
			// if "fail" we use the build gradle version
			return runner
		}
		return runner.withGradleVersion(version)
	}

	private static String gradleVersion() {
		if (JavaVersion.current().isCompatibleWith(JavaVersion.VERSION_23)) {
			return "8.10"
		}
		if (JavaVersion.current().isCompatibleWith(JavaVersion.VERSION_22)) {
			return "8.8"
		}
		if (JavaVersion.current().isCompatibleWith(JavaVersion.VERSION_21)) {
			return "8.5"
		}
		if (JavaVersion.current().isCompatibleWith(JavaVersion.VERSION_20)) {
			return "8.3"
		}
		if (JavaVersion.current().isCompatibleWith(JavaVersion.VERSION_19)) {
			return "7.6"
		}
		if (JavaVersion.current().isCompatibleWith(JavaVersion.VERSION_18)) {
			return "7.5"
		}
		if (JavaVersion.current().isCompatibleWith(JavaVersion.VERSION_17)) {
			return "7.3.2"
		}
		return "6.1"
	}
}
