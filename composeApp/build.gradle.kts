import dev.mokkery.gradle.ApplicationRule
import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.mokkery)
    kotlin("plugin.serialization") version "2.2.0"
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
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            // Splash
            implementation(libs.androidx.splash)

            // Ktor
            implementation(libs.ktor.android)
        }
        iosMain.dependencies {
            // Ktor
            implementation(libs.ktor.ios)
        }
        commonMain.dependencies {
            implementation(projects.network)
            implementation(projects.ds)
            implementation(projects.utils)
            implementation(projects.posts)
            implementation(projects.storage)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            // Arch
            implementation(libs.molecule.runtime)

            // Serialization
            implementation(libs.kotlinx.serialization)

            // Coil
            implementation(libs.coil)
            implementation(libs.coil.compose)
            implementation(libs.coil.ktor)

            // Koin - DI
            implementation(libs.koin.compose)
            implementation(libs.koin.core)

            // Navigation
            implementation(libs.navigation.compose)

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
            implementation(libs.turbine)
        }
    }
}

android {
    namespace = "com.rcudev.stargazer"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "com.rcudev.stargazer"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
    dependencies {
        androidTestImplementation(libs.androidx.ui.test.junit4.android)
        debugImplementation(libs.androidx.ui.test.manifest)
        debugImplementation(compose.uiTooling)
    }
}

dependencies {
    configurations
        .filter { it.name.startsWith("ksp") && it.name.contains("Test") }
        .forEach {
            add(it.name, "io.mockative:mockative-processor:2.2.2")
        }
}

