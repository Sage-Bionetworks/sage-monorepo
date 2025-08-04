rootProject.name = "sage-monorepo-gradle"

// Include the OpenChallenges projects as submodules
include(":openchallenges-api-client-java")
include(":openchallenges-organization-service")

// Set the project directories
project(":openchallenges-api-client-java").projectDir = file("libs/openchallenges/api-client-java")
project(":openchallenges-organization-service").projectDir = file("apps/openchallenges/organization-service")
