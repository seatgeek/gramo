import com.seatgeek.gramo.shared.build.loadStringProperty

buildscript {
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.21")
        classpath("com.vanniktech:gradle-maven-publish-plugin:0.19.0")
        classpath("org.jlleitschuh.gradle:ktlint-gradle:10.2.1")
    }
}

plugins {
    id("com.seatgeek.gramo.defaults")
    id("com.gradle.plugin-publish") version "0.21.0"
    id("com.github.gmazzo.buildconfig") version "3.0.3"

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
        }
    }
}

pluginBundle {
    tags = listOf("kotlin")
}

dependencies {
    implementation(Dependencies.plugins.kotlinGradle)
    implementation(Dependencies.project.kotlin.gradlePluginApi)
    implementation(Dependencies.project.kotlin.stdlib)

    testImplementation(Dependencies.test.mockito)
}

pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
    tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class).configureEach {
        kotlinOptions {
            jvmTarget = "1.8"
            // Until gradle no longer includes an old version of kotlin
            allWarningsAsErrors = false
        }
    }
}
