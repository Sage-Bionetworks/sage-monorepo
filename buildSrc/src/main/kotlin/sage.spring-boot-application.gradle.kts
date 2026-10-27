import org.gradle.api.JavaVersion
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.api.tasks.testing.Test
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
  id("sage.java-common")
  id("org.springframework.boot")
}

// Configure testing
tasks.withType<Test>().configureEach {
  useJUnitPlatform()
  testLogging {
    events("passed", "skipped", "failed")
  }
}

// Task for unit tests only (excludes integration tests)
tasks.register<Test>("testUnit") {
  group = "verification"
  description = "Runs unit tests"

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
  description = "Runs integration tests"

  useJUnitPlatform {
    includeTags("integration")
  }

  testLogging {
    events("passed", "skipped", "failed")
  }
}

// Pinned rather than Spring Boot's default `:latest`, which Paketo has published as a
// single-architecture image (paketo-buildpacks/builder-noble-java-tiny#344). The version lives in
// a stub Dockerfile so Dependabot can bump it.
val paketoBuilderDockerfile = rootProject.layout.projectDirectory.file("buildSrc/paketo-builder/Dockerfile")
val paketoBuilderImage =
  providers.fileContents(paketoBuilderDockerfile).asText.map { dockerfile ->
    dockerfile
      .lineSequence()
      .map { it.trim() }
      .firstOrNull { it.startsWith("FROM ") }
      ?.removePrefix("FROM ")
      ?.trim()
      ?: throw GradleException("No FROM line found in ${paketoBuilderDockerfile.asFile}")
  }

// Configure standard boot build image
tasks.named<BootBuildImage>("bootBuildImage") {
  imageName.set("ghcr.io/sage-bionetworks/${project.name}-base:local")
  builder.set(paketoBuilderImage)
}
