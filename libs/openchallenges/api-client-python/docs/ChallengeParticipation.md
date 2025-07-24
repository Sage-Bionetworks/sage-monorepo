# ChallengeParticipation

An challenge participation.

## Properties

| Name                | Type                                                            | Description                                        | Notes |
| ------------------- | --------------------------------------------------------------- | -------------------------------------------------- | ----- |
| **id**              | **int**                                                         | The unique identifier of a challenge participation |
| **challenge_id**    | **int**                                                         | The unique identifier of the challenge.            |
| **organization_id** | **int**                                                         | The unique identifier of an organization           |
| **role**            | [**ChallengeParticipationRole**](ChallengeParticipationRole.md) |                                                    |

## Example

```python
from openchallenges_api_client_python.models.challenge_participation import ChallengeParticipation

# TODO update the JSON string below
json = "{}"
# create an instance of ChallengeParticipation from a JSON string
challenge_participation_instance = ChallengeParticipation.from_json(json)
# print the JSON string representation of the object
print(ChallengeParticipation.to_json())

# convert the object into a dict
challenge_participation_dict = challenge_participation_instance.to_dict()
# create an instance of ChallengeParticipation from a dict
challenge_participation_from_dict = ChallengeParticipation.from_dict(challenge_participation_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
