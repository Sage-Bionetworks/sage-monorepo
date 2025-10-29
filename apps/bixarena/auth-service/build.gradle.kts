buildscript {
  dependencies {
    classpath(libs.flyway.database.postgresql)
  }
}

plugins {
  id("sage.spring-boot-application")
  id("sage.lombok")
  alias(libs.plugins.flyway)
}

dependencies {
  implementation(libs.flyway.core)
  implementation(libs.jackson.databind)
  implementation(libs.jackson.dataformat.yaml)
  implementation(libs.jackson.datatype.jsr310)
  implementation(libs.jedis)
  implementation(libs.nimbus.jose.jwt)
  implementation(libs.spring.boot.starter.actuator)
  implementation(libs.spring.boot.starter.data.jpa)
  implementation(libs.spring.boot.starter.data.redis)
  implementation(libs.spring.boot.starter.jdbc)
  implementation(libs.spring.boot.starter.oauth2.client)
  implementation(libs.spring.boot.starter.security)
  implementation(libs.spring.boot.starter.validation)
  implementation(libs.spring.boot.starter.web)
  implementation(libs.springdoc.openapi.ui)
  implementation(platform(libs.spring.boot.dependencies))
  runtimeOnly(libs.flyway.database.postgresql)
  runtimeOnly(libs.postgresql)
  runtimeOnly(libs.spring.boot.devtools)
  testImplementation(libs.spring.boot.starter.test)
  testRuntimeOnly(libs.h2database.h2)
}

flyway {
  url = "jdbc:postgresql://bixarena-postgres:21000/bixarena"
  user = System.getenv("FLYWAY_USER") ?: "postgres"
  password = System.getenv("FLYWAY_PASSWORD") ?: "changeme"
  cleanDisabled = false
  schemas = arrayOf("auth")
  defaultSchema = "auth"
}
