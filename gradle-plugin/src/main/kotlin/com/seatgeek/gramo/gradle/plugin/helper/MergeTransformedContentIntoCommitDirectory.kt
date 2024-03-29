package com.seatgeek.gramo.gradle.plugin.helper

import com.seatgeek.gramo.gradle.plugin.entity.MergeConflict
import com.seatgeek.gramo.gradle.plugin.entity.TaskInput

class MergeTransformedContentIntoCommitDirectory {

    operator fun invoke(isMergeEnabled: Boolean, taskInput: TaskInput): List<MergeConflict> {
        return when (val executionType = taskInput.executionType) {
            TaskInput.ExecutionType.DryRun -> { emptyList() }
            is TaskInput.ExecutionType.ProductionRun -> {
                var terminalException: Exception? = null
                val mergeConflicts = mutableListOf<MergeConflict>()

                taskInput.buildDirectory
                    .copyRecursively(executionType.commitDirectory, overwrite = false) { targetFile, exception ->
                        if (isMergeEnabled && exception is FileAlreadyExistsException) {
                            if (targetFile.isDirectory && !exception.file.isDirectory) {
                                terminalException = IllegalStateException(
                                    "Unable to merge a file into a directory at $targetFile.",
                                )
                                OnErrorAction.TERMINATE
                            } else if (!targetFile.isDirectory && exception.file.isDirectory) {
                                terminalException = IllegalStateException(
                                    "Unable to merge a directory into a file at $targetFile.",
                                )
                                OnErrorAction.TERMINATE
                            } else {
                                if (targetFile.readText() != exception.file.readText()) {
                                    mergeConflicts.add(MergeConflict(target = targetFile, newFile = exception.file))
                                }
                                OnErrorAction.SKIP
                            }
                        } else {
                            terminalException = exception
                            OnErrorAction.TERMINATE
                        }
                    }

                terminalException?.run { throw this }
                mergeConflicts
            }
        }
    }
}
