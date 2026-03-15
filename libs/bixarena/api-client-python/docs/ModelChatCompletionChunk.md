# ModelChatCompletionChunk

A streaming event chunk from the model chat completion

## Properties

| Name              | Type                                    | Description                               | Notes      |
| ----------------- | --------------------------------------- | ----------------------------------------- | ---------- |
| **content**       | **str**                                 | The content of a message.                 | [optional] |
| **status**        | **str**                                 | Current state of the streaming response   |
| **finish_reason** | **str**                                 | Reason the model stopped generating       | [optional] |
| **error_message** | **str**                                 | Error message describing what went wrong. | [optional] |
| **usage**         | [**ModelChatUsage**](ModelChatUsage.md) |                                           | [optional] |

## Example

```python
from bixarena_api_client.models.model_chat_completion_chunk import ModelChatCompletionChunk

# TODO update the JSON string below
json = "{}"
# create an instance of ModelChatCompletionChunk from a JSON string
model_chat_completion_chunk_instance = ModelChatCompletionChunk.from_json(json)
# print the JSON string representation of the object
print(ModelChatCompletionChunk.to_json())

# convert the object into a dict
model_chat_completion_chunk_dict = model_chat_completion_chunk_instance.to_dict()
# create an instance of ModelChatCompletionChunk from a dict
model_chat_completion_chunk_from_dict = ModelChatCompletionChunk.from_dict(model_chat_completion_chunk_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
