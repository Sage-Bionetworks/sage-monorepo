# ChallengePlatformUpdateRequest

A challenge platform update request.

## Properties

| Name            | Type    | Description                                 | Notes |
| --------------- | ------- | ------------------------------------------- | ----- |
| **slug**        | **str** | The slug of the challenge platform.         |
| **name**        | **str** | The display name of the challenge platform. |
| **avatar_key**  | **str** | The avatar key                              |
| **website_url** | **str** | A URL to the website or image.              |

## Example

```python
from openchallenges_api_client_python.models.challenge_platform_update_request import ChallengePlatformUpdateRequest

# TODO update the JSON string below
json = "{}"
# create an instance of ChallengePlatformUpdateRequest from a JSON string
challenge_platform_update_request_instance = ChallengePlatformUpdateRequest.from_json(json)
# print the JSON string representation of the object
print(ChallengePlatformUpdateRequest.to_json())

# convert the object into a dict
challenge_platform_update_request_dict = challenge_platform_update_request_instance.to_dict()
# create an instance of ChallengePlatformUpdateRequest from a dict
challenge_platform_update_request_from_dict = ChallengePlatformUpdateRequest.from_dict(challenge_platform_update_request_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
