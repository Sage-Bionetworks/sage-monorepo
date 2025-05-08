plugins {
	java
  alias(libs.plugins.graalvm.native)
	alias(libs.plugins.spring.dependency.management)
	alias(libs.plugins.spring.boot)
}

group = "org.sagebionetworks.openchallenges"
version = "0.0.1-SNAPSHOT"

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(21)
    nativeImageCapable = true
  }
}

// graalvmNative {
// 	toolchainDetection = true
// }

// graalvmNative {
//     // metadataRepository {
//     //     enabled.set(true)
//     // }
    
//     binaries {
//         named("main") {
//             imageName.set(project.name)
//             buildArgs.add("--no-fallback")
            
//             // Standard configuration for native image
//             // jvmArgs.add("-Xmx8g")
//         }
//     }
    
//     // Enable tool chain detection
//     // toolchainDetection.set(true)
// }

// graalvmNative {
// 	metadataRepository {
// 		enabled.set(true)
// 	}
	
// 	binaries {
// 		named("main") {
// 			imageName.set(project.name)
// 			buildArgs.add("--no-fallback")
			
// 			// Use Docker for building the native image
// 			usesContainer.set(true)
// 			containerBuild {
// 				// Use the GraalVM container image
// 				builderImage.set("ghcr.io/graalvm/native-image-community:21")
// 			}
// 		}
// 	}
// }

// graalvmNative {
// 	toolchainDetection.set(true)
// }

repositories {
	mavenCentral()
}

dependencies {
	implementation(libs.spring.ai.starter.mcp.server.webmvc)
	testImplementation(libs.spring.boot.starter.test)
	testRuntimeOnly(libs.junit.platform.launcher)
}

// graalvmNative {
//   binaries {
//     named("main") {
//       imageName.set(project.name)
//       buildArgs.add("--no-fallback")
//     }
//   }
// }

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootBuildImage>("bootBuildImage") {
  imageName.set("ghcr.io/sage-bionetworks/${project.name}-base:local")
}
