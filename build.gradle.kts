plugins {
    id("java")
    id("com.gradleup.shadow") version("8.3.5")
}

group = "team.idealstate.sugar"
version = "0.1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
        vendor.set(JvmVendorSpec.AZUL)
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("org.ow2.asm:asm:9.7.1")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly(files(File(javaToolchains.compilerFor {
        languageVersion.set(java.toolchain.languageVersion)
        vendor.set(java.toolchain.vendor)
    }.get().executablePath.asFile.parentFile.parentFile, "lib/tools.jar")))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

val manifestAttributes = mapOf(
    "Premain-Class" to "team.idealstate.sugar.common.agent.AgentUtils",
    "Agent-Class" to "team.idealstate.sugar.common.agent.AgentUtils",
    "Can-Redefine-Classes" to true,
    "Can-Retransform-Classes" to true,
    "Can-Set-Native-Method-Prefix" to true,
)

tasks.jar {
    manifest {
        attributes(manifestAttributes)
    }
}

tasks.shadowJar {
    archiveClassifier.set("")
    manifest {
        attributes(manifestAttributes)
    }
    val pattern = "org.objectweb.asm"
    relocate(pattern, "${project.group}.${pattern}")
}
