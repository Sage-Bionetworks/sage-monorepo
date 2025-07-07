"""List challenges from openchallenges.io."""

import openchallenges_api_client_python
from openchallenges_api_client_python.models.challenge import Challenge
from openchallenges_api_client_python.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = openchallenges_api_client_python.Configuration(
    host="https://openchallenges.io/api/v1"
)


def get_challenge(challenge_id: int) -> Challenge:
    # Enter a context with an instance of the API client
    with openchallenges_api_client_python.ApiClient(configuration) as api_client:
        # Create an instance of the API class
        challenge_api = openchallenges_api_client_python.ChallengeApi(api_client)
        # challenge_id = 516  # int | The unique identifier of the challenge.

        try:
            # Get a challenge
            api_response = challenge_api.get_challenge(challenge_id)
            print("The response of ChallengeApi->get_challenge:\n")
            return api_response
        except ApiException as e:
            print("Exception when calling ChallengeApi->get_challenge: %s\n" % e)
            raise


if __name__ == "__main__":
    print(get_challenge(516))
