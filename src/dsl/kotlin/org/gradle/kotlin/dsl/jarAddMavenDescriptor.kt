@file:Suppress("unused")

package org.gradle.kotlin.dsl

import dev.hargrave.gradle.addmavendescriptor.AddMavenDescriptorTaskExtension
import org.gradle.api.tasks.bundling.Jar

val Jar.addMavenDescriptor: AddMavenDescriptorTaskExtension
	get() = extensions.getByName(AddMavenDescriptorTaskExtension.NAME) as AddMavenDescriptorTaskExtension

fun Jar.addMavenDescriptor(configure: AddMavenDescriptorTaskExtension.() -> Unit) =
	extensions.configure(AddMavenDescriptorTaskExtension.NAME, configure)
