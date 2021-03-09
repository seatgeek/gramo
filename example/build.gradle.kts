plugins {
    application
    id("com.seatgeek.gramo")
}

gramo {
    archetypesPath = ".gramo"
}

application {
    @Suppress("UnstableApiUsage")
    mainClass.set("com.seatgeek.gramo.example.Main")
}
