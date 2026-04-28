plugins {
  id("sage.spring-boot-application")
  id("sage.lombok")
}

dependencies {
  implementation(libs.caffeine)
  implementation(libs.findbugs.jsr305)
  implementation(libs.jackson.databind)
  implementation(libs.jackson.dataformat.yaml)
  implementation(libs.jackson.datatype.jsr310)
  implementation(libs.spring.boot.starter.validation)
  implementation(libs.spring.boot.starter.web)
  implementation(libs.spring.boot.starter.data.mongodb)
  implementation(libs.spring.boot.starter.cache)
  implementation(libs.spring.boot.starter.actuator)
  implementation(libs.springdoc.openapi.ui)
  implementation(platform(libs.spring.boot.dependencies))
  runtimeOnly(libs.spring.boot.devtools)
  testImplementation(libs.spring.boot.starter.test)
}
