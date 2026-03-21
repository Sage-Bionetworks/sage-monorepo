# ModelChatUsage

Token usage and generation parameters for the chat completion

## Properties

| Name                  | Type      | Description                                  | Notes      |
| --------------------- | --------- | -------------------------------------------- | ---------- |
| **model**             | **str**   | The model identifier that was used           | [optional] |
| **temperature**       | **float** | The sampling temperature that was used       | [optional] |
| **top_p**             | **float** | The nucleus sampling parameter that was used | [optional] |
| **max_tokens**        | **int**   | The maximum token limit that was set         | [optional] |
| **prompt_tokens**     | **int**   | Number of tokens in the prompt               |
| **completion_tokens** | **int**   | Number of tokens in the completion           |
| **total_tokens**      | **int**   | Total number of tokens used                  |

## Example

```python
from bixarena_api_client.models.model_chat_usage import ModelChatUsage

# TODO update the JSON string below
json = "{}"
# create an instance of ModelChatUsage from a JSON string
model_chat_usage_instance = ModelChatUsage.from_json(json)
# print the JSON string representation of the object
print(ModelChatUsage.to_json())

# convert the object into a dict
model_chat_usage_dict = model_chat_usage_instance.to_dict()
# create an instance of ModelChatUsage from a dict
model_chat_usage_from_dict = ModelChatUsage.from_dict(model_chat_usage_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
