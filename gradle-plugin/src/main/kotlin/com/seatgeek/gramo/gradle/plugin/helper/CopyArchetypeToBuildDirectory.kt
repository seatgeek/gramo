package com.seatgeek.gramo.gradle.plugin.helper

import com.seatgeek.gramo.gradle.plugin.entity.TaskInput

class CopyArchetypeToBuildDirectory(private val validateDirectoryExists: ValidateDirectoryExists) {

    operator fun invoke(taskInput: TaskInput) {
        val contentDirectory = taskInput.archetypeDirectory
            .resolve("content")
            .apply { validateDirectoryExists(this) }

        taskInput.buildDirectory.deleteRecursively()

        contentDirectory.copyRecursively(taskInput.buildDirectory, overwrite = true) { _, exception ->
            throw exception
        }
    }
}
