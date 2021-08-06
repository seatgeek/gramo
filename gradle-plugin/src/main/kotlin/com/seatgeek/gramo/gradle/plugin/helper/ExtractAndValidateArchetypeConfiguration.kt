package com.seatgeek.gramo.gradle.plugin.helper

import com.seatgeek.gramo.gradle.plugin.entity.ArchetypeConfiguration
import com.seatgeek.gramo.gradle.plugin.entity.ArchetypeConfigurationInputSet
import com.seatgeek.gramo.gradle.plugin.entity.ExtraVariable
import com.seatgeek.gramo.gradle.plugin.entity.TaskInput
import org.gradle.api.Project

class ExtractAndValidateArchetypeConfiguration(
    private val extractArchetypePresetConfiguration: ExtractArchetypePresetConfiguration,
    private val extractArchetypeSchemeConfiguration: ExtractArchetypeSchemeConfiguration
) {

    operator fun invoke(project: Project, taskInput: TaskInput): ArchetypeConfiguration {
        val validatedScheme = extractArchetypeSchemeConfiguration(taskInput).validateScheme()
        val presetConfiguration = extractArchetypePresetConfiguration(taskInput)

        return ArchetypeConfiguration(
            interpolationMap = validatedScheme.extraVariables
                .let { schemeVariables ->
                    if (presetConfiguration.extraVariables != null) {
                        schemeVariables + presetConfiguration.extraVariables
                    } else {
                        schemeVariables
                    }
                }
                .associate { (nullableName, value) ->
                    val name = nullableName ?: throw IllegalArgumentException(
                        "Malformed object: extra_variable entries must have a value for 'name'. " +
                            "Please include this in your scheme.json or appropriate preset."
                    )
                    val resolvedValue = project.property(name)?.toString() ?: value ?: throw IllegalArgumentException(
                        "Extra property '$name' is required, but not supplied. Please include this property and retry"
                    )

                    name to resolvedValue
                }
                .plus(
                    mapOf(
                        "GROUP_ID" to taskInput.groupId,
                        "MODULE_NAME" to taskInput.baseModuleName.toLowerCase(),
                        "MODULE_CLASS_NAME" to taskInput.moduleClassName,
                        "ROOT_PACKAGE" to taskInput.groupId.toLowerCase(),
                        "ROOT_PACKAGE_PATH" to taskInput.groupId.replace('.', '/'),
                        "VERSION" to taskInput.versionString
                    )
                ),
            // TODO: Could use some better configuration error detection here.
            tags = validatedScheme.availableTags
                .let { availableSchemeTags ->
                    if (presetConfiguration.availableTags != null) {
                        availableSchemeTags + presetConfiguration.availableTags
                    } else {
                        availableSchemeTags
                    }
                }
                .filter { possibleTag ->
                    val userSpecifiedTag = System.getProperty(possibleTag)
                    when {
                        userSpecifiedTag != null -> true
                        presetConfiguration.tags != null -> presetConfiguration.tags.contains(possibleTag)
                        else -> validatedScheme.defaultTags.contains(possibleTag)
                    }
                }
                .toSet()
        )
    }

    private fun ArchetypeConfigurationInputSet.validateScheme(): ValidatedScheme = run {
        ValidatedScheme(
            allowConflictMerge = allowConflictMerge
                ?: throw missingRequiredSchemeKey("allow_conflict_merge", "Boolean"),
            availableTags = availableTags
                ?: throw missingRequiredSchemeKey("available_tags", "Set<String>"),
            extraVariables = extraVariables ?: emptySet(),
            defaultTags = tags ?: emptySet()
        )
    }

    private fun missingRequiredSchemeKey(key: String, type: String): IllegalStateException {
        // TODO: Link to documentation related to scheme.json and presets once it's available.
        return IllegalStateException("$key of type $type not specified in this archetype's scheme.json. Fix that and try again.")
    }

    data class ValidatedScheme(
        val allowConflictMerge: Boolean,
        val availableTags: Set<String>,
        val extraVariables: Set<ExtraVariable>,
        val defaultTags: Set<String>
    )
}