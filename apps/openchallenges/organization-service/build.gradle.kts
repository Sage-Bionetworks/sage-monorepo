plugins {
  alias(libs.plugins.spring.boot)
  jacoco
  java
}

group = "org.sagebionetworks.openchallenges"
version = "0.0.1-SNAPSHOT"

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(21)
  }
}

repositories {
	mavenCentral()
  mavenLocal()
}

dependencies {
  annotationProcessor(libs.lombok)
  compileOnly(libs.lombok)
  implementation(libs.findbugs.jsr305)
  implementation(libs.flyway.core)
  implementation(libs.flyway.mysql)
  implementation(libs.hibernate.search.backend.elasticsearch)
  implementation(libs.hibernate.search.mapper.orm)
  implementation(libs.jackson.databind)
  implementation(libs.jackson.dataformat.yaml)
  implementation(libs.jackson.datatype.jsr310)
  implementation(libs.openchallenges.app.config.data)
  implementation(libs.sagebionetworks.util)
  implementation(libs.spring.boot.starter.data.jpa)
  implementation(libs.spring.boot.starter.jdbc)
  implementation(libs.spring.boot.starter.security)
  implementation(libs.spring.boot.starter.validation)
  implementation(libs.spring.boot.starter.web)
  implementation(libs.spring.cloud.starter.config)
  implementation(libs.spring.cloud.starter.netflix.eureka.client)
  implementation(libs.spring.cloud.starter.openfeign)
  implementation(libs.spring.data.commons)
  implementation(libs.springdoc.openapi.ui)
  implementation(platform(libs.spring.boot.dependencies))
  runtimeOnly(libs.mysql.driver)
  runtimeOnly(libs.spring.boot.devtools)
  testAnnotationProcessor(libs.lombok)
  testCompileOnly(libs.lombok)
  testImplementation(libs.spring.boot.starter.test)
  testRuntimeOnly(libs.h2database.h2)
}

jacoco {
  toolVersion = "0.8.13"
}

tasks.withType<Test>().configureEach {
  maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)

  useJUnitPlatform()

  testLogging {
    events("passed", "skipped", "failed")
  }
}

tasks.jacocoTestReport {
  reports {
    xml.required.set(true)
    html.required.set(true)
    csv.required.set(false)
    html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
  }

  dependsOn(tasks.test)

  classDirectories.setFrom(
    files(classDirectories.files.map {
      fileTree(it) {
        exclude(
          "**/api/*",
          "**/configuration/EnumConverterConfiguration*.*",
          "**/configuration/Flyway*.*",
          "**/configuration/HibernateSearch*.*",
          "**/configuration/HomeController*.*",
          "**/configuration/SpringDocConfiguration*.*",
          "**/model/dto/**",
          "**/RFC3339DateFormat.*"
        )
      }
    })
  )
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootBuildImage>("bootBuildImage") {
  imageName.set("ghcr.io/sage-bionetworks/${project.name}-base:local")
}
