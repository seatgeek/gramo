buildscript {
    repositories {
        gradlePluginPortal()
    }

    dependencies {
        classpath("com.seatgeek.gramo:gradle-plugin")
    }
}

val cleanGradlePlugin: Task by tasks.creating {
    dependsOn(gradle.includedBuild("gradle-plugin").task(":clean"))
}

val cleanSharedBuildSrc: Task by tasks.creating {
    dependsOn(gradle.includedBuild("sharedBuildSrc").task(":clean"))
}

val clean: Task by tasks.creating {
    dependsOn(cleanGradlePlugin, cleanSharedBuildSrc)
}

val testGradlePlugin: Task by tasks.creating {
    dependsOn(gradle.includedBuild("gradle-plugin").task(":test"))
}

val test: Task by tasks.creating {
    dependsOn(testGradlePlugin)
}

val lintKotlinGradlePlugin: Task by tasks.creating {
    dependsOn(gradle.includedBuild("gradle-plugin").task(":lintKotlin"))
}

val lintKotlinSharedBuildSrc by tasks.registering(Task::class) {
    dependsOn(gradle.includedBuild("sharedBuildSrc").task(":lintKotlin"))
}

val lintKotlin by tasks.registering(Task::class) {
    dependsOn(lintKotlinGradlePlugin, lintKotlinSharedBuildSrc)
}

val formatKotlinGradlePlugin by tasks.registering(Task::class) {
    dependsOn(gradle.includedBuild("gradle-plugin").task(":formatKotlin"))
}

val formatKotlinSharedBuildSrc by tasks.registering(Task::class) {
    dependsOn(gradle.includedBuild("sharedBuildSrc").task(":formatKotlin"))
}

val formatKotlin by tasks.registering(Task::class) {
    dependsOn(formatKotlinGradlePlugin, formatKotlinSharedBuildSrc)
}

val uploadGradlePluginArchives: Task by tasks.creating {
    dependsOn(gradle.includedBuild("gradle-plugin").task(":publish"))
}

val publishGradlePluginLocally: Task by tasks.creating {
    dependsOn(gradle.includedBuild("gradle-plugin").task(":publishToMavenLocal"))
}

val publishLocally: Task by tasks.creating {
    dependsOn(publishGradlePluginLocally)
}
