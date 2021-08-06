package com.seatgeek.gramo.gradle.plugin.entity

import com.google.gson.annotations.SerializedName

/**
 * Using nullable types to allow for schema + preset validation.
 */
data class ArchetypeConfiguration(
    @SerializedName("allow_conflict_merge")
    val allowConflictMerge: Boolean?,
    @SerializedName("available_tags")
    val availableTags: List<String>?,
    @SerializedName("extra_variables")
    val extraVariables: List<ExtraVariable>?,
    val tags: List<String>?
)
