plugins {
    application
    id("com.seatgeek.gramo")
}

gramo {
    archetypesPath = ".gramo"
    versionString = "\${version}"
}

application {
    @Suppress("UnstableApiUsage")
    mainClass.set("com.seatgeek.gramo.example.Main")
}
