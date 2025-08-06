buildscript {
  dependencies {
    classpath(libs.flyway.database.postgresql)
  }
}

plugins {
  id("sage.spring-boot-application")
  id("sage.jacoco-coverage")
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
  testAnnotationProcessor(libs.lombok)
  testCompileOnly(libs.lombok)
  testImplementation(libs.spring.boot.starter.test)
  testImplementation(libs.spring.security.test)
  testImplementation(libs.testcontainers.junit.jupiter)
  testImplementation(libs.testcontainers.postgresql)
  testImplementation(platform(libs.testcontainers.bom))
  testRuntimeOnly(libs.h2database.h2)

  // Add dependency on the API client module
  implementation(project(":openchallenges-api-client-java"))
}

// Configure project-specific Jacoco coverage exclusions
jacocoCoverage {
  // Project-specific exclusions (beyond default OpenAPI exclusions)
  exclusions = listOf(
    "org/sagebionetworks/openchallenges/organization/service/model/dto/**",
    "org/sagebionetworks/openchallenges/organization/service/api/**"
  )

  // Configuration and generated class exclusions
  configurationExclusions = listOf(
    "org/sagebionetworks/openchallenges/organization/service/configuration/EnumConverterConfiguration*",
    "org/sagebionetworks/openchallenges/organization/service/configuration/Flyway*",
    "org/sagebionetworks/openchallenges/organization/service/configuration/HibernateSearch*",
    "org/sagebionetworks/openchallenges/organization/service/configuration/HomeController*",
    "org/sagebionetworks/openchallenges/organization/service/configuration/SpringDocConfiguration*",
    "org/sagebionetworks/openchallenges/organization/service/RFC3339DateFormat*"
  )

  // Include specific implementation classes that should be covered
  additionalIncludes = listOf(
    "org/sagebionetworks/openchallenges/organization/service/api/*Impl.class"
  )
}

flyway {
  url = "jdbc:postgresql://openchallenges-postgres:8091/organization_service"
  user = System.getenv("FLYWAY_USER") ?: "organization_service"
  password = System.getenv("FLYWAY_PASSWORD") ?: "changeme"
  cleanDisabled = false
}
