package com.seatgeek.gramo.gradle.plugin.helper

import com.seatgeek.gramo.gradle.plugin.entity.ArchetypeConfiguration
import com.seatgeek.gramo.gradle.plugin.entity.MarkupTokenTransformation

class TokenizeAndTransformDocument {

    operator fun invoke(archetypeConfiguration: ArchetypeConfiguration, document: String): String {
        return tokenizeAndTransformDocumentRecursive(
            archetypeConfiguration = archetypeConfiguration,
            document = document,
            offset = 0,
        )
    }

    private fun tokenizeAndTransformDocumentRecursive(
        archetypeConfiguration: ArchetypeConfiguration,
        document: String,
        offset: Int,
    ): String {
        var transformedDocument = document
        var firstOpenIndex: Int

        while (transformedDocument
                .indexOf(gramoOpeningTag, startIndex = offset)
                .also { firstOpenIndex = it } > -1
        ) {
            println("Opening tag found at index: $firstOpenIndex")

            val nextOpen = transformedDocument.indexOf(gramoOpeningTag, startIndex = firstOpenIndex + 1)
            val nextClose = transformedDocument.indexOf(gramoClosingTag, startIndex = firstOpenIndex + 1)

            println("Subsequent tags found at $nextOpen for next open and $nextClose for next close")

            if (nextClose == -1) {
                throw IllegalStateException(
                    "Malformed gramo tag at:\n${
                        transformedDocument.substring(
                            startIndex = firstOpenIndex,
                            endIndex = (firstOpenIndex + 100).coerceAtMost(transformedDocument.length - 1),
                        )
                    }",
                )
            }

            transformedDocument = if (nextClose < nextOpen || nextOpen == -1) {
                val tokenTransformation = transformGramoToken(
                    archetypeConfiguration = archetypeConfiguration,
                    document = transformedDocument,
                    tokenStart = firstOpenIndex,
                )

                transformedDocument.replaceRange(
                    startIndex = tokenTransformation.startIndex,
                    endIndex = tokenTransformation.endIndex,
                    replacement = tokenTransformation.value,
                )
            } else {
                tokenizeAndTransformDocumentRecursive(archetypeConfiguration, transformedDocument, offset = nextOpen)
            }
        }

        return transformedDocument
    }

    private fun transformGramoToken(
        archetypeConfiguration: ArchetypeConfiguration,
        document: String,
        tokenStart: Int,
    ): MarkupTokenTransformation {
        val tokenEnd = document
            .indexOf(gramoClosingTag, startIndex = tokenStart)
            .plus(gramoClosingTag.length)

        val attributesLine = requireNotNull(gramoAttributeRegex.find(document, startIndex = tokenStart)) {
            "Error occurred while searching for attributes in subdocument: ${document.substring(tokenStart, tokenEnd)}"
        }

        val trimmedAttributesLine = attributesLine.value.trim()

        println("Generating match for subdocument: $document")

        if (trimmedAttributesLine == "interpolate") {
            val contentResult = requireNotNull(gramoInterpolationContentRegex.find(document)) {
                "Error occurred while searching for tag content in $document"
            }

            val trimmedNodeContent = contentResult.value.trim()

            println("Matched interpolate attribute: ${contentResult.value}")

            return MarkupTokenTransformation(
                startIndex = tokenStart,
                endIndex = tokenEnd,
                value = requireNotNull(archetypeConfiguration.interpolationMap[trimmedNodeContent]) {
                    "$trimmedNodeContent is not a known key for interpolation."
                },
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
                            "tags" -> archetypeConfiguration.tags.contains(conditionParts[1])
                            else -> throw IllegalArgumentException("Unknown variable type in 'includeIf' attribute value: ${conditionParts[0]}")
                        }.let { isMatchingTag ->
                            if (negate) {
                                !isMatchingTag
                            } else {
                                isMatchingTag
                            }
                        }
                    }
                }
                .all { it }

            val tokenReplacement = if (allConditionsMet) {
                document.substring(
                    startIndex = tokenStart + gramoOpeningTag.length + attributesLine.value.length + 1,
                    endIndex = tokenEnd - gramoClosingTag.length,
                )
            } else {
                ""
            }

            return MarkupTokenTransformation(
                startIndex = tokenStart,
                endIndex = tokenEnd,
                value = tokenReplacement,
            )
        }

        throw IllegalStateException(
            "No known attributes matched at node: ${document.substring(tokenStart, tokenEnd)}",
        )
    }

    companion object {
        private const val gramoOpeningTag = "<gramo::"
        private const val gramoClosingTag = "<::gramo>"

        private const val gramoInterpolateAttribute = "interpolate"
        private const val gramoIncludeIfAttribute = "includeIf"

        private val gramoAttributeRegex = Regex("(?<=<gramo::)[^>]*(?=>)")
        private val gramoInterpolationContentRegex = Regex("((?<=<gramo::$gramoInterpolateAttribute>).*?(?=<::gramo>))", RegexOption.DOT_MATCHES_ALL)
    }
}
