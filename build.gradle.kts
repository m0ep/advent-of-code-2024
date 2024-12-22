plugins {
    kotlin("jvm") version "2.1.0"
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")

    testImplementation(kotlin("test"))
    testImplementation("org.assertj:assertj-core:3.27.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
}

tasks {
    wrapper {
        gradleVersion = "8.11.1"
    }

    test {
        useJUnitPlatform()
    }
}
