rootProject.buildFileName = "build.gradle.kts"

include(":example")

includeBuild("sharedBuildSrc")

includeBuild("gradle-plugin") {
    dependencySubstitution {
        substitute(module("com.seatgeek.gramo:gradle-plugin")).with(project(":"))
    }
}
