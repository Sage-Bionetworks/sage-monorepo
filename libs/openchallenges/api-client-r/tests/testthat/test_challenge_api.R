# Automatically generated by openapi-generator (https://openapi-generator.tech)
# Please update as you see appropriate

context("Test ChallengeApi")

api_instance <- ChallengeApi$new()

test_that("GetChallenge", {
  # tests for GetChallenge
  # base path: http://localhost/v1
  # Get a challenge
  # Returns the challenge specified
  # @param challenge_id integer The unique identifier of the challenge.
  # @return [Challenge]

  # uncomment below to test the operation
  #expect_equal(result, "EXPECTED_RESULT")
})

test_that("ListChallenges", {
  # tests for ListChallenges
  # base path: http://localhost/v1
  # List challenges
  # List challenges
  # @param challenge_search_query ChallengeSearchQuery The search query used to find challenges. (optional)
  # @return [ChallengesPage]

  # uncomment below to test the operation
  #expect_equal(result, "EXPECTED_RESULT")
})
