plugins {
    id("xyz.jpenilla.run-paper") version "2.0.1"
}

repositories {
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    compileOnlyApi(libs.paper.api)
    compileOnly(libs.worldedit)
}

tasks {
    runServer {
        minecraftVersion("1.20.4")
    }

    shadowJar {
        from("src/main/java/resources") {
            include("*")
        }

        archiveFileName.set("BattleArena.jar")
        archiveClassifier.set("")
    }

    jar {
        archiveClassifier.set("unshaded")
    }

    named("build") {
        dependsOn(shadowJar)
    }
}