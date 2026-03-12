# QuestPostReorder

Request body for reordering all posts in a quest

## Properties

| Name             | Type          | Description                                                                                                                | Notes |
| ---------------- | ------------- | -------------------------------------------------------------------------------------------------------------------------- | ----- |
| **post_indexes** | **List[int]** | Complete ordered list of existing post indexes. The backend reassigns post_index values 0, 1, 2, ... based on array order. |

## Example

```python
from bixarena_api_client.models.quest_post_reorder import QuestPostReorder

# TODO update the JSON string below
json = "{}"
# create an instance of QuestPostReorder from a JSON string
quest_post_reorder_instance = QuestPostReorder.from_json(json)
# print the JSON string representation of the object
print(QuestPostReorder.to_json())

# convert the object into a dict
quest_post_reorder_dict = quest_post_reorder_instance.to_dict()
# create an instance of QuestPostReorder from a dict
quest_post_reorder_from_dict = QuestPostReorder.from_dict(quest_post_reorder_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
