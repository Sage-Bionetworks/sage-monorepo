# MessageCreate

Payload used to create a message.

## Properties

| Name        | Type                              | Description               | Notes |
| ----------- | --------------------------------- | ------------------------- | ----- |
| **role**    | [**MessageRole**](MessageRole.md) |                           |
| **content** | **str**                           | The content of a message. |

## Example

```python
from bixarena_api_client.models.message_create import MessageCreate

# TODO update the JSON string below
json = "{}"
# create an instance of MessageCreate from a JSON string
message_create_instance = MessageCreate.from_json(json)
# print the JSON string representation of the object
print(MessageCreate.to_json())

# convert the object into a dict
message_create_dict = message_create_instance.to_dict()
# create an instance of MessageCreate from a dict
message_create_from_dict = MessageCreate.from_dict(message_create_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
