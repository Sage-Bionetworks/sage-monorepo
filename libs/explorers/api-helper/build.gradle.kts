plugins {
  id("sage.lombok")
  id("sage.jacoco-coverage")
}

dependencies {
  implementation(platform(libs.spring.boot.dependencies))
  implementation(libs.spring.boot.starter.web)
  implementation(libs.spring.boot.starter.data.mongodb)
  testImplementation(libs.spring.boot.starter.test)
}
