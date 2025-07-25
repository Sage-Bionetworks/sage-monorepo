# ChallengeParticipationCreateRequest

A request to create a challenge participation for an organization, defined by a challenge ID and a role.

## Properties

| Name             | Type                                                            | Description                             | Notes |
| ---------------- | --------------------------------------------------------------- | --------------------------------------- | ----- |
| **challenge_id** | **int**                                                         | The unique identifier of the challenge. |
| **role**         | [**ChallengeParticipationRole**](ChallengeParticipationRole.md) |                                         |

## Example

```python
from openchallenges_api_client_python.models.challenge_participation_create_request import ChallengeParticipationCreateRequest

# TODO update the JSON string below
json = "{}"
# create an instance of ChallengeParticipationCreateRequest from a JSON string
challenge_participation_create_request_instance = ChallengeParticipationCreateRequest.from_json(json)
# print the JSON string representation of the object
print(ChallengeParticipationCreateRequest.to_json())

# convert the object into a dict
challenge_participation_create_request_dict = challenge_participation_create_request_instance.to_dict()
# create an instance of ChallengeParticipationCreateRequest from a dict
challenge_participation_create_request_from_dict = ChallengeParticipationCreateRequest.from_dict(challenge_participation_create_request_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
