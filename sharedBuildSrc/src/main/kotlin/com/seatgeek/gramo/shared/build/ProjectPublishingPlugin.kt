package com.seatgeek.gramo.shared.build

import com.gradle.publish.PublishPlugin
import com.vanniktech.maven.publish.MavenPublishPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.withType

class ProjectPublishingPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        /** The main purpose of withType is to make the order of plugins being applied irrelevant. */
        target.plugins.withType<ProjectDefaultsPlugin> {
            target.configurePomProperties()
            target.plugins {
                apply(PublishPlugin::class)
                apply(MavenPublishPlugin::class)
            }
        }
    }

    private fun Project.configurePomProperties() {
        setProperty("GROUP", group)
        setProperty("VERSION_NAME", version)
        setProperty("POM_ARTIFACT_ID", "gramo-$name")

        setProperty("POM_INCEPTION_YEAR", "2022")
        setProperty("POM_URL", "https://github.com/seatgeek/gramo")

        setProperty("POM_LICENSE_NAME", "MIT License")
        setProperty("POM_LICENSE_URL", "https://github.com/seatgeek/gramo/blob/main/LICENSE")
        setProperty("POM_LICENSE_DIST", "repo")

        setProperty("POM_SCM_URL", "https://github.com/seatgeek/gramo")
        setProperty("POM_SCM_CONNECTION", "scm:git:git://github.com/seatgeek/gramo.git")
        setProperty("POM_SCM_DEV_CONNECTION", "scm:git:ssh://git@github.com/seatgeek/gramo.git")

        setProperty("POM_DEVELOPER_ID", "seatgeek")
        setProperty("POM_DEVELOPER_NAME", "SeatGeek")
        setProperty("POM_DEVELOPER_URL", "https://github.com/seatgeek/")
    }
}
