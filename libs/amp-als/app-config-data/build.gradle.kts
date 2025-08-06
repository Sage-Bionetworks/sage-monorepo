plugins {
    id("sage.spring-boot-library")
}

dependencies {
    implementation(libs.spring.boot.starter)
    implementation(libs.spring.boot.configuration.processor)
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)
}
