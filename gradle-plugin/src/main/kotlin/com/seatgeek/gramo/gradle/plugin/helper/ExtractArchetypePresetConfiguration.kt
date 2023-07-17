package com.seatgeek.gramo.gradle.plugin.helper

import com.google.gson.Gson
import com.seatgeek.gramo.gradle.plugin.entity.ArchetypeConfigurationInputSet
import com.seatgeek.gramo.gradle.plugin.entity.TaskInput

class ExtractArchetypePresetConfiguration(
    private val validateDirectoryExists: ValidateDirectoryExists,
    private val gson: Gson,
) {

    operator fun invoke(taskInput: TaskInput): ArchetypeConfigurationInputSet {
        val presetFile = taskInput.archetypeDirectory
            .resolve("presets")
            .apply { validateDirectoryExists(this) }
            .resolve("${taskInput.preset}.json")
            .apply {
                if (!exists()) {
                    throw IllegalArgumentException("Malformed archetype: preset file $this does not exist.")
                }
            }

        return runCatching {
            gson.fromJson(presetFile.reader(), ArchetypeConfigurationInputSet::class.java)
        }.fold(
            onSuccess = { it },
            onFailure = { jsonParseException ->
                val message = "Unable to parse preset file: $presetFile."
                System.err.println("Malformed archetype error:")
                System.err.println(
                    "\t$message " +
                        "Consider reviewing the archetype at ${taskInput.archetypeDirectory} for a malformed preset file.",
                )

                throw IllegalArgumentException(message, jsonParseException)
            },
        )
    }
}
