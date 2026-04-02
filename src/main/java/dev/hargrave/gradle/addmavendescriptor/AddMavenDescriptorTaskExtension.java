package dev.hargrave.gradle.addmavendescriptor;

import static dev.hargrave.gradle.addmavendescriptor.AddMavenDescriptorPluginExtension.capitalize;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.Transformer;
import org.gradle.api.file.FileCollection;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskOutputs;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;

/**
 * Jar task extension to specify the MavenPublication name to
 * use for the maven descriptor.
 * <p>
 * The default is the project extension's MavenPublication name.
 */
public class AddMavenDescriptorTaskExtension {
	/**
	 * Name of the task extension.
	 */
	public static final String NAME = "addMavenDescriptor";

	/**
	 * Property for the Maven Publication name to use for the
	 * maven description.
	 */
	private final Property<String> publicationName;

	/**
	 * Provider for the Maven Publication named by {@link #getPublicationName()}.
	 */
	private final Provider<MavenPublication> publication;

	/**
	 * Task extension.
	 *
	 * @param task The task.
	 * @param pluginExtension The plugin extension.
	 */
	public AddMavenDescriptorTaskExtension(AbstractArchiveTask task, AddMavenDescriptorPluginExtension pluginExtension) {
		Project project = task.getProject();

		// Publication name to use
		this.publicationName = project.getObjects()
			.property(String.class)
			.value(pluginExtension.getPublicationName());

		// Publication for publication name
		this.publication = getPublicationName().flatMap(pluginExtension.publications()::named);

		// need to programmatically add to inputs since @Input in an
		// extension is not processed
		task.getInputs()
			.property("publicationName", getPublicationName())
			.optional(true);

		// Configure the task
		configureTask(task);
	}

	/**
	 * Property naming the MavenPublication to use for the maven
	 * descriptor information.
	 * <p>
	 * The initial value for this property is
	 * {@link AddMavenDescriptorPluginExtension#getPublicationName()}.
	 *
	 * @return The name of the MavenPublication to use for the maven
	 * descriptor information.
	 */
	@Input
	@Optional
	public Property<String> getPublicationName() {
		return publicationName;
	}

	/**
	 * Provider for the Maven Publication named by {@link #getPublicationName()}.
	 */
	private Provider<MavenPublication> publication() {
		return publication;
	}

	/**
	 * Configure the task.
	 *
	 * @param task The task to configure.
	 */
	private void configureTask(AbstractArchiveTask task) {
		Project project = task.getProject();

		// Map task names to their outputs
		TaskContainer tasks = project.getTasks();
		Transformer<Provider<FileCollection>, String> taskOutputs = name -> tasks.named(name)
				.map(Task::getOutputs)
				.map(TaskOutputs::getFiles);

		// Jar location to store maven descriptor
		Provider<String> metaInfMaven = metaInfMaven().orElse("");

		// Empty FileCollection provider if no publication selected
		Provider<? extends FileCollection> fileCollectionProvider = project.getProviders().provider(project.getObjects()::fileCollection);

		// Copy pom.xml into jar
		task.into(metaInfMaven, copySpec -> copySpec.from(generatePomFileTaskName().flatMap(taskOutputs)
				.orElse(fileCollectionProvider))
			.rename(name -> "pom.xml"));

		// Copy pom.properties into jar
		task.into(metaInfMaven, copySpec -> copySpec.from(generatePomPropertiesTaskName().flatMap(taskOutputs)
				.orElse(fileCollectionProvider))
			.rename(name -> "pom.properties"));
	}

	/**
	 * Path to location to store maven descriptor in jar.
	 *
	 * @return Path to location to store maven descriptor in jar.
	 */
	private Provider<String> metaInfMaven() {
		return publication().map(publication -> "META-INF/maven/" + publication.getGroupId() + "/" + publication.getArtifactId());
	}

	/**
	 * Task name of task generating pom file.
	 *
	 * @return Task name of task generating pom file.
	 */
	private Provider<String> generatePomFileTaskName() {
		return publication().map(publication -> "generatePomFileFor" + capitalize(publication.getName()) + "Publication");
	}

	/**
	 * Task name of task generating pom properties.
	 *
	 * @return Task name of task generating pom properties.
	 */
	private Provider<String> generatePomPropertiesTaskName() {
		return publication().map(publication -> "generatePomPropertiesFor" + capitalize(publication.getName()) + "Publication");
	}
}
