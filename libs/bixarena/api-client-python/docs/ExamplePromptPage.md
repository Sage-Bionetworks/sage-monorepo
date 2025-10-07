# ExamplePromptPage

A page of example prompts.

## Properties

| Name                | Type                                        | Description                                 | Notes |
| ------------------- | ------------------------------------------- | ------------------------------------------- | ----- |
| **number**          | **int**                                     | The page number.                            |
| **size**            | **int**                                     | The number of items in a single page.       |
| **total_elements**  | **int**                                     | Total number of elements in the result set. |
| **total_pages**     | **int**                                     | Total number of pages in the result set.    |
| **has_next**        | **bool**                                    | Returns if there is a next page.            |
| **has_previous**    | **bool**                                    | Returns if there is a previous page.        |
| **example_prompts** | [**List[ExamplePrompt]**](ExamplePrompt.md) | A list of example prompts.                  |

## Example

```python
from bixarena_api_client.models.example_prompt_page import ExamplePromptPage

# TODO update the JSON string below
json = "{}"
# create an instance of ExamplePromptPage from a JSON string
example_prompt_page_instance = ExamplePromptPage.from_json(json)
# print the JSON string representation of the object
print(ExamplePromptPage.to_json())

# convert the object into a dict
example_prompt_page_dict = example_prompt_page_instance.to_dict()
# create an instance of ExamplePromptPage from a dict
example_prompt_page_from_dict = ExamplePromptPage.from_dict(example_prompt_page_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
