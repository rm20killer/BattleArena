repositories {
    maven("https://jitpack.io")
}

dependencies {
    compileOnlyApi(project(":plugin"))
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
}