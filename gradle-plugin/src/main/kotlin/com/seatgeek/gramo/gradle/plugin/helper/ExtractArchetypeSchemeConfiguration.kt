package com.seatgeek.gramo.gradle.plugin.helper

import com.google.gson.Gson
import com.seatgeek.gramo.gradle.plugin.entity.ArchetypeConfigurationInputSet
import com.seatgeek.gramo.gradle.plugin.entity.TaskInput

class ExtractArchetypeSchemeConfiguration(
    private val validateDirectoryExists: ValidateDirectoryExists,
    private val gson: Gson
) {

    operator fun invoke(taskInput: TaskInput): ArchetypeConfigurationInputSet {
        val schemeFile = taskInput.archetypeDirectory
            .resolve("scheme.json")
            .apply {
                if (!exists()) {
                    throw IllegalArgumentException("Malformed archetype: scheme file $this does not exist.")
                }
            }

        return runCatching {
            gson.fromJson(schemeFile.reader(), ArchetypeConfigurationInputSet::class.java)
        }.fold(
            onSuccess = { it },
            onFailure = { jsonParseException ->
                val message = "Unable to parse scheme file: ${schemeFile}."
                System.err.println("Malformed archetype error:")
                System.err.println("\t$message " +
                    "Consider reviewing the archetype at ${taskInput.archetypeDirectory} for a malformed scheme.json.")

                throw IllegalArgumentException(message, jsonParseException)
            }
        )
    }
}