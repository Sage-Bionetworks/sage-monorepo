buildscript {
  repositories {
    mavenCentral()
  }
  dependencies {
    classpath(libs.flyway.database.postgresql)
  }
}

dependencies {
  annotationProcessor(libs.lombok)
  compileOnly(libs.lombok)
  implementation(project(":amp-als-app-config-data"))
  implementation(libs.findbugs.jsr305)
  implementation(libs.flyway.core)
  implementation(libs.hibernate.search.backend.elasticsearch)
  implementation(libs.hibernate.search.mapper.orm)
  implementation(libs.jackson.databind)
  implementation(libs.jackson.dataformat.yaml)
  implementation(libs.jackson.datatype.jsr310)
  implementation(libs.sagebionetworks.util)
  implementation(libs.spring.boot.starter.actuator)
  implementation(libs.spring.boot.starter.data.jpa)
  implementation(libs.spring.boot.starter.jdbc)
  implementation(libs.spring.boot.starter.security)
  implementation(libs.spring.boot.starter.validation)
  implementation(libs.spring.boot.starter.web)
  implementation(libs.springdoc.openapi.ui)
  implementation(platform(libs.spring.boot.dependencies))
  runtimeOnly(libs.flyway.database.postgresql)
  runtimeOnly(libs.postgresql)
  runtimeOnly(libs.spring.boot.devtools)
  testAnnotationProcessor(libs.lombok)
  testCompileOnly(libs.lombok)
  testImplementation(libs.spring.boot.starter.test)
  testRuntimeOnly(libs.h2database.h2)
}

tasks.withType<JavaCompile>().configureEach {
  options.encoding = "UTF-8"
}

tasks.withType<Javadoc>().configureEach {
  options.encoding = "UTF-8"
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootBuildImage>("bootBuildImage") {
  imageName.set("ghcr.io/sage-bionetworks/${project.name}-base:local")
}

flyway {
  url = "jdbc:postgresql://amp-als-postgres:8401/dataset_service"
  user = System.getenv("FLYWAY_USER") ?: "dataset_service"
  password = System.getenv("FLYWAY_PASSWORD") ?: "changeme"
  cleanDisabled = false
}