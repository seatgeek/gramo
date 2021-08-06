package com.seatgeek.gramo.gradle.plugin

import com.google.gson.Gson
import com.seatgeek.gramo.gradle.plugin.entity.ArchetypeConfiguration
import com.seatgeek.gramo.gradle.plugin.entity.TaskInput
import com.seatgeek.gramo.gradle.plugin.helper.ExtractTaskInput
import java.io.File
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

open class GramoGenerateSubmoduleTask : DefaultTask() {

    private val extractTaskInput = ExtractTaskInput()

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
    @set:Option(option = "configuration", description = "The archetype configuration to use")
    var configuration: String = "default"

    @get:Input
    @set:Option(option = "commit_path", description = "Determines where to fully commit this module's source code to relative to the project root")
    var commitPathRelativeToRoot: String = ""

    @get:Input
    @set:Option(option = "commit", description = "Determines whether to commit this code to its destination specified by --commit_path")
    var shouldCommit: Boolean = false

    @get:Input
    lateinit var gramoExtension: GramoGradleExtension

    private val interpolationMap by lazy {
        mapOf(
            "GROUP_ID" to groupId,
            "MODULE_NAME" to baseModuleName.toLowerCase(),
            "MODULE_CLASS_NAME" to moduleClassName,
            "ROOT_PACKAGE" to groupId.toLowerCase(),
            "ROOT_PACKAGE_PATH" to groupId.replace('.', '/'),
            "VERSION" to gramoExtension.versionString
        )
    }

    private lateinit var tags: List<String>

    @TaskAction
    fun run() {
        val taskInput = extractTaskInput(this)
        generateModuleFromArchetype()

        when (val execution = taskInput.executionType) {
            is TaskInput.ExecutionType.DryRun -> {
                println("\n~~~~~~~~~~~~~~~~~~~~~ Gramo ~~~~~~~~~~~~~~~~~~~~~~~\n")
                println("Generated code can be found at ${execution.buildDirectory} for your inspection.\n")
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
                // TODO: Ensure there are not any uncommitted changes before continuing
                moveModuleIntoPlace()

                println("\n~~~~~~~~~~~~~~~~~~~~~ Gramo ~~~~~~~~~~~~~~~~~~~~~~~\n")
                println("Generated code can be found at ${execution.commitDirectory}\n")
                println("Be sure to include the new module/modules in your settings gradle file and make any required adjustments.")
                println("It's recommended to use an auto-formatter to convert this new code to your team's style.")
                println("\n~~~~~~~~~~~~~~~~~~~~~ Gramo ~~~~~~~~~~~~~~~~~~~~~~~\n")
            }
        }
    }

    private fun generateModuleFromArchetype() {
        val archetypeDirectory = findSpecifiedArchetypeDirectory()
        val contentDirectory = resolveArchetypeContent(archetypeDirectory)

        gramoExtension.buildDirectory.deleteRecursively()
        contentDirectory.copyRecursively(gramoExtension.buildDirectory, overwrite = true) { _, exception ->
            throw exception
        }

        // STOPSHIP: This default wasn't here
        tags = extractArchetypeConfiguration(archetypeDirectory).tags ?: listOf()

        generateAndRecurseDirectories(
            contentRoot = gramoExtension.buildDirectory,
            directory = gramoExtension.buildDirectory
        )
    }

    private fun moveModuleIntoPlace() {
        val commitDirectory = project.rootProject.projectDir
            .resolve(requireNotNull(commitPathRelativeToRoot))

        gramoExtension.buildDirectory
            .copyRecursively(commitDirectory, overwrite = false) { _, exception ->
                throw exception
            }
    }

    private fun resolveArchetypeContent(archetypeDirectory: File): File = archetypeDirectory
        .resolve("content")
        .apply { validateDirectoryExists(this) }

    private fun extractArchetypeConfiguration(archetypeDirectory: File): ArchetypeConfiguration {
        val configurationReader = archetypeDirectory
            .resolve("configurations")
            .apply { validateDirectoryExists(this) }
            .resolve("$configuration.json")
            .apply {
                if (!exists()) {
                    throw IllegalArgumentException("Input option, --configuration, could not be resolved at $this")
                }
            }
            .reader()

        return Gson().fromJson(configurationReader, ArchetypeConfiguration::class.java)
    }

    private fun generateAndRecurseDirectories(contentRoot: File, directory: File) {

        val renamedDirectory = if (contentRoot != directory) {
            println("Processing file: $directory")
            generateNewFileName(directory)
        } else {
            directory
        }

        renamedDirectory
            ?.listFiles()
            ?.forEach { file ->
                if (file.isDirectory) {
                    generateAndRecurseDirectories(contentRoot, file)
                } else {
                    println("Processing file: $file")
                    generateNewFileName(file)
                        ?.apply { rewriteFileContent(this) }
                }
            }
    }

    private val gramoOpeningTag = "<gramo::"
    private val gramoClosingTag = "<::gramo>"

    private val gramoInterpolateAttribute = "interpolate"
    private val gramoIncludeIfAttribute = "includeIf"

    private val gramoAttributeRegex = Regex("(?<=<gramo::)[^>]*(?=>)")
    private val gramoInterpolationContentRegex = Regex("((?<=<gramo::$gramoInterpolateAttribute>).*?(?=<::gramo>))", RegexOption.DOT_MATCHES_ALL)

    private fun interpolateString(document: String, offset: Int = 0): String {
        var interpolatedDocument = document
        var firstOpenIndex: Int

        while (interpolatedDocument
                .indexOf(gramoOpeningTag, startIndex = offset)
                .also { firstOpenIndex = it } > -1) {

            println("Opening tag found at index: $firstOpenIndex")

            val nextOpen = interpolatedDocument.indexOf(gramoOpeningTag, startIndex = firstOpenIndex + 1)
            val nextClose = interpolatedDocument.indexOf(gramoClosingTag, startIndex = firstOpenIndex + 1)

            println("Subsequent tags found at $nextOpen for next open and $nextClose for next close")

            if (nextClose == -1) {
                throw IllegalStateException(
                    "Malformed gramo tag at:\n${
                        interpolatedDocument.substring(
                            startIndex = firstOpenIndex,
                            endIndex = (firstOpenIndex + 100).coerceAtMost(interpolatedDocument.length - 1))
                    }"
                )
            }

            interpolatedDocument = if (nextClose < nextOpen || nextOpen == -1) {
                val newSegment = generateForSubDocument(document = interpolatedDocument, subDocumentStart = firstOpenIndex)

                interpolatedDocument.replaceRange(
                    startIndex = newSegment.startIndex,
                    endIndex = newSegment.endIndex,
                    replacement = newSegment.value
                )
            } else {
                interpolateString(interpolatedDocument, offset = nextOpen)
            }
        }

        return interpolatedDocument
    }

    private fun generateForSubDocument(document: String, subDocumentStart: Int): GramoSegmentReplacement {
        val subDocumentEnd = document
            .indexOf(gramoClosingTag, startIndex = subDocumentStart)
            .plus(gramoClosingTag.length)

        val attributesLine = requireNotNull(gramoAttributeRegex.find(document, startIndex = subDocumentStart)) {
            "Error occurred while searching for attributes in subdocument: ${document.substring(subDocumentStart, subDocumentEnd)}"
        }

        val trimmedAttributesLine = attributesLine.value.trim()

        println("Generating match for subdocument: $document")

        if (trimmedAttributesLine == "interpolate") {
            val contentResult = requireNotNull(gramoInterpolationContentRegex.find(document)) {
                "Error occurred while searching for tag content in $document"
            }

            val trimmedNodeContent = contentResult.value.trim()

            println("Matched interpolate attribute: ${contentResult.value}")

            return GramoSegmentReplacement(
                startIndex = subDocumentStart,
                endIndex = subDocumentEnd,
                value = requireNotNull(interpolationMap[trimmedNodeContent]) {
                    "$trimmedNodeContent was not a known key for interpolation."
                }
            ).also {
                println("Returning $it")
            }
        }

        if (trimmedAttributesLine.startsWith("$gramoIncludeIfAttribute=")) {
            val conditionsLine = trimmedAttributesLine.substring(gramoIncludeIfAttribute.length + 1)
            val allConditionsMet = conditionsLine.split(',', ignoreCase = true)
                .map { singleConditionString ->
                    val conditionParts = singleConditionString.split(':')
                    if (conditionParts.count() != 2) {
                        throw IllegalArgumentException("Malformed includeIf tag in $document at '$singleConditionString")
                    } else {
                        val (conditionBaseVar, negate) = if (conditionParts[0].firstOrNull() == '!') {
                            conditionParts[0].substring(startIndex = 1) to true
                        } else {
                            conditionParts[0] to false
                        }

                        when (conditionBaseVar) {
                            "tags" -> tags.contains(conditionParts[1])
                            else -> throw IllegalArgumentException("Unknown variable type in 'includeIf' attribute value: ${conditionParts[0]}")
                        }.let {
                            if (negate) {
                                !it
                            } else {
                                it
                            }
                        }
                    }
                }
                .all { it }

            val trimmedNodeContent = if (allConditionsMet) {
                document.substring(
                    startIndex = subDocumentStart + gramoOpeningTag.length + attributesLine.value.length + 1,
                    endIndex = subDocumentEnd - gramoClosingTag.length
                )
            } else {
                ""
            }

            return GramoSegmentReplacement(
                startIndex = subDocumentStart,
                endIndex = subDocumentEnd,
                trimmedNodeContent
            )
        }

        throw IllegalStateException(
            "No known attributes matched at node: ${document.substring(subDocumentStart, subDocumentEnd)}"
        )
    }

    data class GramoSegmentReplacement(val startIndex: Int, val endIndex: Int, val value: String)

    private fun generateNewFileName(file: File): File? {
        val processedFileName = interpolateString(file.name)
        println("Resolved $file to file name: $processedFileName")

        if (processedFileName.isNotBlank()) {
            return if (processedFileName != file.name) {
                val newFile = file.parentFile.resolve(processedFileName)

                if (!file.isDirectory) {
                    newFile.writeBytes(file.readBytes())
                } else {
                    file.copyRecursively(newFile) { _, exception ->
                        throw exception
                    }
                }

                if (!file.deleteRecursively()) {
                    throw IllegalStateException("Unable to delete file: $file")
                }

                println("Created $newFile from $file")
                println("$file has been deleted")

                newFile
            } else {
                file
            }
        } else {
            if (!file.deleteRecursively()) {
                throw IllegalStateException("Unable to delete file: $file.")
            }

            println("$file has been deleted.")

            return null
        }
    }

    private fun rewriteFileContent(file: File) {
        val document = file.readText()
        file.delete()
        val newDocumentText = interpolateString(document)
        println("Rewriting $file contents to:\n$newDocumentText")
        file.writeText(newDocumentText)
    }

    private fun findSpecifiedArchetypeDirectory(): File {
        val archetypesDirectory = gramoExtension.rootProjectDirectory
            .resolve(gramoExtension.archetypesPath)
            .apply { validateDirectoryExists(this) }

        return archetypesDirectory
            .resolve("$archetype.archetype")
            .apply { validateDirectoryExists(this) }
    }

    private fun validateDirectoryExists(file: File) {
        if (!file.exists()) {
            throw IllegalStateException("Required directory, $file, not found.")
        }

        if (!file.isDirectory) {
            throw IllegalStateException("File at path, $file, is not a directory.")
        }
    }
}
