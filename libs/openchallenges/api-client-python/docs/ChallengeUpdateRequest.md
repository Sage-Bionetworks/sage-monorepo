# ChallengeUpdateRequest

The information required to update a challenge

## Properties

| Name            | Type                                                  | Description                                    | Notes |
| --------------- | ----------------------------------------------------- | ---------------------------------------------- | ----- |
| **slug**        | **str**                                               | The unique slug of the challenge.              |
| **name**        | **str**                                               | The name of the challenge.                     |
| **headline**    | **str**                                               | The headline of the challenge.                 |
| **description** | **str**                                               | The description of the challenge.              |
| **doi**         | **str**                                               | The DOI of the challenge.                      |
| **status**      | [**ChallengeStatus**](ChallengeStatus.md)             |                                                |
| **platform_id** | **int**                                               | The unique identifier of a challenge platform. |
| **website_url** | **str**                                               | A URL to the website or image.                 |
| **avatar_url**  | **str**                                               | A URL to the website or image.                 |
| **incentives**  | [**List[ChallengeIncentive]**](ChallengeIncentive.md) |                                                |

## Example

```python
from openchallenges_api_client_python.models.challenge_update_request import ChallengeUpdateRequest

# TODO update the JSON string below
json = "{}"
# create an instance of ChallengeUpdateRequest from a JSON string
challenge_update_request_instance = ChallengeUpdateRequest.from_json(json)
# print the JSON string representation of the object
print(ChallengeUpdateRequest.to_json())

# convert the object into a dict
challenge_update_request_dict = challenge_update_request_instance.to_dict()
# create an instance of ChallengeUpdateRequest from a dict
challenge_update_request_from_dict = ChallengeUpdateRequest.from_dict(challenge_update_request_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
