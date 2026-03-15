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
  implementation(libs.jedis)
  implementation(libs.spring.boot.starter.actuator)
  implementation(libs.spring.boot.starter.cache)
  implementation(libs.spring.boot.starter.data.jpa)
  implementation(libs.spring.boot.starter.data.redis)
  implementation(libs.spring.boot.starter.jdbc)
  implementation(libs.spring.boot.starter.oauth2.resource.server)
  implementation(libs.spring.boot.starter.security)
  implementation(libs.spring.boot.starter.validation)
  implementation(libs.spring.boot.starter.web)
  implementation(libs.springdoc.openapi.ui)
  implementation(platform(libs.spring.boot.dependencies))
  runtimeOnly(libs.flyway.database.postgresql)
  runtimeOnly(libs.postgresql)
  runtimeOnly(libs.spring.boot.devtools)
  testImplementation(libs.spring.boot.starter.test)
  testImplementation(libs.spring.security.test)
  testRuntimeOnly(libs.h2database.h2)
}

jacocoCoverage {
  classExcludes =
    listOf(
      "org/sagebionetworks/bixarena/api/model/dto/**",
      "org/sagebionetworks/bixarena/api/api/AdminApi.class",
      "org/sagebionetworks/bixarena/api/api/AdminApiController.class",
      "org/sagebionetworks/bixarena/api/api/AdminApiDelegate.class",
      "org/sagebionetworks/bixarena/api/api/QuestApi.class",
      "org/sagebionetworks/bixarena/api/api/QuestApiController.class",
      "org/sagebionetworks/bixarena/api/api/QuestApiDelegate.class",
      "org/sagebionetworks/bixarena/api/configuration/EnumConverterConfiguration*",
      "org/sagebionetworks/bixarena/api/configuration/Flyway*",
      "org/sagebionetworks/bixarena/api/configuration/SpringDocConfiguration*",
      "org/sagebionetworks/bixarena/api/RFC3339DateFormat*",
    )

  forceClassIncludes =
    listOf(
      "org/sagebionetworks/bixarena/api/api/*Impl.class",
    )
}

flyway {
  url = "jdbc:postgresql://bixarena-postgres:21000/bixarena"
  user = System.getenv("FLYWAY_USER") ?: "postgres"
  password = System.getenv("FLYWAY_PASSWORD") ?: "changeme"
  cleanDisabled = false
  schemas = arrayOf("api")
  defaultSchema = "api"
}
