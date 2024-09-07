plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    kotlin("jvm")
}

group = "me.meiallu.lutags"
version = "1.0"

repositories {
    mavenCentral()
    mavenLocal()

    maven(url = "https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    compileOnly("org.spigotmc:spigot:1.8.8-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.6")

    implementation("com.esotericsoftware.yamlbeans:yamlbeans:1.17")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.0.0")

    implementation("mysql:mysql-connector-java:8.0.33")
    implementation("org.xerial:sqlite-jdbc:3.41.2.1")
    implementation("redis.clients:jedis:5.1.0")
    implementation("org.mongodb:mongodb-driver-sync:4.11.1")
}

kotlin {
    jvmToolchain(8)
}