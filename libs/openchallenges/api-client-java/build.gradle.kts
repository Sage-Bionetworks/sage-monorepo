plugins {
  java
  `maven-publish`
}

group = "org.sagebionetworks.openchallenges"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

val jackson_version = "2.17.1"
val jackson_databind_version = "2.17.1"
val jackson_databind_nullable_version = "0.2.6"
val spring_web_version = "6.1.6"
val jakarta_annotation_version = "2.1.1"
val jodatime_version = "2.9.9"
val junit_version = "5.10.2"

dependencies {
    implementation("com.google.code.findbugs:jsr305:3.0.2")
    implementation("org.springframework:spring-web:$spring_web_version")
    implementation("org.springframework:spring-context:$spring_web_version")
    implementation("com.fasterxml.jackson.core:jackson-core:$jackson_version")
    implementation("com.fasterxml.jackson.core:jackson-annotations:$jackson_version")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jackson_databind_version")
    implementation("com.fasterxml.jackson.jaxrs:jackson-jaxrs-json-provider:$jackson_version")
    implementation("org.openapitools:jackson-databind-nullable:$jackson_databind_nullable_version")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jackson_version")
    implementation("jakarta.annotation:jakarta.annotation-api:$jakarta_annotation_version")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junit_version")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit_version")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "openchallenges-api-client-java"
            from(components["java"])
        }
    }
}

tasks.register<JavaExec>("execute") {
    mainClass.set(System.getProperty("mainClass"))
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.test {
    useJUnitPlatform()
    dependsOn("cleanTest")
    testLogging {
        events("passed", "skipped", "failed")
    }
}
