import dev.mokkery.gradle.ApplicationRule
import org.gradle.kotlin.dsl.sourceSets
import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeFeatureFlag
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.mokkery)
    kotlin("plugin.serialization") version "2.1.0"
}

mokkery {
    rule.set(ApplicationRule.All)
}

kotlin {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }

        instrumentedTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)

        dependencies {
            androidTestImplementation(libs.androidx.ui.test.junit4.android)
            debugImplementation(libs.androidx.ui.test.manifest)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "posts"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.network)
            implementation(projects.ds)
            implementation(projects.utils)
            implementation(projects.storage)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            // ViewModel
            implementation(libs.androidx.lifecycle.viewmodel)

            // Serialization
            implementation(libs.kotlinx.serialization)

            // DateTime
            implementation(libs.dateTime)

            // Coil
            implementation(libs.coil)
            implementation(libs.coil.compose)
            implementation(libs.coil.ktor)

            // Koin - DI
            implementation(libs.koin.compose)
            implementation(libs.koin.core)

            // DataStore
            implementation(libs.dataStore.preferences)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.coroutines.test)
            implementation(libs.koin.test)
            implementation(kotlin("test-annotations-common"))

            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
        }
    }

    composeCompiler {
        featureFlags.addAll(
            ComposeFeatureFlag.StrongSkipping,
            ComposeFeatureFlag.OptimizeNonSkippingGroups
        )
    }
}

android {
    namespace = "com.rcudev.posts"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
        lint.targetSdk = libs.versions.android.targetSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    configurations
        .filter { it.name.startsWith("ksp") && it.name.contains("Test") }
        .forEach {
            add(it.name, "io.mockative:mockative-processor:2.2.2")
        }
}