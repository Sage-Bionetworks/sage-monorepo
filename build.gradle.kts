import org.gradle.api.plugins.JavaPluginExtension

plugins {
  // Apply external plugins to be used in subprojects
  alias(libs.plugins.nx.gradle) apply false
}

allprojects {
  repositories {
    mavenCentral()
    mavenLocal()
  }
}

subprojects {
  // Apply nx-gradle plugin to all subprojects
  apply(plugin = "dev.nx.gradle.project-graph")

  // Apply convention plugins based on project location and type
  when {
    // Library projects in libs/ folder
    projectDir.path.contains("/libs/") -> {
      when {
        // Spring Boot libraries (app-config-data projects)
        name.contains("app-config-data") -> {
          apply(plugin = "sage.spring-boot-library")
        }
        // Regular Java libraries
        else -> {
          apply(plugin = "sage.java-library")
        }
      }
    }

    // Application projects in apps/ folder
    projectDir.path.contains("/apps/") -> {
      apply(plugin = "sage.spring-boot-application")

      // Apply additional plugins based on project needs
      when {
        name.contains("organization-service") || name.contains("challenge-service") -> {
          apply(plugin = "sage.jacoco-coverage")
        }
      }
    }
  }

  // Configure group and version based on project name patterns
  when {
    name.startsWith("amp-als") -> {
      group = "org.sagebionetworks.amp.als"
      version = "0.0.1-SNAPSHOT"
    }
    name.startsWith("openchallenges") -> {
      group = "org.sagebionetworks.openchallenges"
      version = "0.0.1-SNAPSHOT"
    }
    name.startsWith("sagebionetworks") -> {
      group = "org.sagebionetworks.sagebionetworks"
      version = "0.0.1-SNAPSHOT"
    }
  }
}
