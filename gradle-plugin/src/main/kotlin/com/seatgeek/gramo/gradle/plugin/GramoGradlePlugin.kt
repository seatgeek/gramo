package com.seatgeek.gramo.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Project.DEFAULT_VERSION

class GramoGradlePlugin : Plugin<Project> {

    lateinit var extension: GramoGradleExtension

    override fun apply(target: Project): Unit = with(target) {
        extension = extensions.create("gramo", GramoGradleExtension::class.java, target)

        extension.versionString = target.version
            .takeIf { it != DEFAULT_VERSION }
            ?.let(Any::toString)
            ?: ""

        tasks.register("generateModule", GramoModuleTask::class.java) { task ->
            task.gramoExtension = extension
        }
    }
}
