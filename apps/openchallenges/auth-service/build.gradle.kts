buildscript {
  dependencies {
    classpath(libs.flyway.database.postgresql)
  }
}

plugins {
  alias(libs.plugins.flyway)
  alias(libs.plugins.spring.boot)
  java
}

group = "org.sagebionetworks.openchallenges"
version = "0.0.1-SNAPSHOT"

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(21)
  }
}

repositories {
	mavenCentral()
  mavenLocal()
}

dependencies {
  implementation(libs.findbugs.jsr305)
  implementation(libs.flyway.core)
  implementation(libs.flyway.database.postgresql)
  implementation(libs.jackson.databind)
  implementation(libs.jackson.dataformat.yaml)
  implementation(libs.jackson.datatype.jsr310)
  implementation(libs.postgresql)
  implementation(libs.spring.boot.starter.data.jpa)
  implementation(libs.spring.boot.starter.security)
  implementation(libs.spring.boot.starter.validation)
  implementation(libs.spring.boot.starter.web)
  implementation(libs.springdoc.openapi.ui)
  implementation(platform(libs.spring.boot.dependencies))
  runtimeOnly(libs.spring.boot.devtools)
  testImplementation(libs.spring.boot.starter.test)
  testImplementation(libs.spring.security.test)
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootBuildImage>("bootBuildImage") {
  imageName.set("ghcr.io/sage-bionetworks/${project.name}-base:local")
}

flyway {
  url = "jdbc:postgresql://openchallenges-postgres:8091/auth_service"
  user = System.getenv("FLYWAY_USER") ?: "auth_service"
  password = System.getenv("FLYWAY_PASSWORD") ?: "changeme"
  cleanDisabled = false
}
