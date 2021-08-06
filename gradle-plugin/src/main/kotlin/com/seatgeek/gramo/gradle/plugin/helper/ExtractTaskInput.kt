package com.seatgeek.gramo.gradle.plugin.helper

import com.seatgeek.gramo.gradle.plugin.GramoGenerateSubmoduleTask
import com.seatgeek.gramo.gradle.plugin.entity.TaskInput
import java.io.File

/**
 * TODO: See if this is still accurate when done
 * Although side effects are sprinkled throughout the rest of the helper logic, this class should ensure that all input
 * sources have been consolidated. This includes [System.getProperties], task inputs, and extension overrides.
 */
class ExtractTaskInput(private val validateDirectoryExists: ValidateDirectoryExists) {

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

        if (preset.isBlank()) {
            throw IllegalArgumentException("Please specify arguments: --preset=<e.g. default>")
        }

        /** TODO: Ensure commitPathRelativeToRoot has to exist */
        val executionType: TaskInput.ExecutionType = if (shouldCommit) {
            TaskInput.ExecutionType.ProductionRun(
                commitDirectory = project.rootProject.projectDir.resolve(commitPathRelativeToRoot)
            )
        } else {
            TaskInput.ExecutionType.DryRun
        }

        return TaskInput(
            archetypeDirectory = findSpecifiedArchetypeDirectory(),
            buildDirectory = gramoExtension.buildDirectory,
            baseModuleName = baseModuleName,
            preset = preset,
            groupId = groupId,
            moduleClassName = moduleClassName,
            executionType = executionType,
            versionString = gramoExtension.versionString
        )
    }

    private fun GramoGenerateSubmoduleTask.findSpecifiedArchetypeDirectory(): File {
        val archetypesDirectory = gramoExtension.rootProjectDirectory
            .resolve(gramoExtension.archetypesPath)
            .apply { validateDirectoryExists(this) }

        return archetypesDirectory
            .resolve("$archetype.archetype")
            .apply { validateDirectoryExists(this) }
    }
}

