# PublicStats

Public statistics about the BixArena platform.

## Properties

| Name                 | Type    | Description                                                            | Notes |
| -------------------- | ------- | ---------------------------------------------------------------------- | ----- |
| **models_evaluated** | **int** | Total number of unique models that have been evaluated on the platform |
| **total_battles**    | **int** | Total number of battles conducted on the platform                      |
| **total_users**      | **int** | Total number of registered users on the platform                       |

## Example

```python
from bixarena_api_client.models.public_stats import PublicStats

# TODO update the JSON string below
json = "{}"
# create an instance of PublicStats from a JSON string
public_stats_instance = PublicStats.from_json(json)
# print the JSON string representation of the object
print(PublicStats.to_json())

# convert the object into a dict
public_stats_dict = public_stats_instance.to_dict()
# create an instance of PublicStats from a dict
public_stats_from_dict = PublicStats.from_dict(public_stats_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
