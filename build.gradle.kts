import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.0"
    application
}

group = "com.github.errandir.revolut.test"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile(group="io.javalin", name = "javalin", version = "2.4.0")
    compile(group = "org.apache.logging.log4j", name = "log4j-core", version = "2.11.1")
    compile(group = "org.apache.logging.log4j", name = "log4j-slf4j-impl", version = "2.11.1")
    compile(group = "com.fasterxml.jackson.dataformat", name = "jackson-dataformat-yaml", version = "2.5.0")
    compile(group = "com.fasterxml.jackson.module", name = "jackson-module-kotlin", version = "2.9.4.1")
    testCompile(kotlin("test-junit"))
}

configure<ApplicationPluginConvention> {
    mainClassName = "com.github.errandir.revolute.test.moneytran.app.AppKt"
}

