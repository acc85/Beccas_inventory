plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    jvmToolchain(17)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    // Pure Kotlin models, no dependencies needed yet for entities
    implementation(libs.kotlinx.serialization.cbor)
    // Compose runtime for @Immutable annotation (pure JVM, no Android needed)
    implementation("androidx.compose.runtime:runtime:1.6.1")
    implementation(libs.kotlinx.collections.immutable)
}
