# QuestPostCreateOrUpdate

Request body for creating or updating a quest post

## Properties

| Name                  | Type          | Description                                                                              | Notes      |
| --------------------- | ------------- | ---------------------------------------------------------------------------------------- | ---------- |
| **var_date**          | **date**      | Optional display date for the post                                                       | [optional] |
| **title**             | **str**       | Post heading                                                                             |
| **description**       | **str**       | Post content text                                                                        |
| **images**            | **List[str]** | List of image URLs for the post                                                          |
| **publish_date**      | **datetime**  | Post is hidden entirely before this timestamp. Null means immediately visible.           | [optional] |
| **required_progress** | **int**       | Minimum quest-wide battle count required to unlock content. Null means no progress gate. | [optional] |
| **required_tier**     | **str**       | Minimum contributor tier required to unlock content. Null means public access.           | [optional] |

## Example

```python
from bixarena_api_client.models.quest_post_create_or_update import QuestPostCreateOrUpdate

# TODO update the JSON string below
json = "{}"
# create an instance of QuestPostCreateOrUpdate from a JSON string
quest_post_create_or_update_instance = QuestPostCreateOrUpdate.from_json(json)
# print the JSON string representation of the object
print(QuestPostCreateOrUpdate.to_json())

# convert the object into a dict
quest_post_create_or_update_dict = quest_post_create_or_update_instance.to_dict()
# create an instance of QuestPostCreateOrUpdate from a dict
quest_post_create_or_update_from_dict = QuestPostCreateOrUpdate.from_dict(quest_post_create_or_update_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
