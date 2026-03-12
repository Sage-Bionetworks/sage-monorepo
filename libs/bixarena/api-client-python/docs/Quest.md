# Quest

A community quest with its metadata and posts

## Properties

| Name                  | Type                                | Description                                                       | Notes |
| --------------------- | ----------------------------------- | ----------------------------------------------------------------- | ----- |
| **quest_id**          | **str**                             | Unique identifier for the quest                                   |
| **title**             | **str**                             | Quest display title                                               |
| **description**       | **str**                             | Quest narrative description                                       |
| **goal**              | **int**                             | Target total battle count for the quest                           |
| **start_date**        | **datetime**                        | Quest start date                                                  |
| **end_date**          | **datetime**                        | Quest end date                                                    |
| **active_post_index** | **int**                             | Index of the post to expand by default in the UI                  |
| **total_blocks**      | **int**                             | Current total number of completed battles during the quest period |
| **posts**             | [**List[QuestPost]**](QuestPost.md) | Quest posts ordered by post index                                 |

## Example

```python
from bixarena_api_client.models.quest import Quest

# TODO update the JSON string below
json = "{}"
# create an instance of Quest from a JSON string
quest_instance = Quest.from_json(json)
# print the JSON string representation of the object
print(Quest.to_json())

# convert the object into a dict
quest_dict = quest_instance.to_dict()
# create an instance of Quest from a dict
quest_from_dict = Quest.from_dict(quest_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
