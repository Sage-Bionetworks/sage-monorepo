# ModelChatRequest

Request payload for creating a chat completion

## Properties

| Name               | Type                                        | Description                                    | Notes |
| ------------------ | ------------------------------------------- | ---------------------------------------------- | ----- |
| **model_id**       | **str**                                     | UUID of an AI model.                           |
| **api_model_name** | **str**                                     | The model name used for API calls.             |
| **api_base**       | **str**                                     | Base URL for the model API.                    |
| **messages**       | [**List[MessageCreate]**](MessageCreate.md) | The conversation messages to send to the model |

## Example

```python
from bixarena_api_client.models.model_chat_request import ModelChatRequest

# TODO update the JSON string below
json = "{}"
# create an instance of ModelChatRequest from a JSON string
model_chat_request_instance = ModelChatRequest.from_json(json)
# print the JSON string representation of the object
print(ModelChatRequest.to_json())

# convert the object into a dict
model_chat_request_dict = model_chat_request_instance.to_dict()
# create an instance of ModelChatRequest from a dict
model_chat_request_from_dict = ModelChatRequest.from_dict(model_chat_request_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
