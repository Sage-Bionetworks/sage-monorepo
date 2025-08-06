plugins {
    id("sage.spring-boot-application")
    id("org.graalvm.buildtools.native")
}

group = "org.sagebionetworks.openchallenges"
version = "0.0.1-SNAPSHOT"

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(21)
  }
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
