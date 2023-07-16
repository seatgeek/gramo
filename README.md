# Gramo (Gradle Module Generator)
![tests](https://github.com/seatgeek/gramo/workflows/Test/badge.svg)
![lint](https://github.com/seatgeek/gramo/workflows/Lint/badge.svg)
[![maven central](https://maven-badges.herokuapp.com/maven-central/com.seatgeek.gramo/gradle-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.seatgeek.gramo/gradle-plugin)

An extensible template processor for use with gradle in large multi-module projects.

## Introduction
This project is the result of a SeatGeek hackathon. In its current state, the project should 
be considered pre-alpha. That being said, this doesn't need to run in production. Using this is
safe as long as you commit your code to a source control system before using it.

## How it works
TL;DR: You can build your own module archetype that employs a specialized markup
language to annotate files, directories, and code to produce modules matching your specification.

### Gramo Markdown
The markdown used to code archetypes is fairly limited at the moment, but suites our needs for
the moment. Unfortunately, it doesn't have an interpreter yet, so be sure to read carefully and 
pay attention to module generation error messages.

In essence, a markdown tag always has a single attribute which immediately follows `<gramo::` 
in the opening tag.

#### interpolate 
Searches the configuration variables keyed by whatever content gets resolved between the open/close tags as a key.

Currently available variables:
 - GROUP_ID - e.g. "com.example"
 - MODULE_NAME - e.g. "example"
 - MODULE_CLASS_NAME - e.g. "Example"
 - ROOT_PACKAGE - always matches the group_id
 - ROOT_PACKAGE_PATH - the group_id delimited by file separators
 - VERSION - can also be code e.g. "ext.version" 

Additional variables can be included as specified within the archetype's scheme.json file or preset

#### includeIf

```
TODO
```

## Usage

The following command will use the "feature" archetype to generate an example module in 
the root project directory. Realistically, consumers of this project should create their
own archetype, but feel free to use the example as a starting point.

```
./gradlew :example:generateSubmodule --name=Test --module_name=test --archetype=feature --preset=default --group_id=com.example.test
```

## Setup
Artifacts can be downloaded from Maven Central... soon

In the meantime, you can publish it to your local maven repository by cloning this project
and executing `./gradlew publishLocally` from the root of the project.

### Gradle Kotlin Script
```kotlin
plugins {
  id("com.seatgeek.gramo") version "0.1.1"
}

gramo {
  archetypesPath = "path_to_my_archetypes"
}
```

### Gradle Groovy
```groovy
plugins {
    id 'com.seatgeek.gramo' version '0.1.1'
}

gramo {
    archetypesPath = 'path_to_my_archetypes'
}
```

### Old School
```groovy
buildscript {
    // ...
    
    dependencies {
        classpath 'com.seatgeek.gramo:gradle-plugin:0.1.1'
    }
}

apply plugin: 'com.seatgeek.gramo'

gramo {
    archetypesPath = 'path_to_my_archetypes'
}
```
