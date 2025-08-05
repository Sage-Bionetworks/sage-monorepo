dependencies {
  implementation(libs.spring.boot.starter)
  implementation(libs.spring.boot.configuration.processor)
  annotationProcessor(libs.lombok)
  compileOnly(libs.lombok)
}

tasks.withType<JavaCompile>().configureEach {
  options.encoding = "UTF-8"
}

// Spring Boot library projects don't need an executable jar, so we disable it
tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
  enabled = false
}

tasks.named<Jar>("jar") {
  enabled = true
  archiveClassifier.set("") // use empty string
}
