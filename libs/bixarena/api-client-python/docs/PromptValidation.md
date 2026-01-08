# PromptValidation

## Properties

| Name              | Type      | Description                                                                                                    | Notes |
| ----------------- | --------- | -------------------------------------------------------------------------------------------------------------- | ----- |
| **prompt**        | **str**   | The original prompt that was validated                                                                         |
| **confidence**    | **float** | Confidence score indicating biomedical relevance (0.0 &#x3D; not biomedical, 1.0 &#x3D; definitely biomedical) |
| **is_biomedical** | **bool**  | Whether the prompt is considered biomedically related (confidence &gt;&#x3D; 0.5)                              |

## Example

```python
from bixarena_api_client.models.prompt_validation import PromptValidation

# TODO update the JSON string below
json = "{}"
# create an instance of PromptValidation from a JSON string
prompt_validation_instance = PromptValidation.from_json(json)
# print the JSON string representation of the object
print(PromptValidation.to_json())

# convert the object into a dict
prompt_validation_dict = prompt_validation_instance.to_dict()
# create an instance of PromptValidation from a dict
prompt_validation_from_dict = PromptValidation.from_dict(prompt_validation_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
