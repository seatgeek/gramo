buildscript {
    repositories {
        gradlePluginPortal()
    }

    dependencies {
        classpath("com.seatgeek.gramo:gradle-plugin")
    }
}

val cleanGradlePlugin by tasks.registering(Task::class) {
    dependsOn(gradle.includedBuild("gradle-plugin").task(":clean"))
}

val cleanSharedBuildSrc by tasks.registering(Task::class) {
    dependsOn(gradle.includedBuild("sharedBuildSrc").task(":clean"))
}

val clean by tasks.registering(Task::class) {
    dependsOn(cleanGradlePlugin, cleanSharedBuildSrc)
}

val testGradlePlugin by tasks.registering(Task::class) {
    dependsOn(gradle.includedBuild("gradle-plugin").task(":test"))
}

val test by tasks.registering(Task::class) {
    dependsOn(testGradlePlugin)
}

val lintKotlinGradlePlugin by tasks.registering(Task::class) {
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

val uploadGradlePluginArchives by tasks.registering(Task::class) {
    dependsOn(gradle.includedBuild("gradle-plugin").task(":publish"))
}

val publishGradlePluginLocally by tasks.registering(Task::class) {
    dependsOn(gradle.includedBuild("gradle-plugin").task(":publishToMavenLocal"))
}

val publishLocally by tasks.registering(Task::class) {
    dependsOn(publishGradlePluginLocally)
}
