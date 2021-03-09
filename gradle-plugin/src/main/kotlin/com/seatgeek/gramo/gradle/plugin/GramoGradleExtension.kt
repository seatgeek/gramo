package com.seatgeek.gramo.gradle.plugin

import org.gradle.api.Project
import java.io.File

open class GramoGradleExtension(targetProject: Project) {
    val rootProjectDirectory: File = targetProject.rootProject.projectDir

    val buildDirectory: File = targetProject.rootProject.buildDir
        .resolve(".gramo")
        .apply { mkdirs() }

    /**
     * The directory path of available archetypes relative to the [rootProjectDirectory].
     */
    var archetypesPath: String = "gramo"

    /**
     * The [versionString] can actually be a specific value or code to be executed by gradle.
     */
    var versionString: String = ""
}
