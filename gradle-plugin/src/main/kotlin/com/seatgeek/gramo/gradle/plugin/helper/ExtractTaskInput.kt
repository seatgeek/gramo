package com.seatgeek.gramo.gradle.plugin.helper

import com.seatgeek.gramo.gradle.plugin.GramoGenerateSubmoduleTask
import com.seatgeek.gramo.gradle.plugin.entity.TaskInput

/**
 * Validates the task inputs and returns them in a more meaningful entity. Will throw on invalid input with an appropriate message to the user.
 *
 * Produces side effects that describe the source of each value.
 */
class ExtractTaskInput {
    operator fun invoke(task: GramoGenerateSubmoduleTask): TaskInput = task.run {
        if (moduleClassName.isBlank()) {
            throw IllegalArgumentException("Please specify argument: --name=<e.g. VenueCommerce>")
        }

        if (baseModuleName.isBlank()) {
            throw IllegalArgumentException("Please specify argument: --module_name=<e.g. venue-commerce (use spinal case)>")
        }

        if (groupId.isBlank()) {
            throw IllegalArgumentException("Please specify argument: --group_id=<e.g. com.seatgeek.performer>")
        }

        if (archetype.isBlank()) {
            throw IllegalArgumentException("Please specify arguments: --archetype=<e.g. feature>")
        }

        if (configuration.isBlank()) {
            throw IllegalArgumentException("Please specify arguments: --configuration=<e.g. android_feature>")
        }

        /** TODO: Ensure commitPathRelativeToRoot has to exist */
        val executionType: TaskInput.ExecutionType = if (shouldCommit) {
            TaskInput.ExecutionType.ProductionRun(
                buildDirectory = gramoExtension.buildDirectory,
                commitDirectory = project.rootProject.projectDir.resolve(commitPathRelativeToRoot)
            )
        } else {
            TaskInput.ExecutionType.DryRun(
                buildDirectory = gramoExtension.buildDirectory
            )
        }

        return TaskInput(
            archetype = archetype,
            baseModuleName = baseModuleName,
            configuration = configuration,
            groupId = groupId,
            moduleClassName = moduleClassName,
            executionType = executionType
        )
    }
}

