plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-gradle-plugin:3.5.4")
    implementation("org.graalvm.buildtools.native:org.graalvm.buildtools.native.gradle.plugin:0.11.0")
}
