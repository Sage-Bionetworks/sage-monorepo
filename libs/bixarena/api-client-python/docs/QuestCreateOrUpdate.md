# QuestCreateOrUpdate

Request body for creating or updating a quest

## Properties

| Name                  | Type         | Description                                      | Notes |
| --------------------- | ------------ | ------------------------------------------------ | ----- |
| **quest_id**          | **str**      | Unique identifier for the quest                  |
| **title**             | **str**      | Quest display title                              |
| **description**       | **str**      | Quest narrative description                      |
| **goal**              | **int**      | Target total battle count for the quest          |
| **start_date**        | **datetime** | Quest start date                                 |
| **end_date**          | **datetime** | Quest end date                                   |
| **active_post_index** | **int**      | Index of the post to expand by default in the UI |

## Example

```python
from bixarena_api_client.models.quest_create_or_update import QuestCreateOrUpdate

# TODO update the JSON string below
json = "{}"
# create an instance of QuestCreateOrUpdate from a JSON string
quest_create_or_update_instance = QuestCreateOrUpdate.from_json(json)
# print the JSON string representation of the object
print(QuestCreateOrUpdate.to_json())

# convert the object into a dict
quest_create_or_update_dict = quest_create_or_update_instance.to_dict()
# create an instance of QuestCreateOrUpdate from a dict
quest_create_or_update_from_dict = QuestCreateOrUpdate.from_dict(quest_create_or_update_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
