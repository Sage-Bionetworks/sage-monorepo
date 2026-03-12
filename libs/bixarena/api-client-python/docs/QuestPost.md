# QuestPost

A single quest post. Content fields (description, images) are null/empty when the caller does not meet unlock gates.

## Properties

| Name                  | Type          | Description                                                                              | Notes      |
| --------------------- | ------------- | ---------------------------------------------------------------------------------------- | ---------- |
| **post_index**        | **int**       | Display ordering index (0-based)                                                         |
| **var_date**          | **date**      | Optional display date for the post                                                       | [optional] |
| **title**             | **str**       | Post heading (always visible for published posts)                                        |
| **description**       | **str**       | Post content text. Null when the caller does not meet unlock gates.                      | [optional] |
| **images**            | **List[str]** | Image URLs for the post. Empty when the caller does not meet unlock gates.               |
| **publish_date**      | **datetime**  | Post is hidden entirely before this timestamp. Null means immediately visible.           | [optional] |
| **required_progress** | **int**       | Minimum quest-wide battle count required to unlock content. Null means no progress gate. | [optional] |
| **required_tier**     | **str**       | Minimum contributor tier required to unlock content. Null means public access.           | [optional] |

## Example

```python
from bixarena_api_client.models.quest_post import QuestPost

# TODO update the JSON string below
json = "{}"
# create an instance of QuestPost from a JSON string
quest_post_instance = QuestPost.from_json(json)
# print the JSON string representation of the object
print(QuestPost.to_json())

# convert the object into a dict
quest_post_dict = quest_post_instance.to_dict()
# create an instance of QuestPost from a dict
quest_post_from_dict = QuestPost.from_dict(quest_post_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
