package com.seatgeek.gramo.gradle.plugin.entity

import com.google.gson.annotations.SerializedName

/**
 * Using nullable types to allow for schema + preset validation.
 */
data class ArchetypeConfigurationInputSet(
    @SerializedName("allow_conflict_merge")
    val allowConflictMerge: Boolean?,
    @SerializedName("available_tags")
    val availableTags: Set<String>?,
    @SerializedName("extra_variables")
    val extraVariables: Set<ExtraVariable>?,
    val tags: Set<String>?,
)
