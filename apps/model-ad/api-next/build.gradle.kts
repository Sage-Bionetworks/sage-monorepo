plugins {
  id("sage.spring-boot-application")
}

dependencies {
  implementation(libs.findbugs.jsr305)
  implementation(libs.jackson.databind)
  implementation(libs.jackson.dataformat.yaml)
  implementation(libs.jackson.datatype.jsr310)
  implementation(libs.spring.boot.starter.validation)
  implementation(libs.spring.boot.starter.web)
  implementation(libs.spring.boot.starter.data.mongodb)
  implementation(libs.spring.boot.starter.cache)
  implementation(libs.springdoc.openapi.ui)
  compileOnly(libs.lombok)
  annotationProcessor(libs.lombok)
  testCompileOnly(libs.lombok)
  testAnnotationProcessor(libs.lombok)
  implementation(platform(libs.spring.boot.dependencies))
  runtimeOnly(libs.spring.boot.devtools)
  testImplementation(libs.spring.boot.starter.test)
}
