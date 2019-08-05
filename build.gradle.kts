buildscript {
    val kotlinVersion = "1.3.31"

    dependencies {
        classpath(kotlin("gradle-plugin", version = kotlinVersion))
    }
}

repositories {
    mavenCentral()
}

plugins {
    kotlin("multiplatform") version "1.3.31"
    `maven-publish`
}

group = "site.hackery"
version = "0.0.1"

kotlin {
    jvm {}

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }
    }
}


publishing {
    repositories {
        maven("$buildDir/repo")
    }
}
