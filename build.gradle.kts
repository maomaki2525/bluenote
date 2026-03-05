plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.serialization)
}

group = "com.example"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.config.yaml)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
implementation("io.ktor:ktor-server-content-negotiation-jvm:2.3.12")
implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:2.3.12")
implementation("io.ktor:ktor-server-content-negotiation-jvm:2.3.12")
implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:2.3.12")
implementation("org.xerial:sqlite-jdbc:3.45.1.0")
}
