package com.seatgeek.gramo.gradle.plugin

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.mock
import org.gradle.api.Project
import org.gradle.api.Project.DEFAULT_VERSION
import org.mockito.BDDMockito.given
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object GramoGradlePluginTests : Spek({
    val plugin by memoized { GramoGradlePlugin() }

    describe("apply") {
        val mockProject by memoized { mock<Project>() }

        beforeEachTest {
            given(mockProject.version).willReturn("0.2.1")
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
