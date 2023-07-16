package com.seatgeek.gramo.shared.build

import Dependencies
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.*
import org.jmailen.gradle.kotlinter.KotlinterPlugin
import org.jmailen.gradle.kotlinter.tasks.ConfigurableKtLintTask
import org.jmailen.gradle.kotlinter.tasks.FormatTask
import org.jmailen.gradle.kotlinter.tasks.LintTask

class ProjectDefaultsPlugin : Plugin<Project> {

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

        target.plugins.withType<KotlinterPlugin> {
            val lintKt = target.tasks.register<LintTask>("lintKt") {
                group = "verification"
                source(target.files("src"))
                reports.set(
                    mapOf(
                        "plain" to target.file("build/lint-report.txt"),
                    ),
                )
            }

            val formatKt = target.tasks.register<FormatTask>("formatKt") {
                group = "formatting"
                source(target.files("src"))
                report.set(target.file("build/format-report.txt"))
            }

            target.tasks.named("lintKotlin") { dependsOn(lintKt) }
            target.tasks.named("formatKotlin") { dependsOn(formatKt) }

            target.tasks.withType<ConfigurableKtLintTask> {
                exclude { element ->
                    element.file.absolutePath.contains(target.buildDir.absolutePath)
                }
            }
        }

        if (target.name != "gramo") {
            target.loadProjectProperties()

            target.group = target.loadStringProperty("gramoGroupId")
            target.version = target.loadStringProperty("gramoVersion")

            val sourceSets = target.extensions.getByName("sourceSets") as org.gradle.api.tasks.SourceSetContainer
            sourceSets["main"].java.srcDir("src/main/kotlin")
            sourceSets["test"].java.srcDir("src/test/kotlin")

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
}
