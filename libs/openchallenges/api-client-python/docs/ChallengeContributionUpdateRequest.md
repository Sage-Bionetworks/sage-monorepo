# ChallengeContributionUpdateRequest

A challenge contribution update request.

## Properties

| Name                | Type                                                          | Description                              | Notes |
| ------------------- | ------------------------------------------------------------- | ---------------------------------------- | ----- |
| **organization_id** | **int**                                                       | The unique identifier of an organization |
| **role**            | [**ChallengeContributionRole**](ChallengeContributionRole.md) |                                          |

## Example

```python
from openchallenges_api_client_python.models.challenge_contribution_update_request import ChallengeContributionUpdateRequest

# TODO update the JSON string below
json = "{}"
# create an instance of ChallengeContributionUpdateRequest from a JSON string
challenge_contribution_update_request_instance = ChallengeContributionUpdateRequest.from_json(json)
# print the JSON string representation of the object
print(ChallengeContributionUpdateRequest.to_json())

# convert the object into a dict
challenge_contribution_update_request_dict = challenge_contribution_update_request_instance.to_dict()
# create an instance of ChallengeContributionUpdateRequest from a dict
challenge_contribution_update_request_from_dict = ChallengeContributionUpdateRequest.from_dict(challenge_contribution_update_request_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
