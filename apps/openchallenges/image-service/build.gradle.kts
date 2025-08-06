plugins {
  id("sage.spring-boot-application")
  id("sage.lombok")
  id("sage.jacoco-coverage")
}

dependencies {
  implementation(libs.findbugs.jsr305)
  implementation(libs.jackson.databind)
  implementation(libs.jackson.dataformat.yaml)
  implementation(libs.jackson.datatype.jsr310)
  implementation(project(":sagebionetworks-util"))
  implementation(libs.spring.boot.starter.actuator)
  implementation(libs.spring.boot.starter.security)
  implementation(libs.spring.boot.starter.validation)
  implementation(libs.spring.boot.starter.web)
  implementation(libs.springdoc.openapi.ui)
  implementation(libs.squareup.pollexor)
  implementation(project(":openchallenges-app-config-data"))
  runtimeOnly(libs.spring.boot.devtools)
  testImplementation(libs.spring.boot.starter.test)
  testImplementation(libs.spring.security.test)
  testImplementation(libs.testcontainers.junit.jupiter)
  testImplementation(platform(libs.testcontainers.bom))
}

// Configure Jacoco coverage exclusions for OpenAPI generated files
jacocoCoverage {
  classExcludes = listOf(
    "org/sagebionetworks/openchallenges/image/service/model/dto/**",
    "org/sagebionetworks/openchallenges/image/service/api/**"
  )

  forceClassIncludes = listOf(
    "org/sagebionetworks/openchallenges/image/service/configuration/EnumConverterConfiguration*",
    "org/sagebionetworks/openchallenges/image/service/configuration/HomeController*",
    "org/sagebionetworks/openchallenges/image/service/configuration/SpringDocConfiguration*",
    "org/sagebionetworks/openchallenges/image/service/RFC3339DateFormat*",
    "org/sagebionetworks/openchallenges/image/service/api/*Impl.class"
  )
}
