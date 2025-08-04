plugins {
    id("dev.nx.gradle.project-graph") version("0.1.4")
	alias(libs.plugins.spring.boot)
	java
}

group = "org.sagebionetworks.agora"
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
  implementation(libs.jackson.databind)
  implementation(libs.jackson.dataformat.yaml)
  implementation(libs.jackson.datatype.jsr310)
  implementation(libs.micrometer.registry.prometheus)
  implementation(libs.opentelemetry.spring.boot.starter)
  implementation(libs.pyroscope.agent)
  implementation(libs.sage.bionetworks.util)
  implementation(libs.spring.boot.starter.actuator)
  implementation(libs.spring.boot.starter.data.mongodb)
  implementation(libs.spring.boot.starter.validation)
  implementation(libs.spring.boot.starter.web)
  implementation(libs.spring.data.commons)
  implementation(libs.springdoc.openapi.ui)
  implementation(platform(libs.opentelemetry.bom))
  implementation(platform(libs.spring.boot.dependencies))
  runtimeOnly(libs.spring.boot.devtools)
  testImplementation(libs.spring.boot.starter.test)
}

tasks.withType<JavaCompile> {
  options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
  options.encoding = "UTF-8"
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootBuildImage>("bootBuildImage") {
  imageName.set("ghcr.io/sage-bionetworks/${project.name}-base:local")
}

allprojects {
    apply {
        plugin("dev.nx.gradle.project-graph")
    }
}