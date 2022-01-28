package com.seatgeek.gramo.gradle.plugin.entity

/**
 * Using nullable types to allow for schema + preset validation.
 */
data class ExtraVariable(
    val name: String?,
    val value: String?
)
