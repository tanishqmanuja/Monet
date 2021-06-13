plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    compileSdk = 30
    buildToolsVersion = "31.0.0 rc5"

    defaultConfig {
        minSdk = 21
        targetSdk = 30
        version = "0.1.0-alpha01"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = listOf(
            "-Xopt-in=kotlin.RequiresOptIn",
            "-P",
            "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=true"
        )
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.0.0-SNAPSHOT"
    }
}

dependencies {
    val compose = "1.0.0-SNAPSHOT"
    val accompanist = "0.12.0-SNAPSHOT"
    implementation(project(":monet"))
    implementation("androidx.appcompat:appcompat:1.4.0-alpha02")
    implementation("androidx.compose.ui:ui:$compose")
    implementation("androidx.compose.material:material:$compose")
    implementation("androidx.compose.material:material-icons-extended:$compose")
    implementation("androidx.compose.ui:ui-tooling:$compose")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.0-alpha07")
    implementation("androidx.core:core-ktx:1.7.0-SNAPSHOT")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.0-SNAPSHOT")
    implementation("androidx.navigation:navigation-compose:2.4.0-SNAPSHOT")
    implementation("com.github.haifengl:smile-kotlin:2.6.0")
    implementation("com.google.accompanist:accompanist-coil:$accompanist")
    implementation("com.google.accompanist:accompanist-flowlayout:$accompanist")
    implementation("com.google.accompanist:accompanist-insets:$accompanist")
    implementation("com.google.accompanist:accompanist-pager:$accompanist")
    implementation("com.google.accompanist:accompanist-systemuicontroller:$accompanist")
}