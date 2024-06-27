plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "org.battleplugins"
version = "4.0.0-SNAPSHOT"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

allprojects {
    repositories {
        maven("https://repo.papermc.io/repository/maven-public")
    }

    apply {
        plugin("java")
        plugin("java-library")
        plugin("com.github.johnrengelman.shadow")
    }
}