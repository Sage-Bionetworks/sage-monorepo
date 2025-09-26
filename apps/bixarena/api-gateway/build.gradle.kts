plugins {
  id("sage.spring-boot-application")
  id("sage.lombok")
  id("sage.jacoco-coverage")
}

dependencies {
  implementation(libs.jackson.databind)
  implementation(libs.jackson.dataformat.yaml)
  implementation(libs.spring.boot.starter.actuator)
  implementation(libs.spring.boot.starter.oauth2.resource.server)
  implementation(libs.spring.boot.starter.security)
  implementation(libs.spring.boot.starter.webflux)
  implementation(libs.spring.cloud.starter.gateway.server.webflux)
  implementation(libs.spring.security.oauth2.jose)

  runtimeOnly(libs.spring.boot.devtools)
  testImplementation(libs.spring.boot.starter.test)
}

jacocoCoverage {
  classExcludes = listOf<String>()
  forceClassIncludes = listOf<String>()
}
