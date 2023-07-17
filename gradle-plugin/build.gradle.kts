import com.seatgeek.gramo.shared.build.loadStringProperty

plugins {
    id("com.seatgeek.gramo.defaults")
    id("com.seatgeek.gramo.publish")

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
    implementation(Dependencies.project.kotlin.gradlePluginApi)

    implementation(Dependencies.project.google.gson)

    testImplementation(Dependencies.test.mockito)
}
