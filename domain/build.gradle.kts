plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(17)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(project(":models"))
    implementation(project(":contracts"))
    implementation(libs.kotlinx.coroutines.core)
    implementation("javax.inject:javax.inject:1")
}
