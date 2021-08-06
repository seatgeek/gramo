package com.seatgeek.gramo.gradle.plugin.entity

import java.io.File

data class TaskInput(
    val archetype: String,
    val baseModuleName: String,
    val configuration: String,
    val groupId: String,
    val moduleClassName: String,
    val executionType: ExecutionType
) {
    sealed class ExecutionType {
        data class DryRun(val buildDirectory: File) : ExecutionType()
        data class ProductionRun(val buildDirectory: File, val commitDirectory: File) : ExecutionType()
    }
}
