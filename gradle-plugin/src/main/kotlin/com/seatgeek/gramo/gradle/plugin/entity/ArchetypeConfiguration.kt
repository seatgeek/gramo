package com.seatgeek.gramo.gradle.plugin.entity

data class ArchetypeConfiguration(
    val mergeEnabled: Boolean,
    val interpolationMap: Map<String, String>,
    val tags: Set<String>
)
