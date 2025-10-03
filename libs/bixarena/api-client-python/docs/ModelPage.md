# ModelPage

A page of models.

## Properties

| Name               | Type                        | Description                                 | Notes |
| ------------------ | --------------------------- | ------------------------------------------- | ----- |
| **number**         | **int**                     | The page number.                            |
| **size**           | **int**                     | The number of items in a single page.       |
| **total_elements** | **int**                     | Total number of elements in the result set. |
| **total_pages**    | **int**                     | Total number of pages in the result set.    |
| **has_next**       | **bool**                    | Returns if there is a next page.            |
| **has_previous**   | **bool**                    | Returns if there is a previous page.        |
| **models**         | [**List[Model]**](Model.md) | A list of models.                           |

## Example

```python
from bixarena_api_client.models.model_page import ModelPage

# TODO update the JSON string below
json = "{}"
# create an instance of ModelPage from a JSON string
model_page_instance = ModelPage.from_json(json)
# print the JSON string representation of the object
print(ModelPage.to_json())

# convert the object into a dict
model_page_dict = model_page_instance.to_dict()
# create an instance of ModelPage from a dict
model_page_from_dict = ModelPage.from_dict(model_page_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
