package com.seatgeek.gramo.gradle.plugin.helper

import com.seatgeek.gramo.gradle.plugin.entity.MergeConflict
import java.io.File

class SolveMergeConflicts {

    operator fun invoke(mergeConflicts: List<MergeConflict>) {
        if (mergeConflicts.isNotEmpty()) {
            println("There were merge conflicts:")

            mergeConflicts.forEach { (target, generated) ->
                generated.copyTo(File("${target.absolutePath}.generated"), overwrite = true)
                println("\tFile at ${target.absolutePath}")
            }

            println("\nMerge resolution is currently an incomplete feature.\n")
            println("All conflicts have had generated code copied to its respective directories as <filename>.generated.")
            println("Please resolve conflicts manually.\n")
        }
    }
}
