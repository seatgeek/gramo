package com.seatgeek.gramo.gradle.plugin.entity

data class MarkupTokenTransformation(
    val startIndex: Int,
    val endIndex: Int,
    val value: String
)
