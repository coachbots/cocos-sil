import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    application
}

group = "com.markovejnovic"
version = "1.0-SNAPSHOT"

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