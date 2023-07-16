plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

gradlePlugin {
    plugins.register("defaults") {
        id = "com.seatgeek.gramo.defaults"
        implementationClass = "com.seatgeek.gramo.shared.build.GlobalDefaultsPlugin"
    }
}

dependencies {
    implementation(kotlin("gradle-plugin"))
    implementation("org.jlleitschuh.gradle:ktlint-gradle:11.5.0")
}