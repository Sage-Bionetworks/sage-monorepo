rootProject.name = "sage-monorepo-gradle"

// Include the OpenChallenges projects as submodules
include(":openchallenges-api-client-java")
include(":openchallenges-app-config-data")
include(":openchallenges-organization-service")
include(":openchallenges-mcp-server")
include(":openchallenges-challenge-service")

// Set the project directories
project(":openchallenges-api-client-java").projectDir = file("libs/openchallenges/api-client-java")
project(":openchallenges-app-config-data").projectDir = file("libs/openchallenges/app-config-data")
project(":openchallenges-organization-service").projectDir = file("apps/openchallenges/organization-service")
project(":openchallenges-mcp-server").projectDir = file("apps/openchallenges/mcp-server")
project(":openchallenges-challenge-service").projectDir = file("apps/openchallenges/challenge-service")
