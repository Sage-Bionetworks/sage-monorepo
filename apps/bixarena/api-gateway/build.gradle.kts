plugins {
  id("sage.spring-boot-application")
  id("sage.lombok")
  id("sage.jacoco-coverage")
}

dependencies {
  implementation(libs.caffeine)
  implementation(libs.jackson.databind)
  implementation(libs.jackson.dataformat.yaml)
  implementation(libs.spring.boot.starter.actuator)
  implementation(libs.spring.boot.starter.data.redis)
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

// Task to generate route configurations from OpenAPI specifications
tasks.register<JavaExec>("generateRouteConfig") {
  group = "application"
  description = "Generate route configurations from OpenAPI specifications for BixArena API Gateway"
  classpath = sourceSets["main"].runtimeClasspath
  mainClass.set("org.sagebionetworks.bixarena.api.gateway.util.OpenApiRouteConfigGenerator")

  // Process all OpenAPI service files that have security requirements
  args =
    listOf(
      "../../../libs/bixarena/api-description/openapi/ai-service.openapi.yaml",
      "../../../libs/bixarena/api-description/openapi/api-service.openapi.yaml",
      "../../../libs/bixarena/api-description/openapi/auth-service.openapi.yaml",
    )

  doFirst {
    println("Generating route configurations for BixArena...")
  }
}

tasks.named("generateRouteConfig") {
  dependsOn("compileJava")
}

// tasks.test {
//   useJUnitPlatform()
//   testLogging {
//     showStandardStreams = true
//     events("passed", "skipped", "failed", "standardOut", "standardError")
//     // exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
//   }
// }
