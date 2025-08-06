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
