plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    id("com.louiscad.complete-kotlin") version "1.1.0"
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.realm.plugin)


}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
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
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.android)
        }
        commonMain.dependencies {
            implementation("io.ktor:ktor-client-logging:2.3.9")
            implementation("co.touchlab:kermit:2.0.3")
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            // Koin
            implementation(libs.koin.core)

            // Voyager
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.koin)
            implementation(libs.voyager.screen.model)
            implementation(libs.voyager.transitions)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.kotlinx.datetime)

            implementation(libs.multiplatform.settings.no.arg)
            implementation(libs.multiplatform.settings.coroutines)
            implementation(libs.mongodb.realm)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.stately.common)

            implementation("io.mockk:mockk-common:1.12.0")  // Use the latest version suitable for KMM
            // implementation("junit:junit:4.13.2")
            implementation(libs.kotlin.test)
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
            implementation("org.jetbrains.kotlin:kotlin-test-common")
            implementation("org.jetbrains.kotlin:kotlin-test-annotations-common")


        }
        commonTest.dependencies {
            implementation(kotlin("test-common"))
            implementation(kotlin("test-annotations-common"))
            implementation("io.mockk:mockk-common:1.12.0")
            implementation("org.jetbrains.kotlin:kotlin-test-junit")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        jvmTest.dependencies {
            commonTest
            implementation("io.kotlintest:kotlintest-runner-junit5:3.3.2")
            implementation("org.jetbrains.kotlin:kotlin-reflect:${libs.kotlin}")
            implementation("org.jetbrains.kotlin:kotlin-test-junit")
            implementation("org.junit.jupiter:junit-jupiter-api:5.5.2")

            implementation("org.junit.jupiter:junit-jupiter-params:5.5.2")
        }


    }
}



android {
    namespace = "org.example.currency"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml") // C:\Users\Yodgorbek\Currency\androidApp\src\main\AndroidManifest.xml
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "org.example.currency"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
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
    dependencies {
        debugImplementation(libs.compose.ui.tooling)
    }
}
dependencies {
    implementation("androidx.core:core-ktx:+")
    implementation("androidx.core:core-ktx:+")
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.13.3")
    testImplementation(libs.kotlin.test)
    testImplementation("org.jetbrains.kotlin:kotlin-test-common")
    testImplementation("org.jetbrains.kotlin:kotlin-test-annotations-common")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
}