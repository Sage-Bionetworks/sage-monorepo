plugins {
  java
}

// Extension to allow projects to customize Lombok version
open class LombokExtension {
  var version: String = "1.18.40" // Default version from gradle/libs.versions.toml
}

// Add the extension to the project
val lombokConfig = extensions.create<LombokExtension>("lombokConfig")

// Configure Lombok dependencies after evaluation to access extension values
afterEvaluate {
  // Allow override via project property: -PlombokVersion=1.18.40
  val lombokVersion = project.findProperty("lombokVersion") as String? ?: lombokConfig.version

  dependencies {
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
    compileOnly("org.projectlombok:lombok:$lombokVersion")
    testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")
    testCompileOnly("org.projectlombok:lombok:$lombokVersion")
  }
}
