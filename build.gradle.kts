java.sourceCompatibility = JavaVersion.VERSION_17

object Versions {
    const val JACKSON_KOTLIN = "2.15.0"
}

plugins {
    kotlin("jvm") version "1.9.10"
    id("org.ajoberstar.grgit") version "5.2.0"
    id("com.github.ben-manes.versions") version "0.50.0"
}

group = "eu.antoniolopez.xmlparser"
version = grgit.head().abbreviatedId.take(6)
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation(group = "com.liferay", name = "com.fasterxml.jackson.databind", version = "2.10.5.1.LIFERAY-PATCHED-1")
    implementation(group = "com.fasterxml.jackson.dataformat", name = "jackson-dataformat-xml", version= Versions.JACKSON_KOTLIN)
    implementation(group = "com.fasterxml.jackson.module", name = "jackson-module-kotlin", version= Versions.JACKSON_KOTLIN)
    implementation(group = "com.fasterxml.jackson.core", name = "jackson-core", version= Versions.JACKSON_KOTLIN)
    implementation(group = "com.fasterxml.jackson.core", name = "jackson-annotations", version= Versions.JACKSON_KOTLIN)
    implementation(group = "com.fasterxml.jackson.core", name = "jackson-databind", version= Versions.JACKSON_KOTLIN)
}
