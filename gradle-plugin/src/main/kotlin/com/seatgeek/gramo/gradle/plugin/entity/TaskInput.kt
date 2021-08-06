package com.seatgeek.gramo.gradle.plugin.entity

import java.io.File

data class TaskInput(
    val archetypeDirectory: File,
    val buildDirectory: File,
    val baseModuleName: String,
    val preset: String,
    val groupId: String,
    val moduleClassName: String,
    val executionType: ExecutionType,
    val versionString: String
) {
    sealed class ExecutionType {
        object DryRun : ExecutionType()
        data class ProductionRun(val commitDirectory: File) : ExecutionType()
    }
}
