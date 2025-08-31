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
  // Note: Using WebClient for internal auth service communication instead of API client
  runtimeOnly(libs.spring.boot.devtools)
  testImplementation(libs.spring.boot.starter.test)
}

jacocoCoverage {
  classExcludes = listOf<String>()
  forceClassIncludes = listOf<String>()
}
