package com.seatgeek.gramo.gradle.plugin.helper

import com.seatgeek.gramo.gradle.plugin.entity.TaskInput

class GenerateSubmodule(
    private val copyArchetypeToBuildDirectory: CopyArchetypeToBuildDirectory,
    private val extractAndValidateArchetypeConfiguration: ExtractAndValidateArchetypeConfiguration,
    private val mergeTransformedContentIntoCommitDirectory: MergeTransformedContentIntoCommitDirectory,
    private val transformArchetype: TransformArchetype
) {

    operator fun invoke(taskInput: TaskInput) {
        val archetypeConfiguration = extractAndValidateArchetypeConfiguration(taskInput)

        copyArchetypeToBuildDirectory(taskInput)

        transformArchetype(
            archetypeConfiguration = archetypeConfiguration,
            taskInput = taskInput
        )

        mergeTransformedContentIntoCommitDirectory(taskInput)
    }
}