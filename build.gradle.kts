plugins {
  // Apply external plugins to be used in subprojects
  alias(libs.plugins.spring.boot) apply false
  alias(libs.plugins.flyway) apply false
  alias(libs.plugins.nx.gradle) apply false
  alias(libs.plugins.graalvm.native) apply false
}

allprojects {
  repositories {
    mavenCentral()
    mavenLocal()
  }
}

subprojects {
  apply(plugin = "java")

  configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
  }

  tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
      events("passed", "skipped", "failed")
    }
  }
}

// Configure specific modules
configure(listOf(project(":openchallenges-api-client-java"))) {
  apply(plugin = "maven-publish")
  apply(plugin = "dev.nx.gradle.project-graph")

  configure<PublishingExtension> {
    publications {
      create<MavenPublication>("maven") {
        artifactId = "openchallenges-api-client-java"
        from(components["java"])
      }
    }
  }
}

configure(listOf(project(":openchallenges-organization-service"))) {
  apply(plugin = "org.springframework.boot")
  apply(plugin = "jacoco")
  apply(plugin = "dev.nx.gradle.project-graph")

  configure<JacocoPluginExtension> {
      toolVersion = "0.8.13"
  }
}

configure(listOf(project(":openchallenges-mcp-server"))) {
  apply(plugin = "org.springframework.boot")
  apply(plugin = "org.graalvm.buildtools.native")
  apply(plugin = "dev.nx.gradle.project-graph")
}
