# ChallengeContribution

A challenge contribution.

## Properties

| Name                | Type                                                          | Description                                       | Notes |
| ------------------- | ------------------------------------------------------------- | ------------------------------------------------- | ----- |
| **id**              | **int**                                                       | The unique identifier of a challenge contribution |
| **challenge_id**    | **int**                                                       | The unique identifier of the challenge.           |
| **organization_id** | **int**                                                       | The unique identifier of an organization          |
| **role**            | [**ChallengeContributionRole**](ChallengeContributionRole.md) |                                                   |

## Example

```python
from openchallenges_api_client_python.models.challenge_contribution import ChallengeContribution

# TODO update the JSON string below
json = "{}"
# create an instance of ChallengeContribution from a JSON string
challenge_contribution_instance = ChallengeContribution.from_json(json)
# print the JSON string representation of the object
print(ChallengeContribution.to_json())

# convert the object into a dict
challenge_contribution_dict = challenge_contribution_instance.to_dict()
# create an instance of ChallengeContribution from a dict
challenge_contribution_from_dict = ChallengeContribution.from_dict(challenge_contribution_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
