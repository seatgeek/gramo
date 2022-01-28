package com.seatgeek.gramo.gradle.plugin.entity

import java.io.File

data class MergeConflict(val target: File, val newFile: File)
