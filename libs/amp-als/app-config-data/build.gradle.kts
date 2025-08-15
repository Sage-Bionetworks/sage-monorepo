plugins {
  id("sage.spring-boot-library")
  id("sage.lombok")
}

dependencies {
  implementation(libs.spring.boot.starter)
  implementation(libs.spring.boot.configuration.processor)
}
