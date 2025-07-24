# ChallengePlatformCreateRequest

The information used to create a challenge platform

## Properties

| Name            | Type    | Description                                 | Notes |
| --------------- | ------- | ------------------------------------------- | ----- |
| **slug**        | **str** | The slug of the challenge platform.         |
| **name**        | **str** | The display name of the challenge platform. |
| **avatar_key**  | **str** | The avatar key                              |
| **website_url** | **str** | A URL to the website or image.              |

## Example

```python
from openchallenges_api_client_python.models.challenge_platform_create_request import ChallengePlatformCreateRequest

# TODO update the JSON string below
json = "{}"
# create an instance of ChallengePlatformCreateRequest from a JSON string
challenge_platform_create_request_instance = ChallengePlatformCreateRequest.from_json(json)
# print the JSON string representation of the object
print(ChallengePlatformCreateRequest.to_json())

# convert the object into a dict
challenge_platform_create_request_dict = challenge_platform_create_request_instance.to_dict()
# create an instance of ChallengePlatformCreateRequest from a dict
challenge_platform_create_request_from_dict = ChallengePlatformCreateRequest.from_dict(challenge_platform_create_request_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
