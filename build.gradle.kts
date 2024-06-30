plugins {
    id("java")
}

allprojects {
    apply {
        plugin("java")
        plugin("java-library")
    }

    group = "org.battleplugins"
    version = "4.0.0-SNAPSHOT"

    repositories {
        maven("https://repo.papermc.io/repository/maven-public")
    }

    java {
        withJavadocJar()
        withSourcesJar()
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
}