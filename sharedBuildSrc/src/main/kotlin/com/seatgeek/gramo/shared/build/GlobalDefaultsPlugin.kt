package com.seatgeek.gramo.shared.build

import Dependencies
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.*

class GlobalDefaultsPlugin : Plugin<Project> {

    private val publishingAllowlist = setOf(
        "gradle-plugin"
    )

    override fun apply(target: Project) {
        target.repositories {
            gradlePluginPortal()
        }

        target.buildscript {
            repositories {
                gradlePluginPortal()
            }
        }

        target.plugins {
            apply(Dependencies.targets.jvm)

            apply(Dependencies.plugins.idea)
            apply(Dependencies.plugins.ktlint)
        }

        target.extensions.configure<JavaPluginExtension> {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(11))
            }
        }

        if (target.name != "gramo") {
            target.loadProjectProperties()

            target.group = target.loadStringProperty("gramoGroupId")
            target.version = target.loadStringProperty("gramoVersion")

            val sourceSets = target.extensions.getByName("sourceSets") as org.gradle.api.tasks.SourceSetContainer
            sourceSets["main"].java.srcDir("src/main/kotlin")
            sourceSets["test"].java.srcDir("src/test/kotlin")

            if (publishingAllowlist.contains(target.name)) {
                target.configurePublishing()
            }

            target.tasks.named("test", Test::class) {
                useJUnitPlatform {
                    includeEngines = setOf("spek2")
                }
            }

            // Assertion Library
            target.dependencies.add("testImplementation", Dependencies.test.truth)

            // Test Runner
            target.dependencies.add("testImplementation", Dependencies.test.spek.jvm)
            target.dependencies.add("testRuntimeOnly", Dependencies.test.spek.runner)

            target.tasks.named("clean", Delete::class) {
                delete.add(target.buildDir)

                target.project.gradle.includedBuilds.forEach { build ->
                    dependsOn(build.task(":clean"))
                }
            }
        }
    }

    private fun Project.configurePublishing() {
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

        plugins {
            apply(Dependencies.plugins.mavenPublish)
        }
    }
}