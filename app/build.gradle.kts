plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    compileSdk = 31
    buildToolsVersion = "31.0.0"

    defaultConfig {
        applicationId = "com.kyant.monetdemo"
        minSdk = 28
        targetSdk = 31
        versionCode = 9
        versionName = "0.5.0"
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
        kotlinCompilerExtensionVersion = "1.1.0-SNAPSHOT"
    }
}

dependencies {
    val compose = "1.1.0-SNAPSHOT"
    val accompanist = "0.15.0-SNAPSHOT"
    implementation(project(":materialyou"))
    implementation(project(":monet"))
    implementation("androidx.activity:activity-compose:1.4.0-SNAPSHOT")
    implementation("androidx.appcompat:appcompat:1.4.0-alpha03")
    implementation("androidx.compose.material:material:$compose")
    implementation("androidx.compose.material:material-icons-extended:$compose")
    implementation("androidx.compose.ui:ui:$compose")
    implementation("androidx.compose.ui:ui-tooling:$compose")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.0-alpha08")
    implementation("androidx.core:core-ktx:1.7.0-SNAPSHOT")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.0-SNAPSHOT")
    implementation("androidx.navigation:navigation-compose:2.4.0-SNAPSHOT")
    implementation("com.github.dhaval2404:imagepicker:2.1")
    implementation("com.google.accompanist:accompanist-flowlayout:$accompanist")
    implementation("com.google.accompanist:accompanist-insets:$accompanist")
    implementation("com.google.accompanist:accompanist-pager:$accompanist")
    implementation("com.google.accompanist:accompanist-systemuicontroller:$accompanist")
    implementation("com.google.android.material:material:1.4.0")
    implementation("io.coil-kt:coil-compose:1.3.0")
}