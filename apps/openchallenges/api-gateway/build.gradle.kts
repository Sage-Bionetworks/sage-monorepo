plugins {
  id("sage.spring-boot-application")
  id("sage.jacoco-coverage")
}

dependencies {
  implementation(libs.spring.boot.starter.actuator)
  implementation(libs.spring.boot.starter.security)
  implementation(libs.spring.cloud.starter.gateway.server.webflux)
  implementation(libs.spring.boot.starter.webflux) // For WebClient
  implementation(project(":openchallenges-app-config-data"))

  // OAuth2 and JWT support
  implementation(libs.spring.boot.starter.oauth2.resource.server)
  implementation(libs.spring.security.oauth2.jose)
  implementation(libs.jjwt.api)
  runtimeOnly(libs.jjwt.impl)
  runtimeOnly(libs.jjwt.jackson)

  // Lombok support
  compileOnly(libs.lombok)
  annotationProcessor(libs.lombok)

  // Note: Using WebClient for internal auth service communication instead of API client
  runtimeOnly(libs.spring.boot.devtools)
  testImplementation(libs.spring.boot.starter.test)
}

jacocoCoverage {
  classExcludes = listOf<String>()
  forceClassIncludes = listOf<String>()
}
