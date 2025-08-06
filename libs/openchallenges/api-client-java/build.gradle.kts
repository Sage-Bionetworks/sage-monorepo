plugins {
    id("sage.java-library")
    id("sage.jacoco-coverage")
}

jacocoCoverage {
    // No exclusions - include all generated API client code in coverage reports
}

dependencies {
    implementation(libs.findbugs.jsr305)
    implementation(libs.spring.web)
    implementation(libs.spring.context)
    implementation(libs.jackson.core)
    implementation(libs.jackson.annotations)
    implementation(libs.jackson.databind)
    implementation(libs.jackson.jaxrs.json.provider)
    implementation(libs.jackson.databind.nullable)
    implementation(libs.jackson.datatype.jsr310)
    implementation(libs.jakarta.annotation.api)
}
