package com.seatgeek.gramo.gradle.plugin.extension

import org.gradle.api.Project

fun Project.getStringProperty(name: String): String? =
    runCatching { property(name) }
        .fold(
            onSuccess = { it.toString() },
            onFailure = { System.getenv(name) }
        )
