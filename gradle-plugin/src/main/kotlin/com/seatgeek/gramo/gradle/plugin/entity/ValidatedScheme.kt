package com.seatgeek.gramo.gradle.plugin.entity

data class ValidatedScheme(
    val allowConflictMerge: Boolean,
    val availableTags: Set<String>,
    val extraVariables: Set<ExtraVariable>,
    val defaultTags: Set<String>
)

/*
    val allowConflictMerge: Boolean?,
    val availableTags: List<String>?,
    val extraVariables: List<ExtraVariable>?,
    val tags: List<String>?
 */
