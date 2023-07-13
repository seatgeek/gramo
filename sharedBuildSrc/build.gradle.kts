plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

gradlePlugin {
    plugins.register("defaults") {
        id = "com.seatgeek.gramo.defaults"
        implementationClass = "com.seatgeek.gramo.shared.build.GlobalDefaultsPlugin"
    }
}

dependencies {
    implementation(kotlin("gradle-plugin"))
}