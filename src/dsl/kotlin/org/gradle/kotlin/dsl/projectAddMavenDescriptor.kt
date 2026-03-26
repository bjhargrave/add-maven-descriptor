@file:Suppress("unused")

package org.gradle.kotlin.dsl

import dev.hargrave.gradle.addmavendescriptor.AddMavenDescriptorPluginExtension
import org.gradle.api.Project

val Project.addMavenDescriptor: AddMavenDescriptorPluginExtension
	get() = extensions.getByName(AddMavenDescriptorPluginExtension.NAME) as AddMavenDescriptorPluginExtension

fun Project.addMavenDescriptor(configure: AddMavenDescriptorPluginExtension.() -> Unit) =
	extensions.configure(AddMavenDescriptorPluginExtension.NAME, configure)
