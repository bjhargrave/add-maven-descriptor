package dev.hargrave.gradle.addmavendescriptor;

import java.util.SortedSet;
import java.util.function.BiConsumer;

import org.gradle.api.NamedDomainObjectSet;
import org.gradle.api.Project;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.file.RegularFile;
import org.gradle.api.plugins.PluginManager;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.publish.plugins.PublishingPlugin;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.WriteProperties;
import org.gradle.util.GradleVersion;

/**
 * Plugin extension to specify the MavenPublication name to
 * use for the maven descriptor.
 * <p>
 * If only one MavenPublication exists, then it will automatically
 * be used. If there are multiple MavenPublications, then a publication
 * name must be specified for the maven descriptors.
 * <p>
 * This extension also registers generatePomProperties tasks for each
 * MavenPublication.
 */
public class AddMavenDescriptorPluginExtension {
	/**
	 * Name of the plugin extension.
	 */
	public static final String NAME = "addMavenDescriptor";

	/**
	 * Property for the Maven Publication name to use for the
	 * maven description.
	 */
	private final Property<String> publicationName;

	/**
	 * Set of Maven Publications.
	 */
	private final NamedDomainObjectSet<MavenPublication> publications;

	/**
	 * Plugin extension.
	 *
	 * @param project The project.
	 */
	public AddMavenDescriptorPluginExtension(Project project) {
		PluginManager pluginManager = project.getPluginManager();

		// Set of Maven Publications ignoring PluginMarker publications
		this.publications = project.getExtensions()
			.getByType(PublishingExtension.class)
			.getPublications()
			.withType(MavenPublication.class)
			.matching(publication -> !(pluginManager.hasPlugin("com.gradle.plugin-publish") && publication.getName()
				.contains("PluginMarker")));

		// Default publication name to use
		this.publicationName = project.getObjects()
			.property(String.class)
			.value(project.provider(() -> {
				SortedSet<String> names = publications().getNames();
				if (names.size() == 1) {
					return names.first();
				}
				return null; // no value
			}));

		// Configure the project
		configureProject(project);
	}


	/**
	 * Property naming the MavenPublication to use for the maven
	 * descriptor information.
	 * <p>
	 * The extension on the jar task uses this property as the value.
	 *
	 * @return The name of the MavenPublication to use for the maven
	 * descriptor information.
	 */
	public Property<String> getPublicationName() {
		return publicationName;
	}

	/**
	 * Set of Maven Publications to use.
	 * <p>
	 * PluginMarker publications are not included.
	 *
	 * @return The set of Maven Publications to use.
	 */
	NamedDomainObjectSet<MavenPublication> publications() {
		return publications;
	}

	/**
	 * Configure the project.
	 *
	 * @param project The project to configure.
	 */
	private void configureProject(Project project) {
		TaskContainer tasks = project.getTasks();
		ProjectLayout layout = project.getLayout();
		ProviderFactory providers = project.getProviders();

		// Register generatePomProperties task for each MavenPublication
		publications().configureEach(publication -> {
			String pomPropertiesTaskName = "generatePomPropertiesFor" + capitalize(publication.getName()) + "Publication";
			tasks.register(pomPropertiesTaskName, WriteProperties.class, task -> {
				String publicationName = publication.getName();
				task.setDescription("Generates the Maven pom.properties file for publication '" + publicationName + "'.");
				task.setGroup(PublishingPlugin.PUBLISH_TASK_GROUP);
                task.getDestinationFile().value(layout.getBuildDirectory()
					.file("publications/" + publicationName + "/pom-default.properties"));
				task.property("groupId", providers.provider(publication::getGroupId));
				task.property("artifactId", providers.provider(publication::getArtifactId));
				task.property("version", providers.provider(publication::getVersion));
			});
		});
	}

	/**
	 * Capitalize the first letter in the specified String.
	 *
	 * @param string The string to capitalize.
	 * @return The capitalized string.
	 */
	static String capitalize(String string) {
		if ((string == null) || string.isEmpty()) {
			return string;
		}
		int codePoint = string.codePointAt(0);
		int titleCase = Character.toTitleCase(codePoint);
		if (titleCase == codePoint) {
			return string;
		}
		int length = string.length();
		int[] codePoints = new int[length];
		codePoints[0] = titleCase;
		int count = 1;
		for (int index = 0; (index += Character.charCount(codePoint)) < length; count++) {
			codePoints[count] = codePoint = string.codePointAt(index);
		}
		return new String(codePoints, 0, count);
	}

	/**
	 * Return whether the current Gradle is at least the specified version.
	 *
	 * @param requestedVersion The requested Gradle version.
	 * @return Whether the current Gradle is at least the specified version.
	 */
	static boolean isGradleCompatible(String requestedVersion) {
		return GradleVersion.current()
			.compareTo(GradleVersion.version(requestedVersion)) >= 0;
	}
}
