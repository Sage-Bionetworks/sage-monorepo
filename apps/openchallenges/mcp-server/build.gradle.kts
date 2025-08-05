plugins {
  alias(libs.plugins.graalvm.native)
  alias(libs.plugins.spring.boot)
  java
}

group = "org.sagebionetworks.openchallenges"
version = "0.0.1-SNAPSHOT"

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(21)
    nativeImageCapable = true
  }
}

repositories {
	mavenCentral()
  mavenLocal()
}

dependencies {
	implementation(libs.spring.ai.starter.mcp.server.webflux)
	testImplementation(libs.spring.boot.starter.test)
	testRuntimeOnly(libs.junit.platform.launcher)
  annotationProcessor(libs.lombok)
  compileOnly(libs.lombok)
  implementation(libs.openchallenges.api.client.java)
  implementation(platform(libs.spring.boot.dependencies))
  testAnnotationProcessor(libs.lombok)
  testCompileOnly(libs.lombok)
}

graalvmNative {
  binaries {
    named("main") {
      // Enable GraalVM Toolchain detection
      javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(21))
        vendor.set(JvmVendorSpec.matching("Oracle"))
      })

      imageName.set(project.name)
      buildArgs.add("--static")
      buildArgs.add("--libc=musl")
      buildArgs.add("--no-fallback")
    }
    named("test") {
      // Enable GraalVM Toolchain detection
      javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(21))
        vendor.set(JvmVendorSpec.matching("Oracle"))
      })

      buildArgs.add("-O0")
    }
  }
}

tasks.withType<Test>().configureEach {
  maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)

  useJUnitPlatform()

  testLogging {
    events("passed", "skipped", "failed")
  }
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootBuildImage>("bootBuildImage") {
  imageName.set("ghcr.io/sage-bionetworks/${project.name}-base:local")
}
