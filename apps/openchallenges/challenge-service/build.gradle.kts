buildscript {
  dependencies {
    classpath(libs.flyway.database.postgresql)
  }
}

plugins {
  id("sage.spring-boot-application")
  id("sage.lombok")
  id("sage.jacoco-coverage")
  alias(libs.plugins.flyway)
}

dependencies {
  implementation(libs.findbugs.jsr305)
  implementation(libs.flyway.core)
  implementation(libs.hibernate.search.backend.elasticsearch)
  implementation(libs.hibernate.search.mapper.orm)
  implementation(libs.jackson.databind)
  implementation(libs.jackson.dataformat.yaml)
  implementation(libs.jackson.datatype.jsr310)
  implementation(project(":openchallenges-app-config-data"))
  implementation(libs.sagebionetworks.util)
  implementation(libs.spring.boot.starter.actuator)
  implementation(libs.spring.boot.starter.data.jpa)
  implementation(libs.spring.boot.starter.jdbc)
  implementation(libs.spring.boot.starter.security)
  implementation(libs.spring.boot.starter.validation)
  implementation(libs.spring.boot.starter.web)
  implementation(libs.spring.cloud.starter.openfeign)
  implementation(libs.spring.data.commons)
  implementation(libs.springdoc.openapi.ui)
  implementation(platform(libs.spring.boot.dependencies))
  runtimeOnly(libs.flyway.database.postgresql)
  runtimeOnly(libs.postgresql)
  runtimeOnly(libs.spring.boot.devtools)
  testImplementation(libs.spring.boot.starter.test)
  testImplementation(libs.spring.security.test)
  testImplementation(libs.testcontainers.junit.jupiter)
  testImplementation(libs.testcontainers.postgresql)
  testImplementation(platform(libs.testcontainers.bom))
  testRuntimeOnly(libs.h2database.h2)
}

// Configure project-specific Jacoco coverage exclusions
jacocoCoverage {
  classExcludes = listOf(
    // Project-specific folder exclusions
    "org/sagebionetworks/openchallenges/challenge/service/model/dto/**",
    "org/sagebionetworks/openchallenges/challenge/service/api/**",
    // Configuration and generated class exclusions
    "org/sagebionetworks/openchallenges/challenge/service/configuration/EnumConverterConfiguration*",
    "org/sagebionetworks/openchallenges/challenge/service/configuration/Flyway*",
    "org/sagebionetworks/openchallenges/challenge/service/configuration/HibernateSearch*",
    "org/sagebionetworks/openchallenges/challenge/service/configuration/HomeController*",
    "org/sagebionetworks/openchallenges/challenge/service/configuration/SpringDocConfiguration*",
    "org/sagebionetworks/openchallenges/challenge/service/RFC3339DateFormat*"
  )

  // Include specific implementation classes that should be covered
  forceClassIncludes = listOf(
    "org/sagebionetworks/openchallenges/challenge/service/api/*Impl.class"
  )
}

flyway {
  url = "jdbc:postgresql://openchallenges-postgres:8091/challenge_service"
  user = System.getenv("FLYWAY_USER") ?: "challenge_service"
  password = System.getenv("FLYWAY_PASSWORD") ?: "changeme"
  cleanDisabled = false
}