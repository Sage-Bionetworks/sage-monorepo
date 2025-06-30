buildscript {
  dependencies {
    classpath(libs.flyway.database.postgresql)
  }
}

plugins {
  alias(libs.plugins.flyway)
  alias(libs.plugins.spring.boot)
  jacoco
  java
}

group = "org.sagebionetworks.amp.als"
version = "0.0.1-SNAPSHOT"

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(21)
  }
}

repositories {
  // maven { url = uri("https://repo.spring.io/milestone") }
  mavenCentral()
  mavenLocal()
}

dependencies {
  annotationProcessor(libs.lombok)
  compileOnly(libs.lombok)
  implementation(libs.amp.als.app.config.data)
  implementation(libs.findbugs.jsr305)
  implementation(libs.flyway.core)
  implementation(libs.flyway.database.postgresql)
  implementation(libs.hibernate.search.backend.elasticsearch)
  implementation(libs.hibernate.search.mapper.orm)
  implementation(libs.jackson.databind)
  implementation(libs.jackson.dataformat.yaml)
  implementation(libs.jackson.datatype.jsr310)
  implementation(libs.sagebionetworks.util)
  implementation(libs.spring.boot.starter.data.jpa)
  implementation(libs.spring.boot.starter.jdbc)
  implementation(libs.spring.boot.starter.security)
  implementation(libs.spring.boot.starter.validation)
  implementation(libs.spring.boot.starter.web)
  implementation(libs.springdoc.openapi.ui)
  implementation(platform(libs.spring.boot.dependencies))
  runtimeOnly(libs.postgresql)
  runtimeOnly(libs.spring.boot.devtools)
  testAnnotationProcessor(libs.lombok)
  testCompileOnly(libs.lombok)
  testImplementation(libs.spring.boot.starter.test)
}

tasks.withType<JavaCompile> {
  options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
  options.encoding = "UTF-8"
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootBuildImage>("bootBuildImage") {
  imageName.set("ghcr.io/sage-bionetworks/${project.name}-base:local")
}

flyway {
  url = "jdbc:postgresql://amp-als-postgres:8401/dataset_service"
  user = System.getenv("FLYWAY_USER") ?: "dataset_service"
  password = System.getenv("FLYWAY_PASSWORD") ?: "changeme"
  cleanDisabled = false
}