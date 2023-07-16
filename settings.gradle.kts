include(":example")

includeBuild("sharedBuildSrc")

includeBuild("gradle-plugin") {
    dependencySubstitution {
        substitute(module("com.seatgeek.gramo:gradle-plugin")).using(project(":"))
    }
}
