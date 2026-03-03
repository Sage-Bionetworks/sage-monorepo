# QuestContributor

A user who contributed to the quest

## Properties

| Name                 | Type      | Description                                        | Notes |
| -------------------- | --------- | -------------------------------------------------- | ----- |
| **username**         | **str**   | User&#39;s display name                            |
| **battle_count**     | **int**   | Number of completed battles during quest period    |
| **tier**             | **str**   | Contributor tier based on average battles per week |
| **battles_per_week** | **float** | Average battles completed per week during quest    |

## Example

```python
from bixarena_api_client.models.quest_contributor import QuestContributor

# TODO update the JSON string below
json = "{}"
# create an instance of QuestContributor from a JSON string
quest_contributor_instance = QuestContributor.from_json(json)
# print the JSON string representation of the object
print(QuestContributor.to_json())

# convert the object into a dict
quest_contributor_dict = quest_contributor_instance.to_dict()
# create an instance of QuestContributor from a dict
quest_contributor_from_dict = QuestContributor.from_dict(quest_contributor_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
