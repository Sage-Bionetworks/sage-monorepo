rootProject.name = "sage-monorepo-gradle"

// Include the OpenChallenges projects as submodules
include(":openchallenges-api-client-java")
include(":openchallenges-organization-service")
include(":openchallenges-mcp-server")

// Set the project directories
project(":openchallenges-api-client-java").projectDir = file("libs/openchallenges/api-client-java")
project(":openchallenges-organization-service").projectDir = file("apps/openchallenges/organization-service")
project(":openchallenges-mcp-server").projectDir = file("apps/openchallenges/mcp-server")
