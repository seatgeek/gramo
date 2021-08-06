package com.seatgeek.gramo.gradle.plugin.helper

import com.seatgeek.gramo.gradle.plugin.entity.MergeConflict
import java.io.File

class SolveMergeConflicts {
    operator fun invoke(
        targetDirectory: File,
        buildDirectory: File,
        merger: String,
        mergeConflicts: List<MergeConflict>
    ) {

    }
}