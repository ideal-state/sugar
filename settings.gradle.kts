rootProject.name = "sugar"

pluginManagement {
    repositories {
        mavenLocal()
        maven {
            name = "sonatype-snapshots"
            url = uri("https://central.sonatype.com/repository/maven-snapshots/")
        }
        gradlePluginPortal()
    }
}

plugins {
    id("team.idealstate.glass") version "0.2.0-SNAPSHOT"
}
