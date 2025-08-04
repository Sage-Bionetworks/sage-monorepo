plugins {
    // Apply external plugins to be used in subprojects
    id("org.springframework.boot") version("3.4.5") apply false
    id("org.flywaydb.flyway") version("11.9.1") apply false
    id("dev.nx.gradle.project-graph") version("0.1.4") apply false
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
    // apply(plugin = "maven-publish")
    apply(plugin = "dev.nx.gradle.project-graph")

    // configure<PublishingExtension> {
    //     publications {
    //         create<MavenPublication>("maven") {
    //             artifactId = "openchallenges-api-client-java"
    //             from(components["java"])
    //         }
    //     }
    // }
}

configure(listOf(project(":openchallenges-organization-service"))) {
    apply(plugin = "org.springframework.boot")
    apply(plugin = "jacoco")
    apply(plugin = "dev.nx.gradle.project-graph")

    configure<JacocoPluginExtension> {
        toolVersion = "0.8.13"
    }

    // tasks.withType<Test>().configureEach {
    //     maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)
    // }
}
