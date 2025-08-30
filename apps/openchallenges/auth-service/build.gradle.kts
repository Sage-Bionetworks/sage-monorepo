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
  implementation(libs.jackson.databind)
  implementation(libs.jackson.dataformat.yaml)
  implementation(libs.jackson.datatype.jsr310)
  implementation(libs.spring.boot.starter.actuator)
  implementation(libs.spring.boot.starter.data.jpa)
  implementation(libs.spring.boot.starter.security)
  implementation(libs.spring.boot.starter.validation)
  implementation(libs.spring.boot.starter.web)
  implementation(libs.springdoc.openapi.ui)

  // OAuth2 and JWT dependencies
  implementation(libs.spring.boot.starter.oauth2.client)
  implementation(libs.spring.boot.starter.oauth2.resource.server)
  implementation(libs.spring.security.oauth2.jose)
  implementation(libs.spring.boot.starter.webflux)
  implementation(libs.jjwt.api)
  implementation(libs.jjwt.impl)
  implementation(libs.jjwt.jackson)

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

jacocoCoverage {
  classExcludes =
    listOf(
      "org/sagebionetworks/openchallenges/auth/service/model/dto/**",
      "org/sagebionetworks/openchallenges/auth/service/api/**",
      "org/sagebionetworks/openchallenges/auth/service/configuration/EnumConverterConfiguration*",
      "org/sagebionetworks/openchallenges/auth/service/configuration/Flyway*",
      "org/sagebionetworks/openchallenges/auth/service/configuration/HibernateSearch*",
      "org/sagebionetworks/openchallenges/auth/service/configuration/HomeController*",
      "org/sagebionetworks/openchallenges/auth/service/configuration/SpringDocConfiguration*",
      "org/sagebionetworks/openchallenges/auth/service/RFC3339DateFormat*",
    )

  forceClassIncludes =
    listOf(
      "org/sagebionetworks/openchallenges/auth/service/api/*Impl.class",
    )
}

flyway {
  url = "jdbc:postgresql://openchallenges-postgres:8091/auth_service"
  user = System.getenv("FLYWAY_USER") ?: "auth_service"
  password = System.getenv("FLYWAY_PASSWORD") ?: "changeme"
  cleanDisabled = false
}

// Task to generate password hashes
tasks.register<JavaExec>("generatePasswordHashes") {
  group = "application"
  description = "Generate BCrypt password hashes"
  classpath = sourceSets["main"].runtimeClasspath
  mainClass.set("org.sagebionetworks.openchallenges.auth.service.util.PasswordHashGenerator")
}

tasks.named("generatePasswordHashes") {
  dependsOn("compileJava")
}
