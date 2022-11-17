import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import kotlin.collections.mapOf

tasks {
    val fatJar = register<Jar>("fatJar") {
        dependsOn.addAll(listOf("compileJava", "compileKotlin", "processResources"))
        archiveClassifier.set("standalone")
        duplicatesStrategy = org.gradle.api.file.DuplicatesStrategy.EXCLUDE
        manifest {
            attributes(mapOf("Main-Class" to application.mainClass))
        }
        val sourcesMain = sourceSets.main.get()
        val contents = configurations.runtimeClasspath.get().map {
            if (it.isDirectory) it else zipTree(it)
        }
        from(contents)
    }
    build {
        dependsOn(fatJar)
    }
}

plugins {
    kotlin("jvm") version "1.7.10"
    application
}

group = "com.markovejnovic"
version = "0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.slf4j:slf4j-simple:2.0.3")
    implementation("dev.romainguy:kotlin-math:1.5.2")
    implementation("com.github.ajalt.clikt:clikt:3.5.0")
    implementation("org.reflections:reflections:0.10.2")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}