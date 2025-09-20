# ChallengeContributionCreateRequest

A challenge contribution to be created.

## Properties

| Name                | Type                                                          | Description                              | Notes |
| ------------------- | ------------------------------------------------------------- | ---------------------------------------- | ----- |
| **organization_id** | **int**                                                       | The unique identifier of an organization |
| **role**            | [**ChallengeContributionRole**](ChallengeContributionRole.md) |                                          |

## Example

```python
from openchallenges_api_client.models.challenge_contribution_create_request import ChallengeContributionCreateRequest

# TODO update the JSON string below
json = "{}"
# create an instance of ChallengeContributionCreateRequest from a JSON string
challenge_contribution_create_request_instance = ChallengeContributionCreateRequest.from_json(json)
# print the JSON string representation of the object
print(ChallengeContributionCreateRequest.to_json())

# convert the object into a dict
challenge_contribution_create_request_dict = challenge_contribution_create_request_instance.to_dict()
# create an instance of ChallengeContributionCreateRequest from a dict
challenge_contribution_create_request_from_dict = ChallengeContributionCreateRequest.from_dict(challenge_contribution_create_request_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
