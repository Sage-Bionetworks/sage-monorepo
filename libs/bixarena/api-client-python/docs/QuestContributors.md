# QuestContributors

List of contributors to a quest with their battle counts

## Properties

| Name                   | Type                                              | Description                                       | Notes |
| ---------------------- | ------------------------------------------------- | ------------------------------------------------- | ----- |
| **quest_id**           | **str**                                           | The quest identifier                              |
| **start_date**         | **datetime**                                      | Quest start date                                  |
| **end_date**           | **datetime**                                      | Quest end date                                    |
| **total_contributors** | **int**                                           | Total number of unique contributors               |
| **contributors**       | [**List[QuestContributor]**](QuestContributor.md) | Contributors ordered by battle count (descending) |

## Example

```python
from bixarena_api_client.models.quest_contributors import QuestContributors

# TODO update the JSON string below
json = "{}"
# create an instance of QuestContributors from a JSON string
quest_contributors_instance = QuestContributors.from_json(json)
# print the JSON string representation of the object
print(QuestContributors.to_json())

# convert the object into a dict
quest_contributors_dict = quest_contributors_instance.to_dict()
# create an instance of QuestContributors from a dict
quest_contributors_from_dict = QuestContributors.from_dict(quest_contributors_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
