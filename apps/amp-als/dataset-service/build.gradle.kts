buildscript {
  dependencies {
    classpath(libs.flyway.database.postgresql)
  }
}

plugins {
  id("sage.spring-boot-application")
  alias(libs.plugins.flyway)
}

dependencies {
  annotationProcessor(libs.lombok)
  compileOnly(libs.lombok)
  implementation(libs.findbugs.jsr305)
  implementation(libs.flyway.core)
  implementation(libs.hibernate.search.backend.elasticsearch)
  implementation(libs.hibernate.search.mapper.orm)
  implementation(libs.jackson.databind)
  implementation(libs.jackson.dataformat.yaml)
  implementation(libs.jackson.datatype.jsr310)
  implementation(libs.spring.boot.starter.actuator)
  implementation(libs.spring.boot.starter.data.jpa)
  implementation(libs.spring.boot.starter.jdbc)
  implementation(libs.spring.boot.starter.security)
  implementation(libs.spring.boot.starter.validation)
  implementation(libs.spring.boot.starter.web)
  implementation(libs.springdoc.openapi.ui)
  implementation(platform(libs.spring.boot.dependencies))
  implementation(project(":amp-als-app-config-data"))
  implementation(project(":sagebionetworks-util"))
  runtimeOnly(libs.flyway.database.postgresql)
  runtimeOnly(libs.postgresql)
  runtimeOnly(libs.spring.boot.devtools)
  testAnnotationProcessor(libs.lombok)
  testCompileOnly(libs.lombok)
  testImplementation(libs.spring.boot.starter.test)
  testRuntimeOnly(libs.h2database.h2)
}

jacocoCoverage {
  classExcludes = listOf<String>()
  forceClassIncludes = listOf<String>()
}

flyway {
  url = "jdbc:postgresql://amp-als-postgres:8401/dataset_service"
  user = System.getenv("FLYWAY_USER") ?: "dataset_service"
  password = System.getenv("FLYWAY_PASSWORD") ?: "changeme"
  cleanDisabled = false
}