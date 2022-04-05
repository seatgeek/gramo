import com.seatgeek.gramo.shared.build.loadStringProperty

plugins {
    id("com.seatgeek.gramo.defaults")
}

buildscript {
    dependencies {
        classpath("com.seatgeek.gramo:gradle-plugin")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.21")
        classpath("com.vanniktech:gradle-maven-publish-plugin:0.19.0")
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
    dependsOn(gradle.includedBuild("gradle-plugin").task(":publish"))
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
}
