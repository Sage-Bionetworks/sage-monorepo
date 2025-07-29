# EdamConceptsPage

A page of EDAM concepts.

## Properties

| Name               | Type                                    | Description                                 | Notes      |
| ------------------ | --------------------------------------- | ------------------------------------------- | ---------- |
| **number**         | **int**                                 | The page number.                            |
| **size**           | **int**                                 | The number of items in a single page.       |
| **total_elements** | **int**                                 | Total number of elements in the result set. |
| **total_pages**    | **int**                                 | Total number of pages in the result set.    |
| **has_next**       | **bool**                                | Returns if there is a next page.            |
| **has_previous**   | **bool**                                | Returns if there is a previous page.        |
| **edam_concepts**  | [**List[EdamConcept]**](EdamConcept.md) | A list of EDAM concepts.                    | [optional] |

## Example

```python
from openchallenges_api_client_python.models.edam_concepts_page import EdamConceptsPage

# TODO update the JSON string below
json = "{}"
# create an instance of EdamConceptsPage from a JSON string
edam_concepts_page_instance = EdamConceptsPage.from_json(json)
# print the JSON string representation of the object
print(EdamConceptsPage.to_json())

# convert the object into a dict
edam_concepts_page_dict = edam_concepts_page_instance.to_dict()
# create an instance of EdamConceptsPage from a dict
edam_concepts_page_from_dict = EdamConceptsPage.from_dict(edam_concepts_page_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
