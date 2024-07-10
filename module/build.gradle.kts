subprojects {
    dependencies {
        compileOnlyApi(project(":plugin"))
    }

    tasks.jar {
        from("src/main/java/resources") {
            include("*")
        }

        archiveFileName.set("${project.name}.jar")
        archiveClassifier.set("")
    }
}