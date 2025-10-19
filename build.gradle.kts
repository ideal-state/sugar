import org.jreleaser.model.Active
import team.idealstate.glass.plugin.project.java.extension.JavaExtension
import java.util.Locale

plugins {
    glass
    alias(libs.plugins.jreleaser)
}

group = "team.idealstate.sugar"
version = "0.2.0-SNAPSHOT"

glass {
    apply<JavaExtension> {
        val moduleId = group.toString()
        module(moduleId)

        release(8) {
            multi(9) {
                toolchain(11)
            }
            multi(11)
            multi(17)
            multi(21)
        }

        artifacts {
            manifest {
                val mainClass = "$moduleId.agent.Javaagent"
                premain.set(mainClass)
                agentmain.set(mainClass)
                canRedefineClasses.set(true)
                canRetransformClasses.set(true)
                canSetNativeMethodPrefix.set(true)
            }

            shadowJar {
                val source = "org.objectweb.asm"
                internal(source)
            }

            sourcesJar()
            javadocJar()
        }

        integration {
            junit {
                mockito()
            }
        }

        publication {
            pom {
                description.set("Coffee(Java) with sugar is sweeter.")
                val uri = "https://github.com/ideal-state/sugar"
                url.set(uri)
                licenses {
                    license {
                        name.set("Apache License 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                scm {
                    url.set(uri)
                    connection.set("scm:git:$uri.git")
                    developerConnection.set("scm:git:$uri.git")
                }
                developers {
                    developer {
                        id.set("ideal-state")
                        name.set("ideal-state")
                        email.set("support@idealstate.team")
                    }
                }
            }
        }
    }
}

dependencies {
    api(libs.log4j.api)

    shadow(libs.asm)
}

publishing {
    repositories {
        local(project)
    }
}

jreleaser {
    deploy {
        maven {
            mavenCentral {
                register("release") {
                    active.set(Active.RELEASE)
                    url.set("https://central.sonatype.com/api/v1/publisher")
                    sign.set(false)
                    stagingRepository("build/repository")
                }
            }
            nexus2 {
                register("snapshot") {
                    active.set(Active.SNAPSHOT)
                    url.set("https://central.sonatype.com/repository/maven-snapshots")
                    snapshotUrl.set("https://central.sonatype.com/repository/maven-snapshots")
                    sign.set(false)
                    applyMavenCentralRules.set(true)
                    snapshotSupported.set(true)
                    closeRepository.set(true)
                    releaseRepository.set(true)
                    verifyPom.set(false)
                    stagingRepository("build/repository")
                }
            }
        }
    }
}

tasks.register("doDeploy") {
    dependsOn(tasks.named("test"))
    dependsOn(tasks.named("publishAllPublicationsTo${project.name.replaceFirstChar { it.titlecase(Locale.ENGLISH) }}Repository"))
    finalizedBy(tasks.named("jreleaserDeploy"))
}

tasks.register("deploy") {
    group = "glass"
    dependsOn(tasks.named("clean"))
    dependsOn(tasks.named("spotlessApply"))
    finalizedBy(tasks.named("doDeploy"))
}
