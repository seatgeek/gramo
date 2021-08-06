package com.seatgeek.gramo.gradle.plugin.helper

import com.seatgeek.gramo.gradle.plugin.entity.ArchetypeConfiguration
import com.seatgeek.gramo.gradle.plugin.entity.TaskInput

class GenerateSubmodule(
    private val copyArchetypeToBuildDirectory: CopyArchetypeToBuildDirectory,
    private val mergeTransformedContentIntoCommitDirectory: MergeTransformedContentIntoCommitDirectory,
    private val transformArchetype: TransformArchetype
) {

    operator fun invoke(archetypeConfiguration: ArchetypeConfiguration, taskInput: TaskInput) {
        copyArchetypeToBuildDirectory(taskInput)

        transformArchetype(
            archetypeConfiguration = archetypeConfiguration,
            taskInput = taskInput
        )

        mergeTransformedContentIntoCommitDirectory(
            isMergeEnabled = archetypeConfiguration.mergeEnabled,
            taskInput = taskInput
        )
    }
}
