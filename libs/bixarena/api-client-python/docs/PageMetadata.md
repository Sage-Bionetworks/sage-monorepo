# PageMetadata

The metadata of a page.

## Properties

| Name               | Type     | Description                                 | Notes |
| ------------------ | -------- | ------------------------------------------- | ----- |
| **number**         | **int**  | The page number.                            |
| **size**           | **int**  | The number of items in a single page.       |
| **total_elements** | **int**  | Total number of elements in the result set. |
| **total_pages**    | **int**  | Total number of pages in the result set.    |
| **has_next**       | **bool** | Returns if there is a next page.            |
| **has_previous**   | **bool** | Returns if there is a previous page.        |

## Example

```python
from bixarena_api_client.models.page_metadata import PageMetadata

# TODO update the JSON string below
json = "{}"
# create an instance of PageMetadata from a JSON string
page_metadata_instance = PageMetadata.from_json(json)
# print the JSON string representation of the object
print(PageMetadata.to_json())

# convert the object into a dict
page_metadata_dict = page_metadata_instance.to_dict()
# create an instance of PageMetadata from a dict
page_metadata_from_dict = PageMetadata.from_dict(page_metadata_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
