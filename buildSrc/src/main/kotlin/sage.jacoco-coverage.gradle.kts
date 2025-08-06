import org.gradle.api.tasks.testing.Test
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification

plugins {
    jacoco
}

// Configure Jacoco
configure<JacocoPluginExtension> {
    toolVersion = "0.8.13"
}

// Configure Jacoco reports
tasks.named<JacocoReport>("jacocoTestReport") {
    dependsOn(tasks.named("test"))

    reports {
        xml.required = true
        html.required = true
        csv.required = false
        html.outputLocation = layout.buildDirectory.dir("jacocoHtml")
    }
}

// Configure coverage verification
tasks.named<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
    violationRules {
        rule {
            element = "CLASS"
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0".toBigDecimal()
            }
        }
    }
}

// Add coverage tasks to check
tasks.named("check") {
    dependsOn(tasks.named("jacocoTestReport"), tasks.named("jacocoTestCoverageVerification"))
}
