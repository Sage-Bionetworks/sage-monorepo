plugins {
    id("dev.nx.gradle.project-graph") version("0.1.4")
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
  developmentOnly(libs.spring.boot.devtools)
  implementation(libs.openchallenges.app.config.data)
  implementation(libs.spring.boot.starter.actuator)
  implementation(libs.spring.boot.starter.security)
  implementation(libs.spring.cloud.starter.gateway.server.webflux)
  implementation(platform(libs.spring.boot.dependencies))
  testImplementation(libs.spring.boot.starter.test)
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

// Task for unit tests only (excludes integration tests)
tasks.register<Test>("testUnit") {
  group = "verification"
  description = "Runs unit tests only"

  useJUnitPlatform {
    excludeTags("integration")
  }

  testLogging {
    events("passed", "skipped", "failed")
  }
}

// Task for integration tests only
tasks.register<Test>("testIntegration") {
  group = "verification"
  description = "Runs integration tests only"

  useJUnitPlatform {
    includeTags("integration")
  }

  testLogging {
    events("passed", "skipped", "failed")
  }
}

val coverageExclusions = listOf<String>()

val coverageIncludeExclusions = listOf<String>()

val coverageClassFiles = files(
  fileTree(layout.buildDirectory.dir("classes/java/main")) {
    exclude(coverageExclusions)
    exclude(coverageIncludeExclusions)
  },
  fileTree(layout.buildDirectory.dir("classes/java/main")) {
  }
)

tasks.jacocoTestReport {
  dependsOn(tasks.test)

  classDirectories.setFrom(coverageClassFiles)

  reports {
    xml.required = true
    html.required = true
    csv.required = false
    html.outputLocation = layout.buildDirectory.dir("jacocoHtml")
  }
}

tasks.jacocoTestCoverageVerification {
  classDirectories.setFrom(coverageClassFiles)

  violationRules {
    rule {
      element = "CLASS"
      limit {
        counter = "LINE"
        value = "COVEREDRATIO"
        minimum = "0".toBigDecimal()
      }
    }
  }
}

tasks.check {
  dependsOn(tasks.jacocoTestReport, tasks.jacocoTestCoverageVerification)
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootBuildImage>("bootBuildImage") {
  imageName.set("ghcr.io/sage-bionetworks/${project.name}-base:local")
}

allprojects {
    apply {
        plugin("dev.nx.gradle.project-graph")
    }
}