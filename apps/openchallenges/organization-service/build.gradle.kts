buildscript {
  dependencies {
    classpath(libs.flyway.database.postgresql)
  }
}

plugins {
  alias(libs.plugins.flyway)
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
  // runtimeOnly(libs.flyway.database.postgresql)
  annotationProcessor(libs.lombok)
  compileOnly(libs.lombok)
  implementation(libs.findbugs.jsr305)
  implementation(libs.flyway.core)
  implementation(libs.flyway.database.postgresql)
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
  runtimeOnly(libs.postgresql)
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

  finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
  reports {
    xml.required = true
    html.required = true
    csv.required = false
    html.outputLocation = layout.buildDirectory.dir("jacocoHtml")
  }

  dependsOn(tasks.test)

  getClassDirectories().setFrom(
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

tasks.jacocoTestCoverageVerification {
  violationRules {
    rule {
      limit {
        minimum = "0.8".toBigDecimal()
      }
    }

    rule {
      isEnabled = false
      element = "CLASS"
      includes = listOf("org.sagebionetworks.openchallenges.*")

      limit {
        counter = "LINE"
        value = "COVEREDRATIO"
        minimum = "0.8".toBigDecimal()
      }
    }
  }
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootBuildImage>("bootBuildImage") {
  imageName.set("ghcr.io/sage-bionetworks/${project.name}-base:local")
}

flyway {
  url = "jdbc:postgresql://openchallenges-postgres:8091/organization_service"
  user = System.getenv("FLYWAY_USER") ?: "organization_service"
  password = System.getenv("FLYWAY_PASSWORD") ?: "changeme"
  cleanDisabled = false
}
