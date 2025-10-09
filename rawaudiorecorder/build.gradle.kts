plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
    id("com.vanniktech.maven.publish") version "0.34.0"
}

android {
    namespace = "com.daiatech.karya.rawaudiorecorder"
    compileSdk = 33

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
}


group = "io.github.karya-inc"
version = "0.0.7"

mavenPublishing {
    val artifactId = "rawaudiorecorder"
    publishToMavenCentral(true)
    signAllPublications()

    coordinates(
        groupId = group.toString(),
        artifactId = artifactId,
        version = version.toString()
    )

    pom {
        name.set(artifactId)
        description.set("A lightweight Android library to record wave audio")
        url.set("https://github.com/karya-inc/RawAudioRecorder.git")

        licenses {
            license {
                name.set("GNU license")
                url.set("https://opensource.org/license/gpl-3-0")
            }
        }

        developers {
            developer {
                id.set("divyansh@karya.in")
                name.set("Divyansh Kushwaha")
                email.set("divyansh@karya.in")
            }
        }

        scm {
            connection.set("scm:git:ssh://git@github.com/karya-inc/RawAudioRecorder.git")
            developerConnection.set("scm:git:ssh://git@github.com/karya-inc/RawAudioRecorder.git")
            url.set("https://github.com/karya-inc/RawAudioRecorder.git")
        }
    }
}
