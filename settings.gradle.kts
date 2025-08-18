rootProject.name = "sagebionetworks"

// Include the AMP-ALS projects as submodules
include(":amp-als-app-config-data")
include(":amp-als-dataset-service")
include(":amp-als-user-service")

// Include the BixArena projects as submodules
include(":bixarena-api")

// Include the OpenChallenges projects as submodules
include(":openchallenges-api-client-java")
include(":openchallenges-api-gateway")
include(":openchallenges-app-config-data")
include(":openchallenges-auth-service")
include(":openchallenges-challenge-service")
include(":openchallenges-image-service")
include(":openchallenges-mcp-server")
include(":openchallenges-organization-service")

// Include the shared Java utilities
include(":sagebionetworks-util")

// Set the project directories
project(":amp-als-app-config-data").projectDir = file("libs/amp-als/app-config-data")
project(":amp-als-dataset-service").projectDir = file("apps/amp-als/dataset-service")
project(":amp-als-user-service").projectDir = file("apps/amp-als/user-service")
project(":bixarena-api").projectDir = file("apps/bixarena/api")
project(":openchallenges-api-client-java").projectDir = file("libs/openchallenges/api-client-java")
project(":openchallenges-api-gateway").projectDir = file("apps/openchallenges/api-gateway")
project(":openchallenges-app-config-data").projectDir = file("libs/openchallenges/app-config-data")
project(":openchallenges-auth-service").projectDir = file("apps/openchallenges/auth-service")
project(":openchallenges-challenge-service").projectDir = file("apps/openchallenges/challenge-service")
project(":openchallenges-image-service").projectDir = file("apps/openchallenges/image-service")
project(":openchallenges-mcp-server").projectDir = file("apps/openchallenges/mcp-server")
project(":openchallenges-organization-service").projectDir = file("apps/openchallenges/organization-service")
project(":sagebionetworks-util").projectDir = file("libs/shared/java/util")
