package com.seatgeek.gramo.gradle.plugin.helper

import com.seatgeek.gramo.gradle.plugin.entity.ArchetypeConfiguration
import com.seatgeek.gramo.gradle.plugin.entity.TaskInput
import java.io.File

class TransformArchetype(
    private val tokenizeAndTransformDocument: TokenizeAndTransformDocument
) {

    operator fun invoke(taskInput: TaskInput, archetypeConfiguration: ArchetypeConfiguration) {
        scanDirectoryAndTransformRecursive(
            archetypeConfiguration = archetypeConfiguration,
            contentRoot = taskInput.buildDirectory,
            directory = taskInput.buildDirectory
        )
    }

    private fun scanDirectoryAndTransformRecursive(
        archetypeConfiguration: ArchetypeConfiguration,
        contentRoot: File,
        directory: File
    ) {
        val transformedDirectory = if (contentRoot != directory) {
            println("Transforming directory: $directory")
            transformFileName(
                archetypeConfiguration = archetypeConfiguration,
                file = directory
            )
        } else {
            directory
        }

        transformedDirectory
            ?.listFiles()
            ?.forEach { file ->
                if (file.isDirectory) {
                    scanDirectoryAndTransformRecursive(
                        archetypeConfiguration = archetypeConfiguration,
                        contentRoot = contentRoot,
                        directory = file
                    )
                } else {
                    println("Transforming markup in file: $file")
                    transformFileName(
                        archetypeConfiguration = archetypeConfiguration,
                        file = file
                    )?.also { renamedFile ->
                        transformFileContent(
                            archetypeConfiguration = archetypeConfiguration,
                            file = renamedFile
                        )
                    }
                }
            }
    }

    private fun transformFileName(
        archetypeConfiguration: ArchetypeConfiguration,
        file: File
    ): File? {
        val transformedFileName = tokenizeAndTransformDocument(archetypeConfiguration, file.name)
        if (transformedFileName.isNotBlank()) {
            return if (transformedFileName != file.name) {
                val newFile = file.parentFile.resolve(transformedFileName)

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

                println("Transformed $file to $newFile")

                newFile
            } else {
                file
            }
        } else {
            println("Markup and preset determined not to include file: $file")
            if (!file.deleteRecursively()) {
                throw IllegalStateException("Unable to delete file: $file.")
            }

            return null
        }
    }

    private fun transformFileContent(archetypeConfiguration: ArchetypeConfiguration, file: File) {
        val transformedDocument = tokenizeAndTransformDocument(archetypeConfiguration, file.readText())
        println("Transformed $file contents to:\n$transformedDocument")
        file.delete()
        file.writeText(transformedDocument)
    }
}
