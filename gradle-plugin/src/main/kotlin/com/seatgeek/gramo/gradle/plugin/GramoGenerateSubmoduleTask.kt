package com.seatgeek.gramo.gradle.plugin

import com.google.gson.Gson
import com.seatgeek.gramo.gradle.plugin.entity.TaskInput
import com.seatgeek.gramo.gradle.plugin.helper.CopyArchetypeToBuildDirectory
import com.seatgeek.gramo.gradle.plugin.helper.ExtractAndValidateArchetypeConfiguration
import com.seatgeek.gramo.gradle.plugin.helper.ExtractArchetypePresetConfiguration
import com.seatgeek.gramo.gradle.plugin.helper.ExtractArchetypeSchemeConfiguration
import com.seatgeek.gramo.gradle.plugin.helper.ExtractTaskInput
import com.seatgeek.gramo.gradle.plugin.helper.GenerateSubmodule
import com.seatgeek.gramo.gradle.plugin.helper.MergeTransformedContentIntoCommitDirectory
import com.seatgeek.gramo.gradle.plugin.helper.SolveMergeConflicts
import com.seatgeek.gramo.gradle.plugin.helper.TokenizeAndTransformDocument
import com.seatgeek.gramo.gradle.plugin.helper.TransformArchetype
import com.seatgeek.gramo.gradle.plugin.helper.ValidateDirectoryExists
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

open class GramoGenerateSubmoduleTask : DefaultTask() {

    @get:Input
    @set:Option(option = "module_name", description = "Name of the primary module spinal case")
    var baseModuleName: String = ""

    @get:Input
    @set:Option(option = "name", description = "Name of the primary module")
    var moduleClassName: String = ""

    @get:Input
    @set:Option(option = "group_id", description = "Group ID of your new modules")
    var groupId: String = ""

    @get:Input
    @set:Option(option = "archetype", description = "The archetype to use")
    var archetype: String = ""

    @get:Input
    @set:Option(option = "preset", description = "The archetype preset to use")
    var preset: String = "default"

    @get:Input
    @set:Option(option = "commit_path", description = "Determines where to fully commit this module's source code to relative to the project root")
    var commitPathRelativeToRoot: String = ""

    @get:Input
    @set:Option(option = "commit", description = "Determines whether to commit this code to its destination specified by --commit_path")
    var shouldCommit: Boolean = false

    @get:Input
    lateinit var gramoExtension: GramoGradleExtension

    @TaskAction
    fun run() {
        val gson = Gson()
        val validateDirectoryExists = ValidateDirectoryExists()
        val copyArchetypeToBuildDirectory = CopyArchetypeToBuildDirectory(
            validateDirectoryExists = validateDirectoryExists
        )
        val extractArchetypePresetConfiguration = ExtractArchetypePresetConfiguration(
            validateDirectoryExists = validateDirectoryExists,
            gson = gson
        )
        val extractArchetypeSchemeConfiguration = ExtractArchetypeSchemeConfiguration(
            validateDirectoryExists = validateDirectoryExists,
            gson = gson
        )
        val extractArchetypeConfiguration = ExtractAndValidateArchetypeConfiguration(
            extractArchetypePresetConfiguration = extractArchetypePresetConfiguration,
            extractArchetypeSchemeConfiguration = extractArchetypeSchemeConfiguration
        )
        val extractTaskInput = ExtractTaskInput(
            validateDirectoryExists = validateDirectoryExists
        )
        val tokenizeAndTransformDocument = TokenizeAndTransformDocument()
        val transformArchetype = TransformArchetype(
            tokenizeAndTransformDocument = tokenizeAndTransformDocument
        )
        val mergeTransformedContentIntoCommitDirectory = MergeTransformedContentIntoCommitDirectory()
        val solveMergeConflicts = SolveMergeConflicts()
        val generateSubmodule = GenerateSubmodule(
            copyArchetypeToBuildDirectory = copyArchetypeToBuildDirectory,
            mergeTransformedContentIntoCommitDirectory = mergeTransformedContentIntoCommitDirectory,
            transformArchetype = transformArchetype
        )

        val taskInput = extractTaskInput(this)
        val archetypeConfiguration = extractArchetypeConfiguration(project, taskInput)

        println("Computed task inputs: $taskInput")

        val mergeConflicts = generateSubmodule(
            archetypeConfiguration = archetypeConfiguration,
            taskInput = taskInput
        )

        when (val execution = taskInput.executionType) {
            is TaskInput.ExecutionType.DryRun -> {
                println("\n~~~~~~~~~~~~~~~~~~~~~ Gramo ~~~~~~~~~~~~~~~~~~~~~~~\n")
                println("Generated code can be found at ${taskInput.buildDirectory} for your inspection.\n")
                println(
                    "If you would like to commit this to the codebase, ensure you've committed your code to " +
                        "git and run the generateSubmodule task again with the command line option --commit.\n\n" +
                        "By default the code will be committed in the project's root directory, " +
                        "${project.rootProject.projectDir}, but that can be changed by including the intended" +
                        "path relative to the project root using: --commit_path=<path_to_existing_module>."
                )
                println("\n~~~~~~~~~~~~~~~~~~~~~ Gramo ~~~~~~~~~~~~~~~~~~~~~~~\n")
            }
            is TaskInput.ExecutionType.ProductionRun -> {
                println("\n~~~~~~~~~~~~~~~~~~~~~ Gramo ~~~~~~~~~~~~~~~~~~~~~~~\n")
                println("Generated code can be found at ${execution.commitDirectory}\n")
                println("Be sure to include the new module/modules in your settings gradle file and make any required adjustments.")
                println("It's recommended to use an auto-formatter to convert this new code to your team's style.\n")

                solveMergeConflicts(mergeConflicts = mergeConflicts)

                println("~~~~~~~~~~~~~~~~~~~~~ Gramo ~~~~~~~~~~~~~~~~~~~~~~~")
            }
        }
    }
}
