package com.seatgeek.gramo.gradle.plugin.helper

import com.seatgeek.gramo.gradle.plugin.entity.TaskInput

class MergeTransformedContentIntoCommitDirectory {
    operator fun invoke(taskInput: TaskInput) {
        moveModuleIntoPlace(taskInput)
    }

    private fun moveModuleIntoPlace(taskInput: TaskInput) {
        when (val executionType = taskInput.executionType) {
            TaskInput.ExecutionType.DryRun -> { /** Noop */ }
            is TaskInput.ExecutionType.ProductionRun -> {
                taskInput.buildDirectory
                    .copyRecursively(executionType.commitDirectory, overwrite = false) { _, exception ->
                        throw exception
                    }
            }
        }
    }
}
