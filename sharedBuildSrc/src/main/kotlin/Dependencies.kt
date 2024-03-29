object Dependencies {
    val targets = Targets
    object Targets {
        const val jvm = "org.jetbrains.kotlin.jvm"
        const val gradlePlugin = "org.gradle.java-gradle-plugin"
    }

    val plugins = Plugins
    object Plugins {
        const val idea = "idea"
        const val ktlint = "org.jmailen.kotlinter"
    }

    val project = Project
    object Project {
        val kotlin = Kotlin
        object Kotlin {
            const val gradlePluginApi = "org.jetbrains.kotlin:kotlin-gradle-plugin-api:${Versions.kotlin}"
        }

        val google = Google
        object Google {
            const val gson = "com.google.code.gson:gson:${Versions.google.gson}"
        }
    }

    val test = Test
    object Test {
        const val mockito = "com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.test.mockito}"

        val spek = Spek
        object Spek {
            const val jvm = "org.spekframework.spek2:spek-dsl-jvm:${Versions.test.spek}"
            const val runner = "org.spekframework.spek2:spek-runner-junit5:${Versions.test.spek}"
        }

        const val truth = "com.google.truth:truth:${Versions.test.truth}"
    }
}
