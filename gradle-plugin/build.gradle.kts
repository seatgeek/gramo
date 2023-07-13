import com.seatgeek.gramo.shared.build.loadStringProperty

buildscript {
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0")
        classpath("com.vanniktech:gradle-maven-publish-plugin:0.25.3")
        classpath("org.jlleitschuh.gradle:ktlint-gradle:11.5.0")
    }
}

plugins {
    id("com.seatgeek.gramo.defaults")

    id("com.gradle.plugin-publish") version "1.2.0"
    id("com.github.gmazzo.buildconfig") version "4.1.1"

    `java-gradle-plugin`
}

buildConfig {
    project.properties
        .filter { entry -> entry.key.startsWith("gramo") }
        .forEach { (key, value) ->
            buildConfigField("String", key, "\"${requireNotNull(value) as String}\"")
        }
}

gradlePlugin {
    plugins {
        register("gramo-gradle-plugin") {
            id = loadStringProperty("gramoGradlePluginId")
            implementationClass = "com.seatgeek.gramo.gradle.plugin.GramoGradlePlugin"

            @Suppress("UnstableApiUsage")
            tags = listOf("kotlin")
        }
    }
}

dependencies {
    implementation(Dependencies.plugins.kotlinGradle)
    implementation(Dependencies.project.kotlin.gradlePluginApi)

    implementation(Dependencies.project.google.gson)

    testImplementation(Dependencies.test.mockito)
}
