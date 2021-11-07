import org.jetbrains.compose.compose
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose") version "1.0.0-beta3"
    kotlin("plugin.serialization")
    `maven-publish`
}

group = "de.nebdir"
version = "1.0.0"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(compose.desktop.currentOs)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}

publishing {
    repositories {
        maven("https://maven.pkg.github.com/Hero9909/roughtDrop") {
            name = "GitHubPackages"
            credentials {
                username = System.getenv("USERNAME")
                password = System.getenv("TOKEN")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "de.nebdir.roughtDrop"
            artifactId = "core"
            version = rootProject.version.toString()
            from(components["java"])
        }
    }

}
