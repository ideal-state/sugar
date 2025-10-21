rootProject.name = "sugar"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven {
            name = "sonatype-snapshots"
            url = uri("https://central.sonatype.com/repository/maven-snapshots/")
        }
        mavenLocal()
    }
}

plugins {
    id("team.idealstate.glass") version "0.2.0-SNAPSHOT"
}
