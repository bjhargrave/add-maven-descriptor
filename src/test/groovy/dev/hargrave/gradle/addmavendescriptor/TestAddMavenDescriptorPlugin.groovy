package dev.hargrave.gradle.addmavendescriptor

import aQute.lib.utf8properties.UTF8Properties
import groovy.xml.XmlSlurper
import spock.lang.Specification

import java.util.jar.JarFile

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS
import static org.gradle.testkit.runner.TaskOutcome.UP_TO_DATE

class TestAddMavenDescriptorPlugin extends Specification {
	File buildDir = new File("build")
	File testResources = new File(buildDir, "testresources")
	String groupId = "test.addmavendescriptor.gradle"

	def "Task selected MavenPublication Test"() {
		given:
		String testProject = "plugin1"
		File testProjectDir = new File(testResources, testProject).canonicalFile
		assert testProjectDir.isDirectory()
		File testProjectBuildDir = new File(testProjectDir, "build").canonicalFile

		when:
		UTF8Properties props = new UTF8Properties()
		def result = TestHelper.getGradleRunner()
			.withProjectDir(testProjectDir)
			.withArguments("--parallel", "--stacktrace", "--debug", "--configuration-cache", "build")
			.withPluginClasspath()
			.forwardOutput()
			.build()

		then:
		result.task(":jar").outcome == SUCCESS
		result.task(":generatePomFileForMavenPublication").outcome == SUCCESS
		result.task(":generatePomPropertiesForMavenPublication").outcome == SUCCESS

		testProjectBuildDir.isDirectory()

		File jartask_bundle = new File(testProjectBuildDir, "libs/${testProject}-1.0.0.jar")
		jartask_bundle.isFile()
		JarFile jartask_jar = new JarFile(jartask_bundle)

		def pomxml = jartask_jar.getEntry("META-INF/maven/${groupId}/${testProject}/pom.xml")
		pomxml
		def project = new XmlSlurper(false, false).parse(jartask_jar.getInputStream(pomxml))
		project
		project.groupId.text() == groupId
		project.artifactId.text() == testProject

		def pomproperties = jartask_jar.getEntry("META-INF/maven/${groupId}/${testProject}/pom.properties")
		pomproperties
		props.load(jartask_jar.getInputStream(pomproperties))
		props.getProperty("groupId") == groupId
		props.getProperty("artifactId") == testProject

		jartask_jar.getEntry("doubler/Doubler.class")
		jartask_jar.getEntry("doubler/packageinfo")
		jartask_jar.getEntry("doubler/impl/DoublerImpl.class")
		jartask_jar.getEntry("doubler/impl/packageinfo")
		!jartask_jar.getEntry("doubler/impl/DoublerImplTest.class")
		jartask_jar.getEntry("META-INF/services/foo.properties")
		jartask_jar.close()

		when:
		result = TestHelper.getGradleRunner()
			.withProjectDir(testProjectDir)
			.withArguments("--parallel", "--stacktrace", "--debug", "--configuration-cache", "build")
			.withPluginClasspath()
			.forwardOutput()
			.build()

		then:
		result.task(":jar").outcome == UP_TO_DATE
		result.task(":generatePomFileForMavenPublication")
		result.task(":generatePomPropertiesForMavenPublication").outcome == UP_TO_DATE
	}

	def "Project selected MavenPublication Test"() {
		given:
		String testProject = "plugin2"
		File testProjectDir = new File(testResources, testProject).canonicalFile
		assert testProjectDir.isDirectory()
		File testProjectBuildDir = new File(testProjectDir, "build").canonicalFile

		when:
		UTF8Properties props = new UTF8Properties()
		def result = TestHelper.getGradleRunner()
			.withProjectDir(testProjectDir)
			.withArguments("--parallel", "--stacktrace", "--debug", "--configuration-cache", "build")
			.withPluginClasspath()
			.forwardOutput()
			.build()

		then:
		result.task(":jar").outcome == SUCCESS
		result.task(":generatePomFileForMavenPublication").outcome == SUCCESS
		result.task(":generatePomPropertiesForMavenPublication").outcome == SUCCESS

		testProjectBuildDir.isDirectory()

		File jartask_bundle = new File(testProjectBuildDir, "libs/${testProject}-1.0.0.jar")
		jartask_bundle.isFile()
		JarFile jartask_jar = new JarFile(jartask_bundle)

		def pomxml = jartask_jar.getEntry("META-INF/maven/${groupId}/${testProject}/pom.xml")
		pomxml
		def project = new XmlSlurper(false, false).parse(jartask_jar.getInputStream(pomxml))
		project
		project.groupId.text() == groupId
		project.artifactId.text() == testProject

		def pomproperties = jartask_jar.getEntry("META-INF/maven/${groupId}/${testProject}/pom.properties")
		pomproperties
		props.load(jartask_jar.getInputStream(pomproperties))
		props.getProperty("groupId") == groupId
		props.getProperty("artifactId") == testProject

		jartask_jar.getEntry("doubler/Doubler.class")
		jartask_jar.getEntry("doubler/packageinfo")
		jartask_jar.getEntry("doubler/impl/DoublerImpl.class")
		jartask_jar.getEntry("doubler/impl/packageinfo")
		!jartask_jar.getEntry("doubler/impl/DoublerImplTest.class")
		jartask_jar.getEntry("META-INF/services/foo.properties")
		jartask_jar.close()

		when:
		result = TestHelper.getGradleRunner()
			.withProjectDir(testProjectDir)
			.withArguments("--parallel", "--stacktrace", "--debug", "--configuration-cache", "build")
			.withPluginClasspath()
			.forwardOutput()
			.build()

		then:
		result.task(":jar").outcome == UP_TO_DATE
		result.task(":generatePomFileForMavenPublication")
		result.task(":generatePomPropertiesForMavenPublication").outcome == UP_TO_DATE
	}

	def "Single default MavenPublication Test"() {
		given:
		String testProject = "plugin3"
		File testProjectDir = new File(testResources, testProject).canonicalFile
		assert testProjectDir.isDirectory()
		File testProjectBuildDir = new File(testProjectDir, "build").canonicalFile

		when:
		UTF8Properties props = new UTF8Properties()
		def result = TestHelper.getGradleRunner()
			.withProjectDir(testProjectDir)
			.withArguments("--parallel", "--stacktrace", "--debug", "--configuration-cache", "build")
			.withPluginClasspath()
			.forwardOutput()
			.build()

		then:
		result.task(":jar").outcome == SUCCESS
		result.task(":generatePomFileForMavenPublication").outcome == SUCCESS
		result.task(":generatePomPropertiesForMavenPublication").outcome == SUCCESS

		testProjectBuildDir.isDirectory()

		File jartask_bundle = new File(testProjectBuildDir, "libs/${testProject}-1.0.0.jar")
		jartask_bundle.isFile()
		JarFile jartask_jar = new JarFile(jartask_bundle)

		def pomxml = jartask_jar.getEntry("META-INF/maven/${groupId}/${testProject}/pom.xml")
		pomxml
		def project = new XmlSlurper(false, false).parse(jartask_jar.getInputStream(pomxml))
		project
		project.groupId.text() == groupId
		project.artifactId.text() == testProject

		def pomproperties = jartask_jar.getEntry("META-INF/maven/${groupId}/${testProject}/pom.properties")
		pomproperties
		props.load(jartask_jar.getInputStream(pomproperties))
		props.getProperty("groupId") == groupId
		props.getProperty("artifactId") == testProject

		jartask_jar.getEntry("doubler/Doubler.class")
		jartask_jar.getEntry("doubler/packageinfo")
		jartask_jar.getEntry("doubler/impl/DoublerImpl.class")
		jartask_jar.getEntry("doubler/impl/packageinfo")
		!jartask_jar.getEntry("doubler/impl/DoublerImplTest.class")
		jartask_jar.getEntry("META-INF/services/foo.properties")
		jartask_jar.close()

		when:
		result = TestHelper.getGradleRunner()
			.withProjectDir(testProjectDir)
			.withArguments("--parallel", "--stacktrace", "--debug", "--configuration-cache", "build")
			.withPluginClasspath()
			.forwardOutput()
			.build()

		then:
		result.task(":jar").outcome == UP_TO_DATE
		result.task(":generatePomFileForMavenPublication")
		result.task(":generatePomPropertiesForMavenPublication").outcome == UP_TO_DATE
	}

	def "Multiple none selected MavenPublication Test"() {
		given:
		String testProject = "plugin4"
		File testProjectDir = new File(testResources, testProject).canonicalFile
		assert testProjectDir.isDirectory()
		File testProjectBuildDir = new File(testProjectDir, "build").canonicalFile

		when:
		def result = TestHelper.getGradleRunner()
			.withProjectDir(testProjectDir)
			.withArguments("--parallel", "--stacktrace", "--debug", "--configuration-cache", "build")
			.withPluginClasspath()
			.forwardOutput()
			.build()

		then:
		result.task(":jar").outcome == SUCCESS
		!result.task(":generatePomFileForMavenPublication")
		!result.task(":generatePomPropertiesForMavenPublication")

		testProjectBuildDir.isDirectory()

		File jartask_bundle = new File(testProjectBuildDir, "libs/${testProject}-1.0.0.jar")
		jartask_bundle.isFile()
		JarFile jartask_jar = new JarFile(jartask_bundle)

		!jartask_jar.getEntry("META-INF/maven/${groupId}/${testProject}/pom.xml")
		!jartask_jar.getEntry("META-INF/maven/${groupId}/${testProject}/pom.properties")
		jartask_jar.getEntry("doubler/Doubler.class")
		jartask_jar.getEntry("doubler/packageinfo")
		jartask_jar.getEntry("doubler/impl/DoublerImpl.class")
		jartask_jar.getEntry("doubler/impl/packageinfo")
		!jartask_jar.getEntry("doubler/impl/DoublerImplTest.class")
		jartask_jar.getEntry("META-INF/services/foo.properties")
		jartask_jar.close()

		when:
		result = TestHelper.getGradleRunner()
			.withProjectDir(testProjectDir)
			.withArguments("--parallel", "--stacktrace", "--debug", "--configuration-cache", "build")
			.withPluginClasspath()
			.forwardOutput()
			.build()

		then:
		result.task(":jar").outcome == UP_TO_DATE
	}

	def "Multiple Task selected MavenPublication Test"() {
		given:
		String testProject = "plugin5"
		File testProjectDir = new File(testResources, testProject).canonicalFile
		assert testProjectDir.isDirectory()
		File testProjectBuildDir = new File(testProjectDir, "build").canonicalFile

		when:
		UTF8Properties props = new UTF8Properties()
		def result = TestHelper.getGradleRunner()
			.withProjectDir(testProjectDir)
			.withArguments("--parallel", "--stacktrace", "--debug", "--configuration-cache", "build")
			.withPluginClasspath()
			.forwardOutput()
			.build()

		then:
		result.task(":jar").outcome == SUCCESS
		result.task(":generatePomFileForMavenPublication").outcome == SUCCESS
		result.task(":generatePomPropertiesForMavenPublication").outcome == SUCCESS

		testProjectBuildDir.isDirectory()

		File jartask_bundle = new File(testProjectBuildDir, "libs/${testProject}-1.0.0.jar")
		jartask_bundle.isFile()
		JarFile jartask_jar = new JarFile(jartask_bundle)

		def pomxml = jartask_jar.getEntry("META-INF/maven/${groupId}/${testProject}/pom.xml")
		pomxml
		def project = new XmlSlurper(false, false).parse(jartask_jar.getInputStream(pomxml))
		project
		project.groupId.text() == groupId
		project.artifactId.text() == testProject

		def pomproperties = jartask_jar.getEntry("META-INF/maven/${groupId}/${testProject}/pom.properties")
		pomproperties
		props.load(jartask_jar.getInputStream(pomproperties))
		props.getProperty("groupId") == groupId
		props.getProperty("artifactId") == testProject

		jartask_jar.getEntry("doubler/Doubler.class")
		jartask_jar.getEntry("doubler/packageinfo")
		jartask_jar.getEntry("doubler/impl/DoublerImpl.class")
		jartask_jar.getEntry("doubler/impl/packageinfo")
		!jartask_jar.getEntry("doubler/impl/DoublerImplTest.class")
		jartask_jar.getEntry("META-INF/services/foo.properties")
		jartask_jar.close()

		when:
		result = TestHelper.getGradleRunner()
			.withProjectDir(testProjectDir)
			.withArguments("--parallel", "--stacktrace", "--debug", "--configuration-cache", "build")
			.withPluginClasspath()
			.forwardOutput()
			.build()

		then:
		result.task(":jar").outcome == UP_TO_DATE
		result.task(":generatePomFileForMavenPublication")
		result.task(":generatePomPropertiesForMavenPublication").outcome == UP_TO_DATE
	}

	def "Multiple Project selected MavenPublication Test"() {
		given:
		String testProject = "plugin6"
		File testProjectDir = new File(testResources, testProject).canonicalFile
		assert testProjectDir.isDirectory()
		File testProjectBuildDir = new File(testProjectDir, "build").canonicalFile

		when:
		UTF8Properties props = new UTF8Properties()
		def result = TestHelper.getGradleRunner()
			.withProjectDir(testProjectDir)
			.withArguments("--parallel", "--stacktrace", "--debug", "--configuration-cache", "build")
			.withPluginClasspath()
			.forwardOutput()
			.build()

		then:
		result.task(":jar").outcome == SUCCESS
		result.task(":generatePomFileForMavenPublication").outcome == SUCCESS
		result.task(":generatePomPropertiesForMavenPublication").outcome == SUCCESS

		testProjectBuildDir.isDirectory()

		File jartask_bundle = new File(testProjectBuildDir, "libs/${testProject}-1.0.0.jar")
		jartask_bundle.isFile()
		JarFile jartask_jar = new JarFile(jartask_bundle)

		def pomxml = jartask_jar.getEntry("META-INF/maven/${groupId}/${testProject}/pom.xml")
		pomxml
		def project = new XmlSlurper(false, false).parse(jartask_jar.getInputStream(pomxml))
		project
		project.groupId.text() == groupId
		project.artifactId.text() == testProject

		def pomproperties = jartask_jar.getEntry("META-INF/maven/${groupId}/${testProject}/pom.properties")
		pomproperties
		props.load(jartask_jar.getInputStream(pomproperties))
		props.getProperty("groupId") == groupId
		props.getProperty("artifactId") == testProject

		jartask_jar.getEntry("doubler/Doubler.class")
		jartask_jar.getEntry("doubler/packageinfo")
		jartask_jar.getEntry("doubler/impl/DoublerImpl.class")
		jartask_jar.getEntry("doubler/impl/packageinfo")
		!jartask_jar.getEntry("doubler/impl/DoublerImplTest.class")
		jartask_jar.getEntry("META-INF/services/foo.properties")
		jartask_jar.close()

		when:
		result = TestHelper.getGradleRunner()
			.withProjectDir(testProjectDir)
			.withArguments("--parallel", "--stacktrace", "--debug", "--configuration-cache", "build")
			.withPluginClasspath()
			.forwardOutput()
			.build()

		then:
		result.task(":jar").outcome == UP_TO_DATE
		result.task(":generatePomFileForMavenPublication")
		result.task(":generatePomPropertiesForMavenPublication").outcome == UP_TO_DATE
	}

	def "Multiple Task override MavenPublication Test"() {
		given:
		String testProject = "plugin7"
		File testProjectDir = new File(testResources, testProject).canonicalFile
		assert testProjectDir.isDirectory()
		File testProjectBuildDir = new File(testProjectDir, "build").canonicalFile

		when:
		UTF8Properties props = new UTF8Properties()
		def result = TestHelper.getGradleRunner()
			.withProjectDir(testProjectDir)
			.withArguments("--parallel", "--stacktrace", "--debug", "--configuration-cache", "build")
			.withPluginClasspath()
			.forwardOutput()
			.build()

		then:
		result.task(":jar").outcome == SUCCESS
		result.task(":generatePomFileForMavenPublication").outcome == SUCCESS
		result.task(":generatePomPropertiesForMavenPublication").outcome == SUCCESS

		testProjectBuildDir.isDirectory()

		File jartask_bundle = new File(testProjectBuildDir, "libs/${testProject}-1.0.0.jar")
		jartask_bundle.isFile()
		JarFile jartask_jar = new JarFile(jartask_bundle)

		def pomxml = jartask_jar.getEntry("META-INF/maven/${groupId}/${testProject}/pom.xml")
		pomxml
		def project = new XmlSlurper(false, false).parse(jartask_jar.getInputStream(pomxml))
		project
		project.groupId.text() == groupId
		project.artifactId.text() == testProject

		def pomproperties = jartask_jar.getEntry("META-INF/maven/${groupId}/${testProject}/pom.properties")
		pomproperties
		props.load(jartask_jar.getInputStream(pomproperties))
		props.getProperty("groupId") == groupId
		props.getProperty("artifactId") == testProject

		jartask_jar.getEntry("doubler/Doubler.class")
		jartask_jar.getEntry("doubler/packageinfo")
		jartask_jar.getEntry("doubler/impl/DoublerImpl.class")
		jartask_jar.getEntry("doubler/impl/packageinfo")
		!jartask_jar.getEntry("doubler/impl/DoublerImplTest.class")
		jartask_jar.getEntry("META-INF/services/foo.properties")
		jartask_jar.close()

		when:
		result = TestHelper.getGradleRunner()
			.withProjectDir(testProjectDir)
			.withArguments("--parallel", "--stacktrace", "--debug", "--configuration-cache", "build")
			.withPluginClasspath()
			.forwardOutput()
			.build()

		then:
		result.task(":jar").outcome == UP_TO_DATE
		result.task(":generatePomFileForMavenPublication")
		result.task(":generatePomPropertiesForMavenPublication").outcome == UP_TO_DATE
	}

	def "No MavenPublication Test"() {
		given:
		String testProject = "plugin8"
		File testProjectDir = new File(testResources, testProject).canonicalFile
		assert testProjectDir.isDirectory()
		File testProjectBuildDir = new File(testProjectDir, "build").canonicalFile

		when:
		def result = TestHelper.getGradleRunner()
			.withProjectDir(testProjectDir)
			.withArguments("--parallel", "--stacktrace", "--debug", "--configuration-cache", "build")
			.withPluginClasspath()
			.forwardOutput()
			.build()

		then:
		result.task(":jar").outcome == SUCCESS
		!result.task(":generatePomFileForMavenPublication")
		!result.task(":generatePomPropertiesForMavenPublication")

		testProjectBuildDir.isDirectory()

		File jartask_bundle = new File(testProjectBuildDir, "libs/${testProject}-1.0.0.jar")
		jartask_bundle.isFile()
		JarFile jartask_jar = new JarFile(jartask_bundle)

		!jartask_jar.getEntry("META-INF/maven/${groupId}/${testProject}/pom.xml")
		!jartask_jar.getEntry("META-INF/maven/${groupId}/${testProject}/pom.properties")
		jartask_jar.getEntry("doubler/Doubler.class")
		jartask_jar.getEntry("doubler/packageinfo")
		jartask_jar.getEntry("doubler/impl/DoublerImpl.class")
		jartask_jar.getEntry("doubler/impl/packageinfo")
		!jartask_jar.getEntry("doubler/impl/DoublerImplTest.class")
		jartask_jar.getEntry("META-INF/services/foo.properties")
		jartask_jar.close()

		when:
		result = TestHelper.getGradleRunner()
			.withProjectDir(testProjectDir)
			.withArguments("--parallel", "--stacktrace", "--debug", "--configuration-cache", "build")
			.withPluginClasspath()
			.forwardOutput()
			.build()

		then:
		result.task(":jar").outcome == UP_TO_DATE
	}

	def "Project disabled MavenPublication Test"() {
		given:
		String testProject = "plugin9"
		File testProjectDir = new File(testResources, testProject).canonicalFile
		assert testProjectDir.isDirectory()
		File testProjectBuildDir = new File(testProjectDir, "build").canonicalFile

		when:
		def result = TestHelper.getGradleRunner()
			.withProjectDir(testProjectDir)
			.withArguments("--parallel", "--stacktrace", "--debug", "--configuration-cache", "build")
			.withPluginClasspath()
			.forwardOutput()
			.build()

		then:
		result.task(":jar").outcome == SUCCESS
		!result.task(":generatePomFileForMavenPublication")
		!result.task(":generatePomPropertiesForMavenPublication")

		testProjectBuildDir.isDirectory()

		File jartask_bundle = new File(testProjectBuildDir, "libs/${testProject}-1.0.0.jar")
		jartask_bundle.isFile()
		JarFile jartask_jar = new JarFile(jartask_bundle)

		!jartask_jar.getEntry("META-INF/maven/${groupId}/${testProject}/pom.xml")
		!jartask_jar.getEntry("META-INF/maven/${groupId}/${testProject}/pom.properties")
		jartask_jar.getEntry("doubler/Doubler.class")
		jartask_jar.getEntry("doubler/packageinfo")
		jartask_jar.getEntry("doubler/impl/DoublerImpl.class")
		jartask_jar.getEntry("doubler/impl/packageinfo")
		!jartask_jar.getEntry("doubler/impl/DoublerImplTest.class")
		jartask_jar.getEntry("META-INF/services/foo.properties")
		jartask_jar.close()

		when:
		result = TestHelper.getGradleRunner()
			.withProjectDir(testProjectDir)
			.withArguments("--parallel", "--stacktrace", "--debug", "--configuration-cache", "build")
			.withPluginClasspath()
			.forwardOutput()
			.build()

		then:
		result.task(":jar").outcome == UP_TO_DATE
	}

	def "Task disabled MavenPublication Test"() {
		given:
		String testProject = "plugin10"
		File testProjectDir = new File(testResources, testProject).canonicalFile
		assert testProjectDir.isDirectory()
		File testProjectBuildDir = new File(testProjectDir, "build").canonicalFile

		when:
		def result = TestHelper.getGradleRunner()
			.withProjectDir(testProjectDir)
			.withArguments("--parallel", "--stacktrace", "--debug", "--configuration-cache", "build")
			.withPluginClasspath()
			.forwardOutput()
			.build()

		then:
		result.task(":jar").outcome == SUCCESS
		!result.task(":generatePomFileForMavenPublication")
		!result.task(":generatePomPropertiesForMavenPublication")

		testProjectBuildDir.isDirectory()

		File jartask_bundle = new File(testProjectBuildDir, "libs/${testProject}-1.0.0.jar")
		jartask_bundle.isFile()
		JarFile jartask_jar = new JarFile(jartask_bundle)

		!jartask_jar.getEntry("META-INF/maven/${groupId}/${testProject}/pom.xml")
		!jartask_jar.getEntry("META-INF/maven/${groupId}/${testProject}/pom.properties")
		jartask_jar.getEntry("doubler/Doubler.class")
		jartask_jar.getEntry("doubler/packageinfo")
		jartask_jar.getEntry("doubler/impl/DoublerImpl.class")
		jartask_jar.getEntry("doubler/impl/packageinfo")
		!jartask_jar.getEntry("doubler/impl/DoublerImplTest.class")
		jartask_jar.getEntry("META-INF/services/foo.properties")
		jartask_jar.close()

		when:
		result = TestHelper.getGradleRunner()
			.withProjectDir(testProjectDir)
			.withArguments("--parallel", "--stacktrace", "--debug", "--configuration-cache", "build")
			.withPluginClasspath()
			.forwardOutput()
			.build()

		then:
		result.task(":jar").outcome == UP_TO_DATE
	}

	def "Multiple jar tasks MavenPublication Test"() {
		given:
		String testProject = "plugin11"
		File testProjectDir = new File(testResources, testProject).canonicalFile
		assert testProjectDir.isDirectory()
		File testProjectBuildDir = new File(testProjectDir, "build").canonicalFile

		when:
		UTF8Properties props = new UTF8Properties()
		def result = TestHelper.getGradleRunner()
			.withProjectDir(testProjectDir)
			.withArguments("--parallel", "--stacktrace", "--debug", "--configuration-cache", "build")
			.withPluginClasspath()
			.forwardOutput()
			.build()

		then:
		result.task(":jar").outcome == SUCCESS
		result.task(":testJar").outcome == SUCCESS
		result.task(":generatePomFileForMavenPublication").outcome == SUCCESS
		result.task(":generatePomPropertiesForMavenPublication").outcome == SUCCESS

		testProjectBuildDir.isDirectory()

		File jartask_bundle = new File(testProjectBuildDir, "libs/${testProject}-1.0.0.jar")
		jartask_bundle.isFile()
		JarFile jartask_jar = new JarFile(jartask_bundle)

		def pomxml = jartask_jar.getEntry("META-INF/maven/${groupId}/${testProject}/pom.xml")
		pomxml
		def project = new XmlSlurper(false, false).parse(jartask_jar.getInputStream(pomxml))
		project
		project.groupId.text() == groupId
		project.artifactId.text() == testProject

		def pomproperties = jartask_jar.getEntry("META-INF/maven/${groupId}/${testProject}/pom.properties")
		pomproperties
		props.load(jartask_jar.getInputStream(pomproperties))
		props.getProperty("groupId") == groupId
		props.getProperty("artifactId") == testProject

		jartask_jar.getEntry("doubler/Doubler.class")
		jartask_jar.getEntry("doubler/packageinfo")
		jartask_jar.getEntry("doubler/impl/DoublerImpl.class")
		jartask_jar.getEntry("doubler/impl/packageinfo")
		!jartask_jar.getEntry("doubler/impl/DoublerImplTest.class")
		jartask_jar.getEntry("META-INF/services/foo.properties")
		jartask_jar.close()

		File testjartask_bundle = new File(testProjectBuildDir, "libs/${testProject}-1.0.0-tests.jar")
		testjartask_bundle.isFile()
		JarFile testjartask_jar = new JarFile(testjartask_bundle)

		def pomxml2 = testjartask_jar.getEntry("META-INF/maven/${groupId}/${testProject}/pom.xml")
		pomxml2
		def project2 = new XmlSlurper(false, false).parse(testjartask_jar.getInputStream(pomxml2))
		project2
		project2.groupId.text() == groupId
		project2.artifactId.text() == testProject

		def pomproperties2 = testjartask_jar.getEntry("META-INF/maven/${groupId}/${testProject}/pom.properties")
		pomproperties2
		props.clear()
		props.load(testjartask_jar.getInputStream(pomproperties2))
		props.getProperty("groupId") == groupId
		props.getProperty("artifactId") == testProject

		!testjartask_jar.getEntry("doubler/Doubler.class")
		!testjartask_jar.getEntry("doubler/impl/DoublerImpl.class")
		testjartask_jar.getEntry("doubler/impl/DoublerImplTest.class")

		when:
		result = TestHelper.getGradleRunner()
			.withProjectDir(testProjectDir)
			.withArguments("--parallel", "--stacktrace", "--debug", "--configuration-cache", "build")
			.withPluginClasspath()
			.forwardOutput()
			.build()

		then:
		result.task(":jar").outcome == UP_TO_DATE
		result.task(":generatePomFileForMavenPublication")
		result.task(":generatePomPropertiesForMavenPublication").outcome == UP_TO_DATE
	}

	def "Project Kotlin MavenPublication Test"() {
		given:
		String testProject = "plugin12"
		File testProjectDir = new File(testResources, testProject).canonicalFile
		assert testProjectDir.isDirectory()
		File testProjectBuildDir = new File(testProjectDir, "build").canonicalFile

		when:
		UTF8Properties props = new UTF8Properties()
		def result = TestHelper.getGradleRunner()
			.withProjectDir(testProjectDir)
			.withArguments("--parallel", "--stacktrace", "--debug", "--configuration-cache", "build")
			.withPluginClasspath()
			.forwardOutput()
			.build()

		then:
		result.task(":jar").outcome == SUCCESS
		result.task(":generatePomFileForMavenPublication").outcome == SUCCESS
		result.task(":generatePomPropertiesForMavenPublication").outcome == SUCCESS

		testProjectBuildDir.isDirectory()

		File jartask_bundle = new File(testProjectBuildDir, "libs/${testProject}-1.0.0.jar")
		jartask_bundle.isFile()
		JarFile jartask_jar = new JarFile(jartask_bundle)

		def pomxml = jartask_jar.getEntry("META-INF/maven/${groupId}/${testProject}/pom.xml")
		pomxml
		def project = new XmlSlurper(false, false).parse(jartask_jar.getInputStream(pomxml))
		project
		project.groupId.text() == groupId
		project.artifactId.text() == testProject

		def pomproperties = jartask_jar.getEntry("META-INF/maven/${groupId}/${testProject}/pom.properties")
		pomproperties
		props.load(jartask_jar.getInputStream(pomproperties))
		props.getProperty("groupId") == groupId
		props.getProperty("artifactId") == testProject

		jartask_jar.getEntry("doubler/Doubler.class")
		jartask_jar.getEntry("doubler/packageinfo")
		jartask_jar.getEntry("doubler/impl/DoublerImpl.class")
		jartask_jar.getEntry("doubler/impl/packageinfo")
		!jartask_jar.getEntry("doubler/impl/DoublerImplTest.class")
		jartask_jar.getEntry("META-INF/services/foo.properties")
		jartask_jar.close()

		when:
		result = TestHelper.getGradleRunner()
			.withProjectDir(testProjectDir)
			.withArguments("--parallel", "--stacktrace", "--debug", "--configuration-cache", "build")
			.withPluginClasspath()
			.forwardOutput()
			.build()

		then:
		result.task(":jar").outcome == UP_TO_DATE
		result.task(":generatePomFileForMavenPublication")
		result.task(":generatePomPropertiesForMavenPublication").outcome == UP_TO_DATE
	}

	def "Task Kotlin MavenPublication Test"() {
		given:
		String testProject = "plugin13"
		File testProjectDir = new File(testResources, testProject).canonicalFile
		assert testProjectDir.isDirectory()
		File testProjectBuildDir = new File(testProjectDir, "build").canonicalFile

		when:
		UTF8Properties props = new UTF8Properties()
		def result = TestHelper.getGradleRunner()
			.withProjectDir(testProjectDir)
			.withArguments("--parallel", "--stacktrace", "--debug", "--configuration-cache", "build")
			.withPluginClasspath()
			.forwardOutput()
			.build()

		then:
		result.task(":jar").outcome == SUCCESS
		result.task(":generatePomFileForMavenPublication").outcome == SUCCESS
		result.task(":generatePomPropertiesForMavenPublication").outcome == SUCCESS

		testProjectBuildDir.isDirectory()

		File jartask_bundle = new File(testProjectBuildDir, "libs/${testProject}-1.0.0.jar")
		jartask_bundle.isFile()
		JarFile jartask_jar = new JarFile(jartask_bundle)

		def pomxml = jartask_jar.getEntry("META-INF/maven/${groupId}/${testProject}/pom.xml")
		pomxml
		def project = new XmlSlurper(false, false).parse(jartask_jar.getInputStream(pomxml))
		project
		project.groupId.text() == groupId
		project.artifactId.text() == testProject

		def pomproperties = jartask_jar.getEntry("META-INF/maven/${groupId}/${testProject}/pom.properties")
		pomproperties
		props.load(jartask_jar.getInputStream(pomproperties))
		props.getProperty("groupId") == groupId
		props.getProperty("artifactId") == testProject

		jartask_jar.getEntry("doubler/Doubler.class")
		jartask_jar.getEntry("doubler/packageinfo")
		jartask_jar.getEntry("doubler/impl/DoublerImpl.class")
		jartask_jar.getEntry("doubler/impl/packageinfo")
		!jartask_jar.getEntry("doubler/impl/DoublerImplTest.class")
		jartask_jar.getEntry("META-INF/services/foo.properties")
		jartask_jar.close()

		when:
		result = TestHelper.getGradleRunner()
			.withProjectDir(testProjectDir)
			.withArguments("--parallel", "--stacktrace", "--debug", "--configuration-cache", "build")
			.withPluginClasspath()
			.forwardOutput()
			.build()

		then:
		result.task(":jar").outcome == UP_TO_DATE
		result.task(":generatePomFileForMavenPublication")
		result.task(":generatePomPropertiesForMavenPublication").outcome == UP_TO_DATE
	}

	def "Gradle Plugin MavenPublication Test"() {
		given:
		String testProject = "plugin14"
		File testProjectDir = new File(testResources, testProject).canonicalFile
		assert testProjectDir.isDirectory()
		File testProjectBuildDir = new File(testProjectDir, "build").canonicalFile

		when:
		UTF8Properties props = new UTF8Properties()
		def result = TestHelper.getGradleRunner()
			.withProjectDir(testProjectDir)
			.withArguments("--parallel", "--stacktrace", "--debug", "--configuration-cache", "build")
			.withPluginClasspath()
			.forwardOutput()
			.build()

		then:
		result.task(":jar").outcome == SUCCESS
		result.task(":generatePomFileForPluginMavenPublication").outcome == SUCCESS
		result.task(":generatePomPropertiesForPluginMavenPublication").outcome == SUCCESS

		testProjectBuildDir.isDirectory()

		File jartask_bundle = new File(testProjectBuildDir, "libs/${testProject}-1.0.0.jar")
		jartask_bundle.isFile()
		JarFile jartask_jar = new JarFile(jartask_bundle)

		def pomxml = jartask_jar.getEntry("META-INF/maven/${groupId}/${testProject}/pom.xml")
		pomxml
		def project = new XmlSlurper(false, false).parse(jartask_jar.getInputStream(pomxml))
		project
		project.groupId.text() == groupId
		project.artifactId.text() == testProject

		def pomproperties = jartask_jar.getEntry("META-INF/maven/${groupId}/${testProject}/pom.properties")
		pomproperties
		props.load(jartask_jar.getInputStream(pomproperties))
		props.getProperty("groupId") == groupId
		props.getProperty("artifactId") == testProject

		def pluginproperties = jartask_jar.getEntry("META-INF/gradle-plugins/${groupId}.properties")
		pluginproperties
		props.clear()
		props.load(jartask_jar.getInputStream(pluginproperties))
		props.getProperty("implementation-class") == "doubler.impl.DoublerImpl"

		jartask_jar.getEntry("doubler/Doubler.class")
		jartask_jar.getEntry("doubler/packageinfo")
		jartask_jar.getEntry("doubler/impl/DoublerImpl.class")
		jartask_jar.getEntry("doubler/impl/packageinfo")
		!jartask_jar.getEntry("doubler/impl/DoublerImplTest.class")
		jartask_jar.getEntry("META-INF/services/foo.properties")
		jartask_jar.close()

		when:
		result = TestHelper.getGradleRunner()
			.withProjectDir(testProjectDir)
			.withArguments("--parallel", "--stacktrace", "--debug", "--configuration-cache", "build")
			.withPluginClasspath()
			.forwardOutput()
			.build()

		then:
		result.task(":jar").outcome == UP_TO_DATE
		result.task(":generatePomFileForPluginMavenPublication")
		result.task(":generatePomPropertiesForPluginMavenPublication").outcome == UP_TO_DATE
	}

}
