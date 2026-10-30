plugins {
  id("sage.java-library")
  id("sage.lombok")
  id("sage.jacoco-coverage")
}

dependencies {
  implementation(platform(libs.spring.boot.dependencies))
  implementation(libs.spring.boot.starter)
  implementation(libs.spring.boot.starter.cache)
  implementation(libs.sentry.core)
  compileOnly(libs.spring.boot.starter.actuator)
  testImplementation(libs.spring.boot.starter.actuator)
  testImplementation(libs.spring.boot.starter.test)
}
