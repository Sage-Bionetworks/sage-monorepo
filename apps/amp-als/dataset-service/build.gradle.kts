plugins {
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
    java
}

group = "org.sagebionetworks.amp.als"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenLocal()
    maven { url = uri("https://repo.spring.io/milestone") }
    mavenCentral()
}

dependencies {
    // Spring Boot
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.jdbc)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.web)
    runtimeOnly(libs.spring.boot.devtools)
    testImplementation(libs.spring.boot.starter.test)

    // SpringDoc
    implementation(libs.springdoc.openapi.ui)

    // Jackson
    implementation(libs.jackson.databind)
    implementation(libs.jackson.dataformat.yaml)
    implementation(libs.jackson.datatype.jsr310)

    // Flyway
    implementation(libs.flyway.core)
    implementation(libs.flyway.mysql)

    // MySQL
    runtimeOnly(libs.mysql.driver)

    // Lombok
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)

    // Hibernate Search
    implementation(libs.hibernate.search.mapper.orm)
    implementation(libs.hibernate.search.backend.elasticsearch)

    // Sage Bionetworks
    implementation(libs.sage.util)

    // AMP-ALS
    implementation(libs.amp.als.app.config.data)

    // Misc
    implementation(libs.findbugs.jsr305)
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootBuildImage>("bootBuildImage") {
    imageName.set("ghcr.io/sage-bionetworks/${project.name}-base:local")
}