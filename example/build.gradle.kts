plugins {
    id("com.seatgeek.gramo")
    id("com.seatgeek.gramo.defaults")

    application
}

gramo {
    archetypesPath = ".gramo"
    versionString = "\${version}"
}

application {
    mainClass.set("com.seatgeek.gramo.example.Main")
}
