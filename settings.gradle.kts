rootProject.name = "sagebionetworks"

// Include the Agora projects as submodules
include(":agora-api-next")

// Include the BixArena projects as submodules
include(":bixarena-api-gateway")
include(":bixarena-api")
include(":bixarena-auth-service")

// Include the Model-AD projects as submodules
include(":model-ad-api-next")

// Include the QTL projects as submodules
include(":qtl-api")

// Include the shared Java utilities
include(":sagebionetworks-util")

// Set the project directories
project(":agora-api-next").projectDir = file("apps/agora/api-next")
project(":bixarena-api-gateway").projectDir = file("apps/bixarena/api-gateway")
project(":bixarena-api").projectDir = file("apps/bixarena/api")
project(":bixarena-auth-service").projectDir = file("apps/bixarena/auth-service")
project(":model-ad-api-next").projectDir = file("apps/model-ad/api-next")
project(":qtl-api").projectDir = file("apps/qtl/api")
project(":sagebionetworks-util").projectDir = file("libs/shared/java/util")
