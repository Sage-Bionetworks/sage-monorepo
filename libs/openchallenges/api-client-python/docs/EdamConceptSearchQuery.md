# EdamConceptSearchQuery

An EDAM concept search query.

## Properties

| Name             | Type                                                | Description                                                            | Notes                                             |
| ---------------- | --------------------------------------------------- | ---------------------------------------------------------------------- | ------------------------------------------------- |
| **page_number**  | **int**                                             | The page number.                                                       | [optional] [default to 0]                         |
| **page_size**    | **int**                                             | The number of items in a single page.                                  | [optional] [default to 100]                       |
| **sort**         | [**EdamConceptSort**](EdamConceptSort.md)           |                                                                        | [optional] [default to EdamConceptSort.RELEVANCE] |
| **direction**    | [**EdamConceptDirection**](EdamConceptDirection.md) |                                                                        | [optional]                                        |
| **ids**          | **List[int]**                                       | An array of EDAM concept ids used to filter the results.               | [optional]                                        |
| **search_terms** | **str**                                             | A string of search terms used to filter the results.                   | [optional]                                        |
| **sections**     | [**List[EdamSection]**](EdamSection.md)             | An array of EDAM sections (sub-ontologies) used to filter the results. | [optional]                                        |

## Example

```python
from openchallenges_api_client.models.edam_concept_search_query import EdamConceptSearchQuery

# TODO update the JSON string below
json = "{}"
# create an instance of EdamConceptSearchQuery from a JSON string
edam_concept_search_query_instance = EdamConceptSearchQuery.from_json(json)
# print the JSON string representation of the object
print(EdamConceptSearchQuery.to_json())

# convert the object into a dict
edam_concept_search_query_dict = edam_concept_search_query_instance.to_dict()
# create an instance of EdamConceptSearchQuery from a dict
edam_concept_search_query_from_dict = EdamConceptSearchQuery.from_dict(edam_concept_search_query_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
