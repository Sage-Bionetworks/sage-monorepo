"""List challenges from openchallenges.io."""

import openchallenges_api_client_python
from openchallenges_api_client_python.models.challenge import Challenge
from openchallenges_api_client_python.rest import ApiException

# Defining the host is optional and defaults to http://localhost/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = openchallenges_api_client_python.Configuration(
    host="http://localhost:8000/api/v1"
)


def get_challenge(challenge_id: int) -> Challenge:
    # Enter a context with an instance of the API client
    with openchallenges_api_client_python.ApiClient(configuration) as api_client:
        # Create an instance of the API class
        challenge_api = openchallenges_api_client_python.ChallengeApi(api_client)

        try:
            # Get a challenge
            return challenge_api.get_challenge(challenge_id)
        except ApiException as e:
            print(f"Exception when calling ChallengeApi->get_challenge: {e}\n")
            raise


if __name__ == "__main__":
    print(get_challenge(516))
