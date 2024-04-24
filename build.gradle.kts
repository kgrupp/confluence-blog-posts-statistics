import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
  repositories {
    mavenCentral()
    google()
  }
}

plugins {
  idea
  kotlin("jvm") version "1.9.23"
}

version = "0.0.1-SNAPSHOT"

tasks.withType<KotlinCompile> { kotlinOptions { jvmTarget = "11" } }

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.danilopianini:khttp:1.3.1")
  implementation("com.fasterxml.jackson.core:jackson-databind:2.17.0")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.0")
}
