import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("sage.java-library")
    id("org.springframework.boot")
}

// Spring Boot library projects don't need an executable jar, so we disable it
tasks.named<BootJar>("bootJar") {
    enabled = false
}

tasks.named<Jar>("jar") {
    enabled = true
    archiveClassifier.set("") // use empty string
}
