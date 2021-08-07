import com.seatgeek.gramo.shared.build.loadStringProperty

plugins {
    id("com.seatgeek.gramo.defaults")
}

buildscript {
    dependencies {
        classpath("com.seatgeek.gramo:gradle-plugin")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.21")
        classpath("com.vanniktech:gradle-maven-publish-plugin:0.11.1")
        classpath("org.jlleitschuh.gradle:ktlint-gradle:9.2.1")
    }
}

val cleanGradlePlugin: Task by tasks.creating {
    dependsOn(gradle.includedBuild("gradle-plugin").task(":clean"))
}

val clean: Task by tasks.getting {
    dependsOn(cleanGradlePlugin)
}

val testGradlePlugin: Task by tasks.creating {
    dependsOn(gradle.includedBuild("gradle-plugin").task(":test"))
}

val test: Task by tasks.getting {
    dependsOn(testGradlePlugin)
}

val ktlintCheckGradlePlugin: Task by tasks.creating {
    dependsOn(gradle.includedBuild("gradle-plugin").task(":ktlintCheck"))
}

val ktlintCheck: Task by tasks.getting {
    dependsOn(ktlintCheckGradlePlugin)
}

val ktlintFormatGradlePlugin: Task by tasks.creating {
    dependsOn(gradle.includedBuild("gradle-plugin").task(":ktlintFormat"))
}

val ktlintFormat: Task by tasks.getting {
    dependsOn(ktlintFormatGradlePlugin)
}

val uploadGradlePluginArchives: Task by tasks.creating {
    dependsOn(gradle.includedBuild("gradle-plugin").task(":uploadArchives"))
}

val uploadArchives: Task by tasks.getting {
    dependsOn(uploadGradlePluginArchives)
}

val publishGradlePluginLocally: Task by tasks.creating {
    dependsOn(gradle.includedBuild("gradle-plugin").task(":publishAllPublicationsToLocalRepository"))
}

val publishLocally: Task by tasks.creating {
    dependsOn(publishGradlePluginLocally)
}

subprojects {
    apply(plugin = "com.seatgeek.gramo.defaults")

    pluginManager.withPlugin(Dependencies.targets.jvm) {
        tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class).configureEach {
            kotlinOptions {
                jvmTarget = "1.8"
                allWarningsAsErrors = true
            }
        }
    }

    pluginManager.withPlugin(Dependencies.plugins.mavenPublish) {
        val mavenPublish = requireNotNull(extensions.findByType(com.vanniktech.maven.publish.MavenPublishPluginExtension::class))

        mavenPublish.nexus {
            groupId = loadStringProperty("releaseProfile")
        }

        val uploadArchivesTarget: com.vanniktech.maven.publish.MavenPublishTarget = requireNotNull(
            mavenPublish.targets.findByName("uploadArchives")
        )

        uploadArchivesTarget.releaseRepositoryUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
        uploadArchivesTarget.snapshotRepositoryUrl = "https://oss.sonatype.org/content/repositories/snapshots/"
    }
}
