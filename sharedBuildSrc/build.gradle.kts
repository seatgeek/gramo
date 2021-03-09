plugins {
    `kotlin-dsl`
}

repositories {
    jcenter()
}

gradlePlugin {
    plugins.register("defaults") {
        id = "com.seatgeek.gramo.defaults"
        implementationClass = "com.seatgeek.gramo.shared.build.GlobalDefaultsPlugin"
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

