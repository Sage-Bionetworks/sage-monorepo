import org.gradle.api.tasks.testing.Test
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification

plugins {
    jacoco
}

// Extension to allow projects to customize coverage exclusions
open class JacocoCoverageExtension {
  var exclusions: List<String> = emptyList()
  var configurationExclusions: List<String> = emptyList()
  var additionalIncludes: List<String> = emptyList()
}

// Add the extension to the project
val jacocoCoverage = extensions.create<JacocoCoverageExtension>("jacocoCoverage")

// Configure Jacoco
configure<JacocoPluginExtension> {
    toolVersion = "0.8.13"
}

// Configure coverage class files after evaluation to access extension values
afterEvaluate {
    val coverageClassFiles = if (jacocoCoverage.additionalIncludes.isNotEmpty()) {
        // If there are specific includes, create separate file trees and combine them
        files(
            fileTree(layout.buildDirectory.dir("classes/java/main")) {
                exclude(jacocoCoverage.exclusions)
            },
            *jacocoCoverage.additionalIncludes.map { pattern ->
                fileTree(layout.buildDirectory.dir("classes/java/main")) {
                    include(pattern)
                }
            }.toTypedArray()
        )
    } else {
        // Standard exclusion-only approach
        fileTree(layout.buildDirectory.dir("classes/java/main")) {
            exclude(jacocoCoverage.exclusions)
        }
    }

    // Configure Jacoco reports
    tasks.named<JacocoReport>("jacocoTestReport") {
        dependsOn(tasks.named("test"))
        classDirectories.setFrom(coverageClassFiles)

        reports {
            xml.required = true
            html.required = true
            csv.required = false
            html.outputLocation = layout.buildDirectory.dir("jacocoHtml")
        }
    }

    // Configure coverage verification
    tasks.named<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
        classDirectories.setFrom(coverageClassFiles)
        violationRules {
            rule {
                element = "CLASS"
                limit {
                    counter = "LINE"
                    value = "COVEREDRATIO"
                    minimum = "0.00".toBigDecimal() // 80% coverage threshold, TODO: restore to 80%
                }
            }
        }
    }
}

// Add coverage tasks to check
tasks.named("check") {
    dependsOn(tasks.named("jacocoTestReport"), tasks.named("jacocoTestCoverageVerification"))
}
