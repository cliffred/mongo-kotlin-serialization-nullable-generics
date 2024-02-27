plugins {
    kotlin("jvm") version "2.0.255-SNAPSHOT"
    kotlin("plugin.serialization") version "2.0.255-SNAPSHOT"
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
//    val mongoVersion = "5.0.0-beta0"
    val mongoVersion = "5.0.0-SNAPSHOT"
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.3")
//    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
//    implementation("org.jetbrains.kotlinx:kotlinx-serialization-hocon:1.6.3")
//    implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.6.3")
//    implementation("org.jetbrains.kotlinx:kotlinx-serialization-cbor:1.6.3")
//    implementation("org.jetbrains.kotlinx:kotlinx-serialization-properties:1.6.3")
    implementation("org.mongodb:mongodb-driver-kotlin-coroutine:$mongoVersion")
    implementation("org.mongodb:bson-kotlinx:$mongoVersion")
//    implementation("org.mongodb:bson-kotlin:$mongoVersion")

    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("ch.qos.logback:logback-classic:1.4.14")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}
