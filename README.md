# Gramo (Gradle Module Generator)
![tests](https://github.com/seatgeek/gramo/workflows/Test/badge.svg)
![lint](https://github.com/seatgeek/gramo/workflows/Lint/badge.svg)
[![maven central](https://maven-badges.herokuapp.com/maven-central/com.seatgeek.gramo/gradle-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.seatgeek.gramo/gradle-plugin)

An extensible template processor for use with gradle in large multi-module projects.

## Introduction
TBD

## How it works
TBD

## Setup
Artifacts can be downloaded from Maven Central... soon?

### Gradle Kotlin Script
```kotlin
plugins {
  id("com.seatgeek.gramo") version "0.0.1"
}

gramo {
  architetypesPath = "path_to_my_archetypes"
}
```

### Gradle Groovy
```groovy
plugins {
    id 'com.seatgeek.gramo' version '0.0.1'
}

gramo {
    archetypesPath = 'path_to_my_archetypes'
}
```
