plugins {
  id("sage.spring-boot-application")
  id("sage.jacoco-coverage")
}

dependencies {
  implementation(libs.spring.boot.starter.actuator)
  implementation(libs.spring.boot.starter.security)
  implementation(libs.spring.cloud.starter.gateway.server.webflux)
  implementation(project(":openchallenges-app-config-data"))
  runtimeOnly(libs.spring.boot.devtools)
  testImplementation(libs.spring.boot.starter.test)
}

jacocoCoverage {
  classExcludes = listOf<String>()
  forceClassIncludes = listOf<String>()
}