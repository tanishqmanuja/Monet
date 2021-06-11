buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.1.0-alpha01")
        classpath(kotlin("gradle-plugin", "1.5.10"))
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://androidx.dev/snapshots/builds/7447403/artifacts/repository")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://jitpack.io")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}