import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.0"
}

group = "com.github.errandir.revolut.test"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile(group="io.javalin", name = "javalin", version = "2.4.0")
    testCompile(kotlin("test-junit"))
}