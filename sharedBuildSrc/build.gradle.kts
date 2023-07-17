plugins {
    kotlin("jvm") version "1.9.0" apply false

    id("org.jmailen.kotlinter") version "3.15.0"

    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

gradlePlugin {
    plugins.register("defaults") {
        id = "com.seatgeek.gramo.defaults"
        implementationClass = "com.seatgeek.gramo.shared.build.ProjectDefaultsPlugin"
    }

    plugins.register("publish") {
        id = "com.seatgeek.gramo.publish"
        implementationClass = "com.seatgeek.gramo.shared.build.ProjectPublishingPlugin"
    }
}

dependencies {
    implementation(kotlin("gradle-plugin"))

    implementation("com.gradle.publish:plugin-publish-plugin:1.2.0")
    implementation("com.vanniktech:gradle-maven-publish-plugin:0.25.3")
    implementation("org.jmailen.gradle:kotlinter-gradle:3.15.0")
}