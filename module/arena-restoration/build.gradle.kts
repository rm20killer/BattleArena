repositories {
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    compileOnlyApi(project(":plugin"))
    compileOnly(libs.worldedit)
}