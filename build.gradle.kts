import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    application
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "su.gachi"
version = "1.0.0-SNAPSHOT.1"

repositories {
    mavenCentral()
    maven {
        url = uri("https://jitpack.io")
    }
}

tasks.jar {
    manifest.attributes["Main-Class"] = "MainKt"
    manifest.attributes["Class-Path"] = configurations
        .runtimeClasspath
        .get()
        .joinToString(separator = " ") { file ->
            "libs/${file.name}"
        }
}

dependencies {
    implementation("net.dv8tion:JDA:5.0.0-beta.2")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.0")
    implementation("ch.qos.logback:logback-classic:1.4.5")
    implementation("org.apache.commons:commons-text:1.10.0")
    implementation("org.json:json:20220924")
    implementation("com.github.walkyst:lavaplayer-fork:1.3.99.1")
    implementation("org.mongodb:mongodb-driver-sync:4.7.1")
}


tasks {
    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        baseName = "bot"
        classifier = ""
        version = "1.0.0-SNAPSHOT.1"
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

application {
    mainClass.set("MainKt")
}