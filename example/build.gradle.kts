plugins {
    application
    id("com.seatgeek.gramo")
}

gramo {
    archetypesPath = ".gramo"
    versionString = "\${version}"
}

application {
    mainClass.set("com.seatgeek.gramo.example.Main")
}
