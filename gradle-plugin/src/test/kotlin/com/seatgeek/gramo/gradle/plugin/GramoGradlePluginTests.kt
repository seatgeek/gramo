package com.seatgeek.gramo.gradle.plugin

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.then
import org.gradle.api.Project
import org.gradle.api.Project.DEFAULT_VERSION
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.tasks.TaskContainer
import org.mockito.BDDMockito.given
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object GramoGradlePluginTests : Spek({
    val plugin by memoized { GramoGradlePlugin() }

    describe("apply") {
        val mockProject by memoized { mock<Project>() }
        val mockExtension by memoized { mock<GramoGradleExtension>() }

        val mockRootProject by memoized { mock<Project>() }
        val mockExtensionContainer by memoized { mock<ExtensionContainer>() }
        val mockTaskContainer by memoized { mock<TaskContainer>() }

        beforeEachTest {
            given(mockProject.rootProject).willReturn(mockRootProject)
            given(mockProject.version).willReturn("0.2.1")
            given(mockProject.extensions).willReturn(mockExtensionContainer)
            given(mockProject.tasks).willReturn(mockTaskContainer)

            given(mockExtensionContainer.create(any(), eq(GramoGradleExtension::class.java), eq(mockProject)))
                .willReturn(mockExtension)

            given(mockRootProject.allprojects).willReturn(emptySet())
        }

        it("should create the gramo extension") {
            plugin.apply(mockProject)

            then(mockExtensionContainer).should().create("gramo", GramoGradleExtension::class.java, mockProject)
        }

        describe("given the target project version is not set") {
            beforeEachTest {
                given(mockProject.version).willReturn(DEFAULT_VERSION)
            }

            it("should set the version to blank allowing the task to fail") {
                plugin.apply(mockProject)

                assertThat(plugin.extension.versionString).isEmpty()
            }
        }

        describe("given the target project version is set") {
            beforeEachTest {
                given(mockProject.version).willReturn("0.2.1")
            }

            it("should set the version to according to the target project") {
                plugin.apply(mockProject)

                assertThat(plugin.extension.versionString).isEqualTo("0.2.1")
            }
        }
    }
})
