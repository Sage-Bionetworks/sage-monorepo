# install.packages("openapi", lib = ".")

devtools::install(".")

library("openapi")

api_client <- ApiClient$new(base_path = "http://localhost:8000/api/v1")
challenge_api <- ChallengeApi$new(api_client)
challenge_api$GetChallenge(1)


# > challenge_api$GetChallenge()
# Error in self$GetChallengeWithHttpInfo(challenge_id, data_file = data_file,  : 
#   Missing required parameter `challenge_id`.

# > challenge_api$GetChallenge(1)
# Error in value[[3L]](cond) : Failed to deserialize response