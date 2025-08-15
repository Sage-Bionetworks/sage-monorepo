import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
  id("sage.java-library")
  id("org.springframework.boot")
}

// Spring Boot library projects don't need an executable jar, so we disable it
tasks.named<BootJar>("bootJar") {
  enabled = false
}

// Spring Boot library projects don't need a bootRun task, so we disable it
tasks.named<BootRun>("bootRun") {
  enabled = false
}

tasks.named<Jar>("jar") {
  enabled = true
  archiveClassifier.set("") // use empty string
}
