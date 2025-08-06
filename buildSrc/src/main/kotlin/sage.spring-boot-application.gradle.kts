import org.gradle.api.JavaVersion
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
    java
    id("org.springframework.boot")
}

// Configure Java
configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

// Configure encoding
tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

// Configure testing
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
    description = "Runs unit tests only (fast)"

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
    description = "Runs integration tests only (slow)"

    useJUnitPlatform {
        includeTags("integration")
    }

    testLogging {
        events("passed", "skipped", "failed")
    }
}

// Configure standard boot build image
tasks.named<BootBuildImage>("bootBuildImage") {
    imageName.set("ghcr.io/sage-bionetworks/${project.name}-base:local")
}
