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

// Task to generate route-to-scope mappings from OpenAPI specifications
tasks.register<JavaExec>("generateRouteScopes") {
  group = "application"
  description = "Generate route-to-scope mappings from OpenAPI specifications for API Gateway"
  classpath = sourceSets["main"].runtimeClasspath
  mainClass.set("org.sagebionetworks.openchallenges.api.gateway.util.OpenApiScopeMapper")

  // Process all OpenAPI service files that have security requirements
  args =
    listOf(
      "../../../libs/openchallenges/api-description/openapi/auth-service.openapi.yaml",
      "../../../libs/openchallenges/api-description/openapi/challenge-service.openapi.yaml",
      "../../../libs/openchallenges/api-description/openapi/organization-service.openapi.yaml",
    )

  doFirst {
    println("Generating route-to-scope mappings...")
    println("Output will be written to src/main/resources/route-scopes.yml")
  }
}

tasks.named("generateRouteScopes") {
  dependsOn("compileJava")
}
