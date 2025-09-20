# ChallengesPerYear

An object

## Properties

| Name                        | Type          | Description | Notes          |
| --------------------------- | ------------- | ----------- | -------------- |
| **years**                   | **List[str]** |             |
| **challenge_counts**        | **List[int]** |             |
| **undated_challenge_count** | **int**       |             | [default to 0] |

## Example

```python
from openchallenges_api_client.models.challenges_per_year import ChallengesPerYear

# TODO update the JSON string below
json = "{}"
# create an instance of ChallengesPerYear from a JSON string
challenges_per_year_instance = ChallengesPerYear.from_json(json)
# print the JSON string representation of the object
print(ChallengesPerYear.to_json())

# convert the object into a dict
challenges_per_year_dict = challenges_per_year_instance.to_dict()
# create an instance of ChallengesPerYear from a dict
challenges_per_year_from_dict = ChallengesPerYear.from_dict(challenges_per_year_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
