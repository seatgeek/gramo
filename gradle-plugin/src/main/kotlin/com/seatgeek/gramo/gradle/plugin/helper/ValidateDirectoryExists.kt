package com.seatgeek.gramo.gradle.plugin.helper

import java.io.File

class ValidateDirectoryExists {
    operator fun invoke(directory: File) {
        if (!directory.exists()) {
            throw IllegalStateException("Required directory, $directory, not found.")
        }

        if (!directory.isDirectory) {
            throw IllegalStateException("File at path, $directory, is not a directory.")
        }
    }
}